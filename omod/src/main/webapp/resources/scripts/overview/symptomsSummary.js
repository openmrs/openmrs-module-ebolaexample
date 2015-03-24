angular.module('symptomsSummary', ['feature-toggles'])
    .controller('SymptomsSummaryController', ['$scope', '$http', 'FeatureToggles',
        function ($scope, $http, FeatureToggles) {
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("symptomsSummaryDesktop");
            }

        }]
    );
