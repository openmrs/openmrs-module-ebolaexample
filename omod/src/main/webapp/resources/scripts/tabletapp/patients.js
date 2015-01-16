angular.module("patients", ["ui.router", "resources", "ngDialog", "constants", "session", "filters"])

    .controller("ListWardsController", [ "$scope", "WardResource", function ($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];
        $scope.loading = true;

        WardResource.query({ v: "default" }, function (response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, { type: "suspect"});
            $scope.confirmedWards = _.where(results, { type: "confirmed"});
            $scope.recoveryWards = _.where(results, { type: "recovery"});
            $scope.loading = false;
        });
    }])

    .controller("WardController", [ "$state", "$scope", "WardResource", "CurrentSession",
        function ($state, $scope, WardResource, CurrentSession) {
            $scope.loading = true;
            var wardId = $state.params.uuid;
            $scope.ward = WardResource.get({ uuid: wardId }, function (response) {
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

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog",
        "$rootScope", "Constants", "ScheduledDoseResource", "CurrentSession", "StopOrderService", "ActiveOrders",
        "PastOrders",
        function ($state, $scope, PatientResource, OrderResource, ngDialog, $rootScope, Constants,
                  ScheduledDoseResource, CurrentSession, StopOrderService, ActiveOrders, PastOrders) {
            var patientUuid = $state.params.patientUUID;
            $scope.hasErrors = false;
            $scope.patient = PatientResource.get({ uuid: patientUuid });

            $scope.activeOrders = ActiveOrders.reload($scope, patientUuid);
            $scope.$watch(ActiveOrders.get, function(newOrders) {
                $scope.activeOrders = newOrders;
            }, true);
            $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
                $scope.administeredDrug = false;
                $scope.comeFromPrescriptionForm = $state.params.prescriptionSuccess == 'true' && fromState && fromState.name == 'patient.addPrescriptionDetails';
            });

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

            $scope.loadPastOrders = function(patientUuid) {
                PastOrders.reload($scope, patientUuid);
                $scope.$watch(PastOrders.get, function(orders) {
                    $scope.pastOrders = orders;
                }, true);
            };

            $scope.showAdminister = function (order) {
                $scope.problemSaving = false;
                $scope.hasErrors = false;
                $scope.administerOrder = order;
                ngDialog.open({
                    template: "administerDialog",
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
                        $rootScope.administeredDrug = true;
                        callback();
                    }, function () {
                        $scope.problemSaving = true;
                    });
                } else {
                    $scope.hasErrors = true;
                }
            };

            $scope.stopOrder = StopOrderService.stopOrder;

            $scope.editOrder = function(order) {
                $state.go('patient.editPrescriptionDetails', { orderUuid: order.uuid });
            }

            $scope.showStoppingError = function() {
                $scope.problemStopping = true;
            }

            $scope.onStopOrderSuccess = function() {
                $scope.closeThisDialog();
                ActiveOrders.reload($scope, patientUuid);
                PastOrders.reload($scope, patientUuid);
            }

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
        }])

    .factory("ActiveOrders", ['OrderResource', function (OrderResource) {
        var cachedOrders;
        return {
            reload: function (scope, patientUuid) {
                scope.loading = true;
                return OrderResource.query({ t: "drugorder", v: 'full', patient: patientUuid }, function (response) {
                    scope.loading = false;
                    cachedOrders = response.results;
                })
            },
            get: function() {
                return cachedOrders;
            }
        }
    }])

    .factory("PastOrders", ['OrderResource', function (OrderResource) {
        var cachedOrders;
        return {
            reload: function(scope, patientUuid) {
                scope.loadingPastOrders = true;
                return OrderResource.query({ t: "drugorder", v: 'full', patient: patientUuid, expired: true}, function (response) {
                    scope.loadedPastOrders = true;
                    scope.loadingPastOrders = false;
                    cachedOrders = response;
                });
            },
            get: function() {
                return cachedOrders;
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

                        if(order.drug) {
                            orderJson["drug"] = order.drug.uuid;
                        } else {
                            orderJson["concept"] = order.concept.uuid;
                        }

                        new OrderResource(orderJson).$save().then(function (order) {
                            success();
                        }, function(response) {
                            error();
                        });
                    });
                }
            }
        }]);

