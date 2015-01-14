angular.module('prescription-service', ['tabletapp'])

    .service('PrescriptionService', ['ActiveOrders', 'Constants', 'CurrentSession', 'OrderResource',
        function (ActiveOrders, Constants, CurrentSession, OrderResource) {
            function setDosing(order, orderJson) {
                if (order.freeTextInstructions) {
                    orderJson["dosingType"] = Constants.dosingType.unvalidatedFreeText;
                    orderJson["dosingInstructions"] = order.instructions;
                } else {
                    var rounds = _.filter(Object.keys(order.rounds),function (key) {
                        return order.rounds[key];
                    }).join(", ");
                    orderJson["dosingType"] = Constants.dosingType.roundBased;
                    orderJson["dose"] = order.drug.dose;
                    orderJson["doseUnits"] = order.drug.doseUnits;
                    orderJson["route"] = order.drug.route && order.drug.route.uuid;
                    orderJson["frequency"] = "";
                    orderJson["dosingInstructions"] = rounds;
                    orderJson["duration"] = order.drug.duration;
                    orderJson["durationUnits"] = Constants.durationUnits.days;
                    orderJson["asNeeded"] = order.drug.asNeeded;
                    orderJson["asNeededCondition"] = order.drug.asNeededCondition;
                }
            }

            function savePrescription(order, newState, $scope, $state) {
                if (order.form.$valid && (order.freeTextInstructions || $scope.roundSelected)) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        var sessionInfo = CurrentSession.getInfo();
                        var orderJson = {
                            "type": Constants.orderType.drugorder,
                            "patient": order.patient.uuid,
                            "drug": order.drug.uuid,
                            "encounter": encounter.uuid,
                            "careSetting": Constants.careSetting.inpatient,
                            "orderer": sessionInfo["provider"]["uuid"],
                            "concept": order.drug.concept.uuid
                        }
                        setDosing(order, orderJson);
                        new OrderResource(orderJson).$save().then(function (order) {
                            $state.params['uuid'] = CurrentSession.getRecentWard().uuid;
                            $state.params['prescriptionSuccess'] = true;
                            ActiveOrders.reload($scope, $state.params['patientUUID']);
                            $state.go(newState, $state.params);
                        }, function () {
                            $scope.serverError = true;
                        });
                    })
                } else {
                    $scope.hasErrors = true;
                }
            }

            return {
                buildSaveHandler: function($scope, $state) {
                    return function(order, newState) {
                        savePrescription(order, newState, $scope, $state);
                    }
                }
            };
        }]);
