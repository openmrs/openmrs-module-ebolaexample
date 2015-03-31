angular.module("feature-toggles", [])
    .factory("FeatureToggles", ["$http", function ($http) {
        var toggleStates = [
            {name:'enterVitals', enabled:false},
            {name:'enterSymptoms', enabled:false},
            {name:'vitalsAndSymptomsSummaryDesktop', enabled:false},
            {name:'vitalsAndSymptomsSummaryTablet', enabled:false},
            {name:'ivFluids', enabled:true},
            {name:'laboratory', enabled:false},
            {name:'clinicalNote', enabled:false}
        ];
        var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/feature-toggle";

        function init(){
            return $http.get(url).success(function(response){
                var featureToggles = response["featureToggles"];
                _.each(featureToggles, function(featureToggle){
                    setToggle(featureToggle.name, featureToggle.enabled);
                });
            });
        }

        function setToggle(key, value){
            var item = _.find(toggleStates, function (toggle) {
                return toggle.name == key
            });
            if(!item){
                //comment this to show the toggles defined in this file only.
                //toggleStates.push({name:key, enabled:value})
            }else{
                item.enabled = value;
            }
        }
        init();

        return {
            isFeatureEnabled: function(key) {
                var item = _.find(toggleStates, function (toggle) {
                    return toggle.name == key
                });
                if(!item) {
                    console.log("No feature toggle named "+ key);
                }
                return item.enabled;
            },
            turnOff: function(key){
                $http.post(url, data={'featureName':key, 'action':'turnOff'}).success(function(data){
                    setToggle(key, false);
                });
            },
            turnOn: function(key){
                $http.post(url, data={'featureName':key, 'action':'turnOn'}).success(function(data){
                    setToggle(key, true);
                });
            },
            allFeatureToggles: function(){
                return toggleStates;
            }
        };
    }])

    .controller("FeatureToggleController", ["$scope", "FeatureToggles", function ($scope, FeatureToggles) {
        $scope.allFeatureToggles = FeatureToggles.allFeatureToggles();

        $scope.turnOff = function(toggleName){
            FeatureToggles.turnOff(toggleName);
        };

        $scope.isFeatureEnabled = function(toggleName){
            return FeatureToggles.isFeatureEnabled(toggleName);
        };

        $scope.turnOn = function(toggleName){
            FeatureToggles.turnOn(toggleName);
        }

    }]);
