angular.module("iv-fluid-orders", ["tabletapp", "constants", "patients", "filters", "constants"])
    .controller("NewIvFluidOrderController", ['$state', '$scope', 'ConceptResource', 'Constants',
        function ($state, $scope, ConceptResource, Constants) {
            $scope.ivFluidsConcepts = Constants.fluids.list;
            $scope.$watch('concept', function (concept) {
                if (concept) {
                    $state.go('patient.addPrescriptionRoute', {concept: concept});
                }
            })
        }
    ]);

