angular.module("session", ["ui.router", "constants", "resources", "patients"])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource",
        function ($http, Constants, EncounterResource) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID,
                mostRecentWardUUIDD;

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
                    mostRecentWardUUIDD = uuid;
                },
                getRecentWard: function () {
                    return mostRecentWardUUIDD || "";
                }
            };
        }]);
