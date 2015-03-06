angular.module("iv-fluid-orders", ["tabletapp", "constants", "patients", "filters", "constants", "iv-fluid-orders-service"])
    .controller("NewIvFluidOrderController", ['$state', '$scope', 'IvFluidOrderService',
        function ($state, $scope, IvFluidOrderService) {
            $scope.ivFluidsConcepts = IvFluidOrderService.getAll();
        }
    ])

    .controller("NewIvFluidOrderDetailsController", ["$state", "$scope", "$window", "IvFluidOrderService", "IvFluidOrderSetup",
        function ($state, $scope, $window, IvFluidOrderService, IvFluidOrderSetup) {
            $scope.concept = IvFluidOrderService.retrieveConcept($state.params.conceptUUID);

            IvFluidOrderSetup.setupScopeConstants($scope);
            IvFluidOrderSetup.setupStandardFunctions($scope);
            IvFluidOrderSetup.setupIvFluidOrder($scope, $scope.concept, $scope.patient);

            $scope.save = IvFluidOrderService.buildSaveHandler($scope, $state);
        }
    ])

    .factory('IvFluidOrderSetup', ['Constants',
        function (Constants) {
            return {
                setupScopeConstants: function ($scope) {
                    $scope.routes = angular.copy(Constants.fluids.routeOptions);
                    $scope.bolusAmountOptions = angular.copy(Constants.fluids.bolusAmountOptions);
                    $scope.bolusRateOptions = angular.copy(Constants.fluids.bolusRateOptions);
                    $scope.bolusUnits = angular.copy(Constants.fluids.bolusUnits);
                    $scope.bolusRateUnits = angular.copy(Constants.fluids.bolusRateUnits);
                    $scope.infusionRateOptions = angular.copy(Constants.fluids.infusionRateOptions);
                    $scope.infusionDurationOptions = angular.copy(Constants.fluids.infusionDurationOptions);
                    $scope.infusionDurationUnits = angular.copy(Constants.fluids.infusionDurationUnits);
                    $scope.infusionRateNumeratorUnit = angular.copy(Constants.fluids.infusionRateNumeratorUnit);
                    $scope.infusionRateDenominatorUnit = angular.copy(Constants.fluids.infusionRateDenominatorUnit);
                },
                setupStandardFunctions: function ($scope) {
                    $scope.clearServerError = function () {
                        $scope.serverError = null;
                    };
                    $scope.infusionRateDisplay = function(rate) {
                        if (rate == 0) {
                            return 'KVO';
                        }
                        return rate + " " + Constants.fluids.infusionRateNumeratorUnit.display + "/" +
                            Constants.fluids.infusionRateDenominatorUnit.display;
                    };

                    $scope.infusionDurationDisplay = function(duration) {
                        if (duration == 0) {
                            return 'Continuous';
                        }
                        var unit = Constants.fluids.infusionDurationUnits.display;
                        if (duration != 1) {
                            unit = unit + 's';
                        }
                        return duration + " " + unit;
                    }

                },
                setupIvFluidOrder: function ($scope, concept, patient) {
                    var order = {
                        concept: concept,
                        patient: patient,
                        route: Constants.fluids.routeOptions[0].uuid,
                        admType: null
                    };
                    $scope.addOrder = order;
                }
            }
        }
    ]);

