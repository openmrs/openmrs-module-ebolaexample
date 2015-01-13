angular.module("directives", [])
    .factory('BackService', ['CurrentSession', function (CurrentSession) {
        return {
            buildHandler: function ($state) {
                return function()
                {
                    var params = $state.params;
                    params['uuid'] = CurrentSession.getRecentWard().uuid;
                    $state.go($state.current.data.back.target, params, {reload: true});
                }
            }
        };
    }])
    .directive('backButton', ['$state', 'BackService', function ($state, BackService) {
        return {
            link: function (scope, element, attrs) {
                var description = $state.current.data.back.description || attrs.name;
                element.html('<div class="tablet-navigation">' +
                    '<button class="left small">' +
                    '<i class="fa fa-chevron-left"></i>' +
                    description +
                    '</button>' +
                    '</div>');
                element.bind('click', BackService.buildHandler($state));
            }
        }
    }])
    .directive('cancelButton', ['$state', 'BackService', function ($state, BackService) {
        return {
            link: function (scope, element, attrs) {
                element.html('<button class="left small secondary">' +
                    'Cancel' +
                    '</button>');
                element.bind('click', BackService.buildHandler($state));
            }
        }
    }])
    .directive('patientHeader', ['WardService', function(WardService) {
        return {
            templateUrl: 'templates/patient/patientHeader.html',
            transclude: true,
            link: function(scope, element, attrs)  {
                scope.$watch(attrs.patient, function(patient) {
                    scope.patientInfo = patient;
                });
                scope.$watch(attrs.patientId, function(value) {
                    scope.patientId = value;
                });
                scope.bed = WardService.getBedDescriptionFor(scope.patient);
                scope.ward = WardService.getWardDescription();
            }
        }
    }]);