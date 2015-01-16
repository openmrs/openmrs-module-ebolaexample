angular.module('filters', [])

    // shortens ward name
    .filter('ward', function() {
        return function (input) {
            if (!input) {
                return "";
            }
            var output = input.replace("Ward", "");
            var output = output.replace("#", "");
            var output = output.replace("Confirmed", "Conf");
            var output = output.replace("Suspect", "Susp");
            var output = output.replace("Recovery", "Rec");
            return output
        }
    })

    // shortens bed name
    .filter('bed', function() {
        return function (input) {
            if (!input) {
                return "";
            }
            var output = input.replace("Bed", "");
            var output = output.replace("#", "");
            return output
        }
    });