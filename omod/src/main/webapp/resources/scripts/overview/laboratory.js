

angular.module('laboratory', ['feature-toggles'])

    .controller('LaboratoryController', ['$scope', '$http', 'FeatureToggles',
        function ($scope, $http, FeatureToggles) {
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("laboratory");
            };
            var config = {};
            $scope.init = function(setConfig){
                config = setConfig;
                //$scope.getEncounters(2);
            };
            $scope.encounters = [];

            $scope.getEncounters = function(top){
                var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/encounter/laboratory";
                url += "?patientUuid=" + config.patientUuid;
                url  += "&formUuid=" + "a000cb34-9ec1-4344-a1c8-f692232f6edd";
                if(!!top){
                    url  += "&top=" + top;
                    $scope.showAll = false;
                }else{
                    $scope.showAll = true;

                }
                $http.get(url).success(function(response) {
                    $scope.encounters = response["encounters"];
                });
            };
            $scope.getObsDesc = function(obs){
               return [];
            };
        }]);
