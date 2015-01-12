angular.module("directives", [])
    .directive('backButton', ['$state', 'CurrentSession', function ($state, CurrentSession) {
        return {
            link: function (scope, element, attrs) {
                element.html('<div class="tablet-navigation">' +
                                '<button class="left small">' +
                                    '<i class="fa fa-chevron-left"></i>' +
                                    $state.current.data.back.description +
                                '</button>' +
                            '</div>');
                element.bind('click', goBack);

                function goBack() {
                    var params = $state.params;
                    params['uuid'] = CurrentSession.getRecentWard();
                    $state.go($state.current.data.back.target, params);
                    scope.$apply();
                }
            }
        }
    }]);
