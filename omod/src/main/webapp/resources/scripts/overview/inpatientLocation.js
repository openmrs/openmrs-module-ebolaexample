angular.module('inpatientLocation', [ 'locationService', 'ui.bootstrap'])
    .controller('InpatientLocationCtrl', ['$scope', '$http', 'LocationService',
        function ($scope, $http, LocationService) {

            var allLocations = [];
            var config = {};
            var bedTagUuid = 'c8bb459c-5e7d-11e4-9305-df58197607bd';

            $scope.wardTypes = [
                {
                    display: 'Suspect',
                    uuid: '2e78a6e0-582a-11e4-af12-660e112eb3f5'
                },
                {
                    display: 'Confirmed',
                    uuid: '18789832-582a-11e4-af12-660e112eb3f5'
                },
                {
                    display: 'Recovery',
                    uuid: '16250bf4-5e71-11e4-9305-df58197607bd'
                }
            ]

            $scope.init = function (setConfig) {
                config = setConfig;
                if (typeof config.currentWard !== 'undefined') {
                    for (x = 0; x < $scope.wardTypes.length; x++) {
                        if (config.currentWard.display.indexOf($scope.wardTypes[x].display) != -1) {
                            $scope.changeToWardType = $scope.wardTypes[x];
                            break;
                        }
                    }
                    $scope.changeToWard = config.currentWard;
                } else {
                    $scope.changeToWardType = null;
                    $scope.changeToWard = null;
                }

                $scope.changeToBed = typeof config.currentBed !== 'undefined' ? config.currentBed : null;
            }

            $scope.makingChange = true;

            $scope.$watch('changeToWardType', function (changeToWardType) {
                if (typeof changeToWardType === 'undefined') {
                    $scope.changeToWard = null;
                    $scope.changeToBed = null;
                }
            });

            $scope.$watch('changeToWard', function (changeToWard) {
                if (typeof changeToWard === 'undefined') {
                    $scope.changeToBed = null;
                }
            });

            $scope.doTransfer = function () {
                var url = emr.fragmentActionLink("ebolaexample", "overview/inpatientLocation", "transfer", {
                    patient: config.patientUuid,
                    location: $scope.changeToBed ? $scope.changeToBed.uuid : $scope.changeToWard.uuid
                });
                $http.post(url).success(function (data) {
                    alert(location.href);
                    baseURL = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "/"
                    location.href = baseURL + "openmrs/ebolaexample/ebolaOverview.page?patient=" + config.patientUuid;
                    ;
                });
            }

            $scope.wardsOfType = function (wardType) {
                if (!wardType) {
                    return [];
                }
                return _.filter(allLocations, function (item) {
                    return _.some(item.tags, function (tag) {
                        return tag.uuid === wardType.uuid;
                    });
                });
            }

            $scope.bedsForWard = function (ward) {
                if (!ward) {
                    return [];
                }
                var beds = _.filter(allLocations, function (item) {
                    return item.parentLocation
                        && item.parentLocation.uuid == ward.uuid
                        && _.some(item.tags, function (tag) {
                            return tag.uuid === bedTagUuid;
                        });
                })

                return beds.sort(function (a, b) {
                    var nameA = parseInt(a.display.substr(a.display.indexOf('#') + 1));
                    var nameB = parseInt(b.display.substr(b.display.indexOf('#') + 1));
                    //alert("names are " + nameA + " " + nameB);
                    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
                });
            }

            function getBedAssignments(ward) {
                return WardResource.get({ uuid: ward.uuid });
            }

            LocationService.getLocations({v: "default", limit: 100}).then(function (result) {
                allLocations = result;
                if (result.length == 100) {
                    LocationService.getLocations({v: "default", limit: 100, startIndex: 100}).then(function (more) {
                        allLocations = _.union(allLocations, more);
                    });
                }
            });

            $scope.changeLocationPatient = function () {
                location.href = '/' + OPENMRS_CONTEXT_PATH + '/ebolaexample/changeInPatientLocation.page?patientUuid=' + config.patientUuid;
            }
        }]);

