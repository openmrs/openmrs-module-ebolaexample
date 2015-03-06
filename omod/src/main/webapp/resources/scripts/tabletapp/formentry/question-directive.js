angular.module('tabletapp')

    .directive("question", [ function() {
        return {
            restrict: 'E',
            scope: {
                question: '='
            },
            link: function($scope, element, attrs) {
                $scope.id = emr.domId(null, "question");
            },
            templateUrl: 'templates/questionTemplate.html'
        }
    }])

    // from http://stackoverflow.com/a/14519881/4212249
    .directive("checkList", [ function() {
        return {
            restrict: 'A',
            scope: {
                list: '=checkList',
                value: '@'
            },
            link: function(scope, elem, attrs) {
                if (scope.list == null) {
                    scope.list = [];
                }
                var handler = function(setup) {
                    var checked = elem.prop('checked');
                    var index = scope.list.indexOf(scope.value);

                    if (checked && index < 0) {
                        if (setup) elem.prop('checked', false);
                        else scope.list.push(scope.value);
                    } else if (!checked && index >= 0) {
                        if (setup) elem.prop('checked', true);
                        else scope.list.splice(index, 1);
                    }
                };

                var setupHandler = handler.bind(null, true);
                var changeHandler = handler.bind(null, false);

                elem.bind('change', function() {
                    scope.$apply(changeHandler);
                });
                scope.$watch('list', setupHandler, true);
            }
        }
    }])

    .controller("inputCtrl", [ '$scope', function($scope) {
        $scope.focusInput = function ($event) {
            angular.element($event.target).parent().addClass('highlight');
            var $target = angular.element($event.target);
            $target.parent().removeClass('error');
        };

        $scope.blurInput = function ($event) {
            angular.element($event.target).parent().removeClass('highlight');
            var $target = angular.element($event.target);
            $target.hasClass("ng-invalid") ? $target.parent().addClass('error') : $target.parent().removeClass('error');
        };
    }]);