angular.module("feature-toggles", [])
    .factory("FeatureToggles", function () {
        var toggleStates = {
            enterVitals: false,
            enterIvFluids: false
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