angular.module("session", ["ui.router", "constants", "resources", "patients", "ngCookies"])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource", "$cookies",
        function ($http, Constants, EncounterResource, $cookies) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID;

            return {
                getInfo: function () {
                    return $cookies['session'];
                },
                setInfo: function(info) {
                    $cookies['session'] = info;
                },
                getEncounter: function (patientUUID) {
                    if (cachedEncounter && cachedEncounterPatientUUID == patientUUID) {
                        return cachedEncounter;
                    }
                    cachedEncounterPatientUUID = patientUUID;
                    cachedEncounter = new EncounterResource({
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
