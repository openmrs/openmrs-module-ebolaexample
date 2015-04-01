

angular.module('laboratory', ['feature-toggles', 'filters'])

    .controller('LaboratoryController', ['$scope', '$http', 'FeatureToggles',
        function ($scope, $http, FeatureToggles) {
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("laboratory");
            };
            var config = {};
            $scope.init = function(setConfig){
                config = setConfig;
                $scope.getEncounters(2);
            };
            $scope.encounters = [];

            $scope.getEncounters = function(top){
                var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/encounter/laboratory";
                url += "?patientUuid=" + config.patientUuid;
                if(!!top){
                    url  += "&top=" + top;
                    $scope.showAll = false;
                }else{
                    $scope.showAll = true;

                }
                $http.get(url).success(function(response) {
                    $scope.encounters = response["encounters"];

                    _.each($scope.encounters, function(encounter){
                        if(encounter.key == "162599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"){
                            $scope.ebolaTests = encounter.value;
                        }
                        if(encounter.key == "32AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"){
                            $scope.malariaTests = encounter.value;
                        }
                    });
                });
            };
        }]);
