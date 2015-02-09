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
                clear: function() {
                    delete $cookies["session"]
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
                    // we can't store the entire ward object here since it makes the cookie too big and breaks jetty
                    $cookies[Constants.wardKey] = { uuid: ward.uuid, display: ward.display };
                    cachedWard = ward;
                },
                getRecentWard: function () {
                    if (cachedWard) {
                        return cachedWard;
                    }
                    else {
                        var ward = $cookies[Constants.wardKey];
                        if (ward) {
                            cachedWard = WardResource.get({uuid: ward.uuid});
                            return ward;
                        } else {
                            console.log("No cached ward or cookie");
                            return null;
                        }
                    }
                }
            };
        }]);
