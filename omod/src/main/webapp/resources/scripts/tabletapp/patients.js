angular.module("patients", ["ui.router", "resources", "ngDialog", "constants", "session", "orders", "filters", "feedback-messages"])

    .controller("ListWardsController", ["$scope", "WardResource", function ($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];
        $scope.loading = true;

        WardResource.query({v: "default"}, function (response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, {type: "suspect"});
            $scope.confirmedWards = _.where(results, {type: "confirmed"});
            $scope.recoveryWards = _.where(results, {type: "recovery"});
            $scope.loading = false;
        });
    }])

    .controller("WardController", ["$state", "$scope", "WardResource", "CurrentSession",
        function ($state, $scope, WardResource, CurrentSession) {
            $scope.loading = true;
            var wardId = $state.params.uuid;
            $scope.ward = WardResource.get({uuid: wardId}, function (response) {
                CurrentSession.setRecentWard(response.toJSON());
                $scope.loading = false;
            });

            function toCamelCase(sentenceCase) {
                var out = "";
                sentenceCase.split(" ").forEach(function (el, idx) {
                    var add = el.toLowerCase();
                    out += (idx === 0 ? add : add[0].toUpperCase() + add.slice(1) + ' ');
                });
                return out;
            };

            $scope.getPatientName = function (display) {
                return toCamelCase(display.split(/ (.+)/)[1].split('-')[1]);
            };

            $scope.getPatientId = function (display) {
                return display.split(/ (.+)/)[0];
            };
        }])

    .controller("PatientController", ["$http", "$state", "$scope", "PatientResource", "OrderResource", "ngDialog",
        "$rootScope", "Constants", "ScheduledDoseResource", "CurrentSession", "StopOrderService",
        "DrugOrders", "FluidOrders", "DoseHistory", "WardResource", 'WardService', 'FeedbackMessages',
        function ($http, $state, $scope, PatientResource, OrderResource, ngDialog, $rootScope, Constants,
                  ScheduledDoseResource, CurrentSession, StopOrderService, DrugOrders, FluidOrders, DoseHistory,
                  WardResource, WardService, FeedbackMessages) {

            var patientUuid = $state.params.patientUUID;
            var wardUuid = $state.params.wardUUID;
            $scope.hasErrors = false;
            $scope.patient = PatientResource.get({uuid: patientUuid});
            $scope.backButtonText = "Some Text";

            if (wardUuid) {
                $scope.ward = WardResource.get({uuid: wardUuid}, function (response) {
                    var recentWard = response.toJSON();
                    CurrentSession.setRecentWard(recentWard);
                    $scope.bed = WardService.getBedDescriptionFor($scope.patient);
                    $scope.backButtonText = recentWard.display;
                    $scope.loading = false;
                });

                $scope.currentWard = function(){
                    return $scope.ward && $scope.ward.display;
                };
                $scope.currentBed = function(){
                    return $scope.bed && $scope.bed.display;
                };
            }

            DrugOrders.reload($scope, patientUuid);
            $scope.$watch(DrugOrders.get, function (newOrders) {
                $scope.drugOrders = newOrders;
            }, true);

            FluidOrders.reload($scope, patientUuid);
            $scope.$watch(FluidOrders.get, function (newOrders) {
                _.each(newOrders.orders, function(order){
                    $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/ivfluid-order-status?order_uuid="+order.uuid).success(function(status){
                        order.status = status['ivfluid-order']['status'].replace('_', ' ')
                    });
                });
                $scope.fluidOrders = newOrders;
            }, true);

            $scope.ivFluidStatusToggleOn = window.location.hostname == 'localhost';

            $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                $rootScope.clearMessages();
                $rootScope.comeFromPrescriptionForm = $state.params.prescriptionSuccess == 'true' && fromState && fromState.name == 'patient.addPrescriptionDetails';
            });

            $rootScope.clearMessages = function () {
                $rootScope.comeFromPrescriptionForm = null;
                $rootScope.administeredDrug = null;
                FeedbackMessages.clearMessages();
            };

            function mostRecentDose(doses) {
                // in the future, exclude future not-yet-given doses
                return doses[0];
            }

            $scope.doseHistory = DoseHistory.reload($scope, patientUuid);
            $scope.$watch(DoseHistory.get, function (newDoseHistory) {
                $scope.doseHistory = newDoseHistory;
            }, true);
            $scope.lastGiven = function (order) {
                if (!$scope.doseHistory || !order) {
                    return null;
                }
                var item = _.find($scope.doseHistory.dosesByOrder, function (it) {
                    return it.order.uuid == order.uuid
                });
                if (item) {
                    return mostRecentDose(item.doses);
                }
            };

            $scope.focusInput = function ($event) {
                angular.element($event.target).parent().addClass('highlight');
            };

            $scope.blurInput = function ($event) {
                angular.element($event.target).parent().removeClass('highlight');
            };

            $scope.getPatientId = function () {
                return $scope.patient && $scope.patient.display && $scope.patient.display.split(" ")[0];
            };

            $scope.getWard = function () {
                return CurrentSession.getRecentWard();
            };

            $scope.isActivePrescriptionsOnly = function () {
                return DrugOrders.getActiveOnly();
            };

            $scope.showActivePrescriptions = function () {
                DrugOrders.setActiveOnly(true);
            };

            $scope.showAllPrescriptions = function () {
                DrugOrders.setActiveOnly(false);
            };

            $scope.isActiveIvFluidOrdersOnly = function () {
                return FluidOrders.getActiveOnly();
            };

            $scope.showActiveIvFluidOrders = function () {
                FluidOrders.setActiveOnly(true);
            };

            $scope.showAllIvFluidOrders = function () {
                FluidOrders.setActiveOnly(false);
            };

            $scope.showAdminister = function (order) {
                $scope.problemSaving = false;
                $scope.hasErrors = false;
                $scope.administerOrder = order;
                ngDialog.open({
                    template: "administerDialog",
                    controller: "PatientController",
                    className: "ngdialog-theme-plain",
                    closeByDocument: false,
                    scope: $scope
                });
            };

            $scope.administrationStatuses = Constants.administrationStatuses;
            $scope.reasonsNotAdministered = Constants.reasonsNotAdministered;
            var needsAReason = function (administeredDose) {
                if (administeredDose && administeredDose.status) {
                    return administeredDose.status == 'PARTIAL'
                        || administeredDose.status == 'NOT_GIVEN'
                }
                return false;
            };
            $scope.needsAReason = needsAReason;
            $scope.saveAdministeredDose = function (dose, order, callback) {
                if (dose.status) {
                    var doseJSON = {
                        status: dose.status,
                        order: order.uuid
                    };
                    if (needsAReason(dose)) {
                        doseJSON['reasonNotAdministeredNonCoded'] = dose.reasonNotAdministeredNonCoded;
                    }
                    new ScheduledDoseResource(doseJSON).$save().then(function () {
                        $rootScope.clearMessages();
                        $rootScope.administeredDrug = {
                            order: order,
                            dose: dose
                        };
                        callback();
                    }, function () {
                        $scope.problemSaving = true;
                    });
                } else {
                    $scope.hasErrors = true;
                }
            };
            $scope.onSaveAdministeredDose = function () {
                $scope.closeThisDialog();
                DoseHistory.reload($scope, patientUuid);
            };

            $scope.stopOrder = StopOrderService.stopOrder;

            $scope.editOrder = function (order) {
                $state.go('patient.editPrescriptionDetails', {orderUuid: order.uuid});
            };

            $scope.showStoppingError = function () {
                $scope.problemStopping = true;
            };

            $scope.onStopOrderSuccess = function () {
                $scope.closeThisDialog();
                DrugOrders.reload($scope, patientUuid);
            };

            $scope.openStopOrderDialog = function (order) {
                $scope.order = order;
                ngDialog.open({
                    template: "stopOrderDialog",
                    controller: "PatientController",
                    className: "ngdialog-theme-plain",
                    closeByDocument: false,
                    scope: $scope
                });
            };

            $scope.openAddNewPrescriptionForm = function () {
                $state.go('patient.addPrescription');
            };

            $scope.openAddIvFluidOrderForm = function () {
                $state.go('patient.addIvFluidOrder');
            };

            $scope.hasActiveForm = function () {
                return $state.current.data && $state.current.data.activeForm;
            };

            $scope.goToLaptopPatientSummary = function () {
                location.href = emr.pageLink("ebolaexample", "ebolaOverview", {patient: patientUuid});
            }

            $scope.captureVitalsAndSymptoms = function () {
                $state.go('patient.captureVitalsAndSymptoms');
            }

            $scope.successMessages = function() {
                return FeedbackMessages.getSuccessMessages();
            }

        }])

    .factory("DoseHistory", ['DoseHistoryResource', function (DoseHistoryResource) {
        var cachedDoses;
        return {
            reload: function (scope, patientUuid) {
                scope.loadingDoseHistory = true;
                return DoseHistoryResource.query({uuid: patientUuid}, function (response) {
                    scope.loadedDoseHistory = true;
                    scope.loadingDoseHistory = false;
                    cachedDoses = response;
                });
            },
            get: function () {
                return cachedDoses;
            }
        }
    }])

    .factory('StopOrderService', ['CurrentSession', 'OrderResource', 'Constants',
        function (CurrentSession, OrderResource, Constants) {

            return {
                stopOrder: function (order, success, error) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        sessionInfo = CurrentSession.getInfo();
                        orderJson = {
                            "action": Constants.orderAction.discontinue,
                            "orderReasonNonCoded": "",
                            "type": Constants.orderType.drugorder,
                            "patient": order.patient.uuid,
                            "encounter": encounter.uuid,
                            "careSetting": Constants.careSetting.inpatient,
                            "orderer": sessionInfo["provider"]["uuid"],
                            "dosingType": Constants.dosingType.unvalidatedFreeText
                        };

                        if (order.drug) {
                            orderJson["drug"] = order.drug.uuid;
                        } else {
                            orderJson["concept"] = order.concept.uuid;
                        }

                        new OrderResource(orderJson).$save().then(function (order) {
                            success();
                        }, function (response) {
                            error();
                        });
                    });
                }
            }
        }]);

