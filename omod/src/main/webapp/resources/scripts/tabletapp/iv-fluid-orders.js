angular.module("iv-fluid-orders", ["tabletapp", "constants", "patients", "filters", "constants"])
    .controller("NewIvFluidOrderController", ['$state', '$scope', 'Constants',
        function ($state, $scope, Constants) {
            $scope.ivFluidsConcepts = Constants.fluids.list;
        }
    ])

    .controller("NewIvFluidOrderDetailsController", ["$state", "$scope", "$window", "Constants", "IvFluidOrderSetup",
        function ($state, $scope, $window, Constants, IvFluidOrderSetup) {
            function loadConcept(conceptUUID) {
                $scope.concept = $.grep(Constants.fluids.list, function (concept) {
                    return concept.uuid == conceptUUID;
                })[0];
            }

            loadConcept($state.params.conceptUUID);
            IvFluidOrderSetup.setupScopeConstants($scope);
            IvFluidOrderSetup.setupStandardFunctions($scope);
            IvFluidOrderSetup.setupIvFluidOrder($scope, $scope.concept, $scope.patient);
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

