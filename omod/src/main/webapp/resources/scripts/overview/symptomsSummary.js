angular.module('symptomsSummary', ['feature-toggles'])
    .controller('SymptomsSummaryController', ['$scope', '$http', 'FeatureToggles',
        function ($scope, $http, FeatureToggles) {
            console.log('symptoms');
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("symptomsSummaryDesktop");
            }
        }]
    );
