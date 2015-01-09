var OPENMRS_CONTEXT_PATH = location.pathname.substring(1, location.pathname.indexOf("/", 1));

angular.module("tabletapp", ["ui.router", "uicommons.widget.select-drug", "select-drug-name", "constants",
    "prescriptions", "resources", "patients", "session", "directives", "login", "permission", "tabletPermissions"])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("/wards");

        $stateProvider
            .state("login", {
                url: "/login",
                templateUrl: "templates/login.html"
            })
            .state("wards", {
                url: "/wards",
                templateUrl: "templates/wards.html",
                data: {
//                    permissions: {
//                        only: ['loggedIn'],
//                        redirectTo: 'login'
//                    }
                }
            })
            .state("ward", {
                url: "/wards/:uuid",
                templateUrl: "templates/ward.html",
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            })
            .state("patient", {
                url: "/patients/:patientUUID",
                templateUrl: "templates/patient.html",
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            })
            .state("patient.overview", {
                url: "/overview/:patientUUID/:prescriptionSuccess",
                templateUrl: "templates/patient/overview.html",
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            })
            .state("patient.addPrescription", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescription.html",
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            })
            .state("patient.addPrescriptionRoute", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescriptionRoute.html",
                params: { concept: null },
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            })
            .state("patient.addPrescriptionDetails", {
                url: "/addPrescription",
                templateUrl: "templates/patient/prescriptionForm.html",
                params: { prescriptionInfo: null },
                data: {
                    permissions: {
                        only: ['loggedIn'],
                        redirectTo: 'login'
                    }
                }
            });
    });