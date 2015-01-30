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
    .service('sidebarService', function() {
        var self = this;

        this.mask = $('#js-sidebar-service-overlay').length || false;
        this.visible = false;
        this.view = false;

        this.createMask = function() {
            return $('<div>').attr('id', 'js-sidebar-service-overlay');
        }

        this.getMask = function() {

            if(!self.mask){
                this.mask = self.createMask();

                $('body').append(this.mask);
            }

            return this.mask;
        }

        this.setView = function(value) {
            self.view = value;
        }

        this.hide = function() {

            self.getMask().fadeOut('fast');

            self.view.fadeOut('fast');

            self.visible = false;
        }

        this.show = function() {

            self.getMask().fadeIn('fast');

            self.view.fadeIn('fast');
            self.visible = true;
        }

        this.toggle = function() {
            self[self.visible ? 'hide' : 'show']();
        }

        this.getActionElements = function() {
            return self.view.find(".actions-list li button");
        }
    })
    .directive('backButton', ['$rootScope', '$timeout', '$state', 'BackService', 'backButtonFilter',
         function ($rootScope, $timeout, $state, BackService, backButtonFilter){
        return {
            link: function(scope, element, attrs) {
                scope.$watch('targetState', function(oldState, state) {
                    element.find('span').text(
                        backButtonFilter(state.current.data.back.description || scope.getWard().display)
                    );

                    element.off('click');

                    element.on('click', BackService.buildHandler(state));
                });

                scope.targetState = $state;
            }
        }
    }])
    .directive('actionSidebar',function(){
        return {
            templateUrl:'templates/sidebar.html'
        }
    })
    .directive('actionButton', ['sidebarService', '$rootScope', function(sidebarService, $rootScope) {
        return {
            link: function(scope, element, attrs) {

                sidebarService.setView($('#js-actions-sidebar'));

                element.on('click', function() {
                    sidebarService.toggle();
                });
                
                sidebarService.getActionElements().each(function(){
                    $(this).on('click', function(){
                        sidebarService.hide();
                    });

                });
            }
        }
    }])
    .directive('cancelButton', ['$state', 'BackService', function ($state, BackService) {
        return {
            link: function (scope, element, attrs) {
                element.html('<button class="left">' +
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
    }])
    .directive('positive', [ function() {
        // This does validation that a number is positive
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$validators.positive = function(modelValue, viewValue) {
                    if (ctrl.$isEmpty(modelValue)) {
                        // consider empty models to be valid
                        return true;
                    }

                    if (parseFloat(viewValue) > 0) {
                        // it is valid
                        return true;
                    }

                    // it is invalid
                    return false;
                };
            }
        }
    }]);