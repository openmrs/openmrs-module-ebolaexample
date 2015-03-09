angular.module("session", ["ui.router", "constants", "resources", "patients", "ngCookies"])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource", "WardResource", "$cookies",
        function ($http, Constants, EncounterResource, WardResource, $cookies) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID,
                cachedWard;

            return {
                getInfo: function () {
                    if (!cachedInfo) {
                        cachedInfo = { loading: true };
                        $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/login")
                            .success(function (data) {
                                cachedInfo.loading = false;
                                angular.extend(cachedInfo, data);
                            })
                            .error(function(data) {
                                console.log("Error checking session info");
                                console.log(data);
                                cachedInfo = null;
                            });
                    }
                    return cachedInfo;
                },
                setInfo: function(info) {
                    cachedInfo = info;
                },
                clear: function() {
                    cachedInfo = null;
                },
                getEncounter: function (patientUUID) {
                    if (cachedEncounter && cachedEncounterPatientUUID == patientUUID) {
                        return cachedEncounter;
                    }
                    cachedEncounterPatientUUID = patientUUID;
                    cachedEncounter = new EncounterResource({
                        "patient": patientUUID,
                        "encounterType": Constants.encounterType.ebolaInpatientFollowup.uuid
                    }).$save();
                    return cachedEncounter;
                },
                setRecentWard: function (ward) {
                    // we can't store the entire ward object here since it makes the cookie too big and breaks jetty
                    $cookies[Constants.wardKey] = JSON.stringify({ uuid: ward.uuid, display: ward.display });
                    cachedWard = ward;
                },
                getRecentWard: function () {
                    if (cachedWard) {
                        return cachedWard;
                    }
                    else {
                        var ward = JSON.parse($cookies[Constants.wardKey]);
                        if (ward) {
                            cachedWard = WardResource.get({uuid: ward.uuid});
                            return ward;
                        } else {
                            console.log("No cached ward or cookie");
                            return null;
                        }
                    }
                },
                hasPrivilege: function(privilege) {
                    if (cachedInfo && cachedInfo.user) {
                        return _.findWhere(cachedInfo.user.roles, { display: "System Developer" }) ||
                            _.findWhere(cachedInfo.user.privileges, { display: privilege });
                    }
                    else {
                        return false;
                    }
                }
            };
        }]);
