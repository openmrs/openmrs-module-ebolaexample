angular.module('iv-fluid-orders-service', ['tabletapp'])
    .service('IvFluidOrderService', ['Orders', 'Constants', 'CurrentSession', 'OrderResource',
        function (Orders, Constants, CurrentSession, OrderResource) {

            var orderJson = function (order, encounter) {
                var sessionInfo = CurrentSession.getInfo();
                var orderJson = {
                    "type": Constants.orderType.ivfluidorder,
                    "patient": order.patient.uuid,
                    "concept": order.concept.uuid,
                    "encounter": encounter.uuid,
                    "careSetting": Constants.careSetting.inpatient,
                    "orderer": sessionInfo["provider"]["uuid"],
                    "route": order.route,
                    "administrationType": order.admType.toUpperCase(),
                    "comments": order.comments
                };
                if (order.admType == 'Bolus') {
                    orderJson["bolusQuantity"] = order.bolusQuantity;
                    orderJson["bolusUnits"] = Constants.fluids.bolusUnits.uuid;
                    orderJson["bolusRate"] = order.bolusRate;
                    orderJson["bolusRateUnits"] = Constants.fluids.bolusRateUnits.uuid;
                } else if (order.admType == 'Infusion') {
                    orderJson["infusionRate"] = order.infusionRate;
                    orderJson["infusionRateNumeratorUnit"] = Constants.fluids.infusionRateNumeratorUnit.uuid;
                    orderJson["infusionRateDenominatorUnit"] = Constants.fluids.infusionRateDenominatorUnit.uuid;
                    orderJson["infusionDuration"] = order.infusionDuration;
                    orderJson["infusionDurationUnits"] = Constants.fluids.infusionDurationUnits.uuid;
                } else {
                    throw "Please select an Administration Type";
                }
                return orderJson;
            };

            var orderSuccessHandler = function ($scope, $state, newState) {
                return function (order) {
                    $state.params['uuid'] = CurrentSession.getRecentWard().uuid;
                    $state.params['ivFluidOrderSuccess'] = true;
                    Orders.reload($scope, $state.params['patientUUID']);
                    $state.go(newState, $state.params);
                }
            };

            var orderFailureHandler = function ($scope) {
                var prettyErrors = {
                    "Cannot have more than one active order for the same orderable and care setting at same time":
                        "This fluid is already prescribed. (Cannot prescribe the same formulation more than once.)"
                };
                return function (err) {
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
            };

            var saveIvFluidOrder = function (order, newState, $scope, $state) {
                if (order.form.$valid && order.admType) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        new OrderResource(orderJson(order, encounter))
                            .$save()
                            .then(orderSuccessHandler($scope, $state, newState), orderFailureHandler($scope));
                    })
                } else {
                    $scope.hasErrors = true;
                }
            };

            return {
                buildSaveHandler: function ($scope, $state) {
                    return function (order, newState) {
                        saveIvFluidOrder(order, newState, $scope, $state);
                    }
                },
                getAll: function() {
                    return Constants.fluids.list;
                },
                retrieveConcept: function (conceptUUID) {
                    return $.grep(Constants.fluids.list, function (concept) {
                        return concept.uuid == conceptUUID;
                    })[0];
                }
            };

        }]);