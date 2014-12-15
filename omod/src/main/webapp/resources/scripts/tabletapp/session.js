angular.module("session", ["ui.router", "constants", "resources", "patients", "ngCookies"])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource", "$cookies",
        function ($http, Constants, EncounterResource, $cookies) {
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
                },
                setRecentWard: function (uuid) {
                    $cookies[Constants.wardKey] = uuid;
                },
                getRecentWard: function () {
                    return $cookies[Constants.wardKey];
                }
            };
        }]);
