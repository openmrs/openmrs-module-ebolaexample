angular.module("feature-toggles", [])
    .factory("FeatureToggles", ["$http", function ($http) {
        var toggleStates = {
            enterVitals: false,
            enterIvFluids: true,
            administerIvFluids:false
        };
        var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/feature-toggle";


        function reload(){
            $http.get(url).success(function(response){
                var featureToggles = response["featureToggles"];
                _.each(featureToggles, function(featureToggle){
                    toggleStates[featureToggle.name] = featureToggle.enabled;
                })
            });
        }
        reload();


        return {
            isFeatureEnabled: function(key) {
                return toggleStates[key];
            },
            allFeatureToggles: function(){
                return toggleStates;
            },
            turnOn : function(toggleName){
                $http.post(url, data={'featureName':toggleName, 'action':'turnOn'}).success(function(data){
                    toggleStates[toggleName] = true;
                });
            },
            turnOff : function(toggleName){
                $http.post(url, data={'featureName':toggleName, 'action':'turnOn'}).success(function(data){
                    toggleStates[toggleName] = false;
                });
            },
            reload:reload
        };
    }])
    .controller("FeatureToggleController", ["$scope", "FeatureToggles", function ($scope, FeatureToggles) {
        $scope.allFeatureToggles = _.pairs(FeatureToggles.allFeatureToggles());

        $scope.turnOff = function(toggleName){
            console.log('turn off ' + toggleName );
            FeatureToggles.turnOff(toggleName);

        };

        $scope.turnOn = function(toggleName){
            console.log('turn on ' + toggleName );
            FeatureToggles.turnOn(toggleName);
        }

    }]);