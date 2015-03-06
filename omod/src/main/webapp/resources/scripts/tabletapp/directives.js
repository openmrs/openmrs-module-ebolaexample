angular.module("directives", ["session", "feature-toggles"])

    .factory('BackService', ['CurrentSession', function (CurrentSession) {
        return {
            buildHandler: function ($state) {
                return function () {
                    var params = $state.params;
                    params['uuid'] = CurrentSession.getRecentWard().uuid;
                    $state.go($state.current.data.back.target, params, {reload: true});
                }
            }
        };
    }])

    .service('sidebarService', function () {
        var self = this;

        this.mask = $('#js-sidebar-service-overlay').length || false;
        this.visible = false;
        this.view = false;

        this.createMask = function () {
            return $('<div>').attr('id', 'js-sidebar-service-overlay');
        };

        this.getMask = function () {

            if (!self.mask) {
                this.mask = self.createMask();

                $('body').append(this.mask);
            }

            return this.mask;
        };

        this.setView = function (value) {
            self.view = value;
        };

        this.hide = function () {

            self.getMask().fadeOut('fast');

            self.view.fadeOut('fast');

            self.visible = false;
        };

        this.show = function () {

            self.getMask().fadeIn('fast');

            self.view.fadeIn('fast');
            self.visible = true;
        };

        this.toggle = function () {
            self[self.visible ? 'hide' : 'show']();
        };

        this.getActionElements = function () {
            return self.view.find(".actions-list li button");
        }
    })
    .directive('backButton', ['$rootScope', '$timeout', '$state', 'BackService', 'WardService', 'backButtonFilter', 'CurrentSession',
        function ($rootScope, $timeout, $state, BackService, WardService, backButtonFilter, CurrentSession) {
            return {
                link: function (scope, element, attrs) {

                   // CurrentSession.scope.setRecentWard(WardService.getBedDescriptionFor(scope.patient));
                    scope.$watch('targetState', function (oldState, state) {

                        scope.backButtonText = backButtonFilter(state.current.data.back.description || scope.getWard().display);
                        //element.find('span').text(backButtonFilter(state.current.data.back.description || scope.getWard().display));

                        element.off('click');

                        element.on('click', BackService.buildHandler(state));
                    });

                    scope.$watch('CurrentSession', function (state) {
                        scope.backButtonText = CurrentSession.getRecentWard().display;
                    });

                    scope.targetState = $state;
                }
            }
        }])
    .directive('actionSidebar', function () {
        return {
            templateUrl: 'templates/sidebar.html'
        }
    })
    .directive('actionButton', ['sidebarService', 'CurrentSession', 'FeatureToggles', '$rootScope',
        function (sidebarService, CurrentSession, FeatureToggles, $rootScope) {
        return {
            link: function (scope, element, attrs) {

                sidebarService.setView($('#js-actions-sidebar'));

                element.on('click', function () {
                    sidebarService.toggle();
                });

                scope.hasPrivilege = function (privilege) {
                    return CurrentSession.hasPrivilege(privilege);
                }

                scope.isFeatureEnabled = function (feature) {
                    return FeatureToggles.isFeatureEnabled(feature);
                }

                sidebarService.getActionElements().each(function () {
                    $(this).on('click', function () {
                        sidebarService.hide();
                    });

                });
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
    .directive('prevButton', ['$window', function ($window) {
        return {
            link: function (scope, element, attrs) {
                element.html('<button class="left small secondary">' + 'Back' + '</button>');
                element.bind('click', function () {
                    $window.history.back()
                });
            }
        }
    }])
    .directive('patientHeader', ['WardService', function (WardService) {
        return {
            templateUrl: 'templates/patient/patientHeader.html',
            transclude: true,
            link: function (scope, element, attrs) {
                scope.$watch(attrs.patient, function (patient) {
                    scope.patientInfo = patient;
                });
                scope.$watch(attrs.patientId, function (value) {
                    scope.patientId = value;
                });

                scope.bed = WardService.getBedDescriptionFor(scope.patient);
                scope.ward = WardService.getWardDescription();
                scope.$watch(attrs.ward, function (value) {
                    if(!value){
                        return;
                    }
                    scope.ward = value;
                });
                scope.$watch(attrs.bed, function(value){
                    if(!value){
                        return;
                    }
                    scope.bed = {display:value};
                })
            }
        }
    }])
    .directive('positive', [function () {
        // This does validation that a number is positive
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ctrl) {
                ctrl.$validators.positive = function (modelValue, viewValue) {
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
    }])
    .directive( 'showData', function ( $compile ) {
        return {
            scope: true,
            link: function ( scope, element, attrs ) {
                var el;

                attrs.$observe( 'template', function ( tpl ) {
                    if ( angular.isDefined( tpl ) ) {
                        tpl = "<span>" + tpl + "</span>";

                        // compile the provided template against the current scope
                        el = $compile( tpl )( scope );

                        // stupid way of emptying the element
                        element.html("");

                        // add the template content
                        element.append( el );
                    }
                });
            }
        };
    });
