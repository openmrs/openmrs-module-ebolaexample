var OPENMRS_CONTEXT_PATH = location.pathname.substring(1, location.pathname.indexOf("/", 1));

angular.module("tabletapp", ["ui.router", "uicommons.widget.select-drug", "constants",
    "prescriptions", "resources", "patients"])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("/wards");

        $stateProvider
            .state("wards", {
                url: "/wards",
                templateUrl: "templates/wards.html"
            })
            .state("ward", {
                url: "/wards/:uuid",
                templateUrl: "templates/ward.html"
            })
            .state("patient", {
                url: "/patients/:patientUUID",
                templateUrl: "templates/patient.html"
            })
            .state("patient.overview", {
                url: "/overview/:patientUUID",
                templateUrl: "templates/patient/overview.html"
            })
            .state("patient.addPrescription", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescription.html"
            })
            .state("patient.addPrescriptionRoute", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescriptionRoute.html",
                params: { concept: null }
            })
            .state("patient.addPrescriptionDetails", {
                url: "/addPrescription",
                templateUrl: "templates/patient/prescriptionForm.html",
                params: { prescriptionInfo: null }
            });
    })

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource",
        function ($http, Constants, EncounterResource) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID;
            return {
                getInfo: function () {
                    if (!cachedInfo) {
                        cachedInfo = $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/session-info");
                    }
                    return cachedInfo;
                },
                getEncounter: function (patientUUID) {
                    if (cachedEncounter && cachedEncounterPatientUUID == patientUUID) {
                        return cachedEncounter;
                    }
                    cachedEncounterPatientUUID = patientUUID;
                    cachedEncounter = new EncounterResource({
                        "encounterDatetime": new Date().toJSON(),
                        "patient": patientUUID,
                        "encounterType": Constants.encounterType.ebolaInpatientFollowup
                    }).$save();
                    return cachedEncounter;
                }
            };
        }]);