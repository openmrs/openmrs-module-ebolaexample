var OPENMRS_CONTEXT_PATH = location.pathname.substring(1, location.pathname.indexOf("/", 1));

angular.module("tabletapp", ["ui.router", "uicommons.widget.select-drug", "select-drug-name", "constants",
        "prescriptions", "resources", "patients", "session", "directives", "login", "logout", "ward-service",
        "prescription-service"])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("wards");

        $stateProvider
            .state("login", {
                url: "/login",
                templateUrl: "templates/login.html"
            })
            .state("wards", {
                url: "/wards",
                templateUrl: "templates/wards.html",
                data: {
                    requiresLogin: true
                }
            })
            .state("ward", {
                url: "/wards/:uuid",
                templateUrl: "templates/ward.html",
                data: {
                    requiresLogin: true,
                    back: {
                        description: "Wards",
                        target: "wards"
                    }
                }
            })
            .state("patient", {
                url: "/patients/:patientUUID",
                templateUrl: "templates/patient.html",
                data: {
                    requiresLogin: true,
                    back: {
                        target: "ward"
                    }
                }
            })
            .state("patient.overview", {
                url: "/overview/:patientUUID/:prescriptionSuccess",
                templateUrl: "templates/patient/overview.html",
                data: {
                    requiresLogin: true,
                    activeForm: false,
                    back: {
                        target: "ward"
                    }
                }
            })
            .state("patient.overview2", {
                url: "/overview2/:patientUUID/:wardUUID",
                templateUrl: "templates/patient/overview.html",
                data: {
                    requiresLogin: true,
                    activeForm: false,
                    back: {
                        target: "ward"
                    }
                }
            })
            .state("patient.addPrescription", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescription.html",
                data: {
                    requiresLogin: true,
                    activeForm: true,
                    back: {
                        description: "Overview",
                        target: "patient.overview"
                    }
                }
            })
            .state("patient.addPrescriptionRoute", {
                url: "/addPrescriptionRoute/:conceptUUID",
                templateUrl: "templates/patient/newPrescriptionRoute.html",
                params: { concept: null },
                data: {
                    requiresLogin: true,
                    activeForm: true,
                    back: {
                        description: "Overview",
                        target: "patient.addPrescription"
                    }
                }
            })
            .state("patient.addPrescriptionDetails", {
                url: "/addPrescription",
                controller: "NewPrescriptionDetailsController",
                templateUrl: "templates/patient/prescriptionForm.html",
                params: { prescriptionInfo: null, skipPrescriptionRoute: false },
                data: {
                    requiresLogin: true,
                    activeForm: true,
                    back: {
                        description: "Overview",
                        target: "patient.overview"
                    }
                }
            })
            .state("patient.editPrescriptionDetails", {
                url: "/editPrescription/:orderUuid",
                controller: "EditPrescriptionDetailsController",
                templateUrl: "templates/patient/prescriptionForm.html",
                data: {
                    requiresLogin: true,
                    activeForm: true,
                    back: {
                        description: "Overview",
                        target: "patient.overview"
                    }
                }
            });
    })

    .run(['$rootScope', '$location', 'CurrentSession', '$state', function ($rootScope, $location, CurrentSession, $state) {
        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            var isAuthenticationRequired = toState.data && toState.data.requiresLogin,
                loggedIn = CurrentSession.getInfo();

            if (isAuthenticationRequired && !loggedIn) {
                $location.path("/login");
            }
        });
    }]);

angular.module('uicommons.common', []).

    factory('http-auth-interceptor', function($q, $rootScope) {
        return {
            responseError: function(response) {
                if (response.status === 401 || response.status === 403) {
                    $rootScope.$broadcast('event:auth-loginRequired');
                }
                return $q.reject(response);
            }
        }
    }).

    config(function($httpProvider) {
        $httpProvider.interceptors.push('http-auth-interceptor');

        // to prevent the browser from displaying a password pop-up in case of an authentication error
        $httpProvider.defaults.headers.common['Disable-WWW-Authenticate'] = 'true';
    }).

    run(['$rootScope', '$location', function ($rootScope, $location) {
        $rootScope.$on('event:auth-loginRequired', function () {
            console.log('AUTH REQUIRED');
            $location.path("/login");
        });
    }]);