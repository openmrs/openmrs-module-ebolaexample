angular.module("directives", [])
    .directive('backButton', function () {
        return {
            link: function (scope, element, attrs) {
                element.html('<button class="left small secondary">Back</button>');
                element.bind('click', goBack);

                function goBack() {
                    history.back();
                    scope.$apply();
                }
            }
        }
    });
