angular.module("feature-toggles", [])
    .factory("FeatureToggles", function () {
        var toggleStates = {
            enterVitals: false
        };

        return {
            isFeatureEnabled: function(key) {
                return toggleStates[key];
            },
            setFeatureEnabled: function(key, state) {
                toggleStates[key] = state;
            }
        };
    });