angular.module("patients", ["ui.router", "resources", "ngDialog", "constants", "session", "filters", "feedback-messages"])

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

    .controller("PatientController", ["$state", "$scope", "PatientResource", "OrderResource", "ngDialog",
        "$rootScope", "Constants", "ScheduledDoseResource", "CurrentSession", "StopOrderService",
        "Orders", "DoseHistory", "WardResource", 'WardService', 'FeedbackMessages',
        function ($state, $scope, PatientResource, OrderResource, ngDialog, $rootScope, Constants,
                  ScheduledDoseResource, CurrentSession, StopOrderService, Orders, DoseHistory, WardResource, WardService,
                  FeedbackMessages) {

            var patientUuid = $state.params.patientUUID;
            var wardUuid = $state.params.wardUUID;
            $scope.hasErrors = false;
            $scope.patient = PatientResource.get({uuid: patientUuid});

            if (wardUuid) {
                $scope.ward = WardResource.get({uuid: wardUuid}, function (response) {
                    var recentWard = response.toJSON();
                    CurrentSession.setRecentWard(recentWard);
                    $scope.bed = WardService.getBedDescriptionFor($scope.patient);
                    $scope.loading = false;
                });

                $scope.currentWard = function(){
                    return $scope.ward && $scope.ward.display;
                };
                $scope.currentBed = function(){
                    return $scope.bed && $scope.bed.display;
                };
            }


            $scope.backButtonText = "Some Text";

            Orders.reload($scope, patientUuid);
            $scope.$watch(Orders.get, function (newOrders) {
                $scope.orders = newOrders;
            }, true);
            $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                $rootScope.clearMessages();
                $rootScope.comeFromPrescriptionForm = $state.params.prescriptionSuccess == 'true' && fromState && fromState.name == 'patient.addPrescriptionDetails';
            });

            $rootScope.clearMessages = function () {
                $rootScope.comeFromPrescriptionForm = null;
                $rootScope.administeredDrug = null;
                FeedbackMessages.clearMessages();
            }

            function mostRecentDose(doses) {
                // TODO verify that these are always given to us sorted
                // in the future, exclude future not-yet-given doses
                return doses[doses.length - 1];
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
            }

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
                return Orders.getActiveOnly();
            }

            $scope.showActivePrescriptions = function () {
                Orders.setActiveOnly(true);
            };

            $scope.showAllPrescriptions = function () {
                Orders.setActiveOnly(false);
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
            }

            $scope.stopOrder = StopOrderService.stopOrder;

            $scope.editOrder = function (order) {
                $state.go('patient.editPrescriptionDetails', {orderUuid: order.uuid});
            }

            $scope.showStoppingError = function () {
                $scope.problemStopping = true;
            }

            $scope.onStopOrderSuccess = function () {
                $scope.closeThisDialog();
                Orders.reload($scope, patientUuid);
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

            $scope.openAddNewPrescriptionForm = function () {
                $state.go('patient.addPrescription');
            }

            $scope.hasActiveForm = function () {
                return $state.current.data && $state.current.data.activeForm;
            }

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

    .factory("Orders", ['OrderResource', function (OrderResource) {
        var activeOnly = true;
        var cachedActive;
        var cachedPast;

        function sortable(date) {
            // Handle case where client and server aren't using the same timezone.
            // (I'm sure there's a better way to do this, but I have no internet now and can't search)
            if (typeof date === "string") {
                date = new Date(date);
            }
            var ret = "";
            ret += date.getUTCFullYear();
            ret += date.getUTCMonth() < 10 ? ("0" + date.getUTCMonth()) : date.getUTCMonth();
            ret += date.getUTCDate() < 10 ? ("0" + date.getUTCDate()) : date.getUTCDate();
            ret += date.getUTCHours() < 10 ? ("0" + date.getUTCHours()) : date.getUTCHours();
            ret += date.getUTCMinutes() < 10 ? ("0" + date.getUTCMinutes()) : date.getUTCMinutes();
            ret += date.getUTCSeconds() < 10 ? ("0" + date.getUTCSeconds()) : date.getUTCSeconds();
            return ret;
        }

        function decorateOrders(orders) {
            var now = sortable(new Date());
            return _.map(orders, function (order) {
                order.actualStopDate = order.dateStopped ? order.dateStopped : (
                    order.autoExpireDate && sortable(order.autoExpireDate) <= now ?
                        order.autoExpireDate :
                        null
                );
                return order;
            });
        }

        return {
            reload: function (scope, patientUuid) {
                scope.loading = true;
                OrderResource.query({t: "drugorder", v: 'full', patient: patientUuid}, function (response) {
                    scope.loading = false;
                    cachedActive = decorateOrders(response.results);

                });
                scope.loadingPastOrders = true;
                OrderResource.query({t: "drugorder", v: 'full', patient: patientUuid, expired: true}, function (response) {
                    scope.loadingPastOrders = false;
                    cachedPast = decorateOrders(response.results);
                });
            },
            setActiveOnly: function (newVal) {
                activeOnly = newVal;
            },
            getActiveOnly: function () {
                return activeOnly;
            },
            get: function () {
                var orders;
                if (activeOnly) {
                    orders = cachedActive;
                } else {
                    orders = _.union(cachedActive, cachedPast);
                }
                var grouped;
                if (orders) {
                    orders = _.sortBy(orders, 'dateActivated').reverse();
                    var drugs = _.map(_.uniq(_.pluck(_.pluck(orders, 'drug'), 'uuid')), function (uuid) {
                        return {
                            uuid: uuid,
                            orders: []
                        }
                    });
                    _.each(orders, function (order) {
                        _.findWhere(drugs, {uuid: order.drug.uuid}).orders.push(order);
                    });
                    grouped = _.sortBy(drugs, function (group) {
                        return group.orders[0].dateActivated;
                    }).reverse();
                }
                return {
                    activeOnly: activeOnly,
                    none: orders && (orders.length == 0),
                    groupedOrders: grouped
                };
            }
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

