angular.module("session", ["ui.router", "constants", "resources", "patients", "ngCookies"])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource", "WardResource", "$cookies",
        function ($http, Constants, EncounterResource, WardResource, $cookies) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID,
                cachedWard;

            return {
                getInfo: function () {
                    var sessionInfo = $cookies['session'];
                    if(sessionInfo) {
                        return JSON.parse(sessionInfo);
                    }
                    return sessionInfo;
                },
                setInfo: function(info) {
                    $cookies['session'] = JSON.stringify(info);
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
                setRecentWard: function (ward) {
                    // I haven't checked why we're storing this in a cookie (that code was already there) but I am
                    // changing it to store just the UUID instead of the whole ward, since it was breaking jetty by
                    // causing us to have a too-big header: $cookies[Constants.wardKey] = JSON.stringify(ward);
                    $cookies[Constants.wardKey] = ward.uuid;
                    cachedWard = ward;
                },
                getRecentWard: function () {
                    if (cachedWard) {
                        return cachedWard;
                    }
                    else {
                        var wardUuid = $cookies[Constants.wardKey];
                        if (wardUuid) {
                            cachedWard = WardResource.get({uuid: wardUuid});
                            return cachedWard;
                        } else {
                            return null;
                        }
                    }
                }
            };
        }]);
