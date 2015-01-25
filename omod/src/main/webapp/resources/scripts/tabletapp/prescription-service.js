angular.module('prescription-service', ['tabletapp'])
    .service('PrescriptionService', ['Orders', 'Constants', 'CurrentSession', 'OrderResource',
        function (Orders, Constants, CurrentSession, OrderResource) {
            var myFunc = function() {console.log('a')};

            var orderJson = function(order, encounter) {
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
                if (order.freeTextInstructions) {
                    orderJson["dosingType"] = Constants.dosingType.unvalidatedFreeText;
                    orderJson["dosingInstructions"] = order.dosingInstructions;
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
                return orderJson;
            }

            var orderSuccessHandler = function($scope, $state, newState) {
                return function(order) {
                    $state.params['uuid'] = CurrentSession.getRecentWard().uuid;
                    $state.params['prescriptionSuccess'] = true;
                    Orders.reload($scope, $state.params['patientUUID']);
                    $state.go(newState, $state.params);
                }
            }
            var orderFailureHandler = function($scope) {
                var prettyErrors = {
                    "Cannot have more than one active order for the same orderable and care setting at same time":
                        "This drug is already prescribed. (Cannot prescribe the same formulation more than once.)"
                };
                return function(err) {
                    if (err.data && err.data.error && err.data.error.message) {
                        err = err.data.error.message;
                        if (err in prettyErrors) {
                            $scope.serverError = prettyErrors[err];
                        } else {
                            $scope.serverError = err;
                        }
                    } else {
                        $scope.serverError = "There was an error saving this prescription";
                    }
                }
            }
            var savePrescription = function(order, newState, $scope, $state) {
                if (order.form.$valid && (order.freeTextInstructions || $scope.roundSelected)) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        new OrderResource(orderJson(order, encounter))
                            .$save()
                            .then(orderSuccessHandler($scope, $state, newState), orderFailureHandler($scope));
                    })
                } else {
                    $scope.hasErrors = true;
                }
            }
            var updatePrescription = function(order, newState, $scope, $state) {
                if (order.form.$valid && (order.freeTextInstructions || $scope.roundSelected)) {
                    var orderJsonF = orderJson;
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        var orderJson = orderJsonF(order, encounter);
                        orderJson['previousOrder'] = order.uuid;
                        orderJson['action'] = Constants.orderAction.revise,
                            new OrderResource(orderJson)
                            .$save()
                            .then(orderSuccessHandler($scope, $state, newState), orderFailureHandler($scope));
                    })
                } else {
                    $scope.hasErrors = true;
                }
            }

            return {
                buildSaveHandler: function ($scope, $state) {
                    return function (order, newState) {
                        savePrescription(order, newState, $scope, $state);
                    }
                },
                buildUpdateHandler: function ($scope, $state) {
                    return function (order, newState) {
                        updatePrescription(order, newState, $scope, $state);
                    }
                },
                formOrderFromResponse: function (orderJson) {
                    var order = {
                        drug: { route: {} }
                    };
                    order.uuid = orderJson["uuid"];
                    order.freeTextInstructions = orderJson["dosingType"] == Constants.dosingType.unvalidatedFreeText;
                    order.drug.concept = orderJson["concept"];
                    if(orderJson["drug"]) {
                        order.drug.uuid = orderJson["drug"]["uuid"];
                        order.drug.display = orderJson["drug"]["display"];
                    }
                    if (order.freeTextInstructions) {
                        order.dosingInstructions = orderJson["dosingInstructions"];
                    } else {
                        var rounds = {}
                        if (orderJson["dosingInstructions"]) {
                            _.each(orderJson["dosingInstructions"].split(", "), function (v, i) {
                                rounds[v] = true;
                            });
                        }
                        order.rounds = rounds;
                        order.drug.route = orderJson["route"];
                        order.drug.dose = orderJson["dose"];
                        order.drug.doseUnits = orderJson["doseUnits"]["uuid"];
                        order.drug.duration = orderJson["duration"];
                        order.drug.asNeeded = orderJson["asNeeded"];
                        order.drug.asNeededCondition = orderJson["asNeededCondition"];
                    }
                    return order;
                }
            };
        }]);