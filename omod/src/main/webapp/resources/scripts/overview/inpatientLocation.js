angular.module('inpatientLocation', ['resources', 'locationService', 'ui.bootstrap'])
    .controller('InpatientLocationCtrl', ['$scope', '$http', 'LocationService', 'WardResource',
        function ($scope, $http, LocationService, WardResource) {

            var allLocations = [];
            var config = {};
            var bedTagUuid = 'c8bb459c-5e7d-11e4-9305-df58197607bd';
            $scope.bedAssignments = [];
            var currentWard = {};

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
            $scope.bedsInWard = null;
            $scope.occupied = 'Occupied';

            //not going to wait
            LocationService.getLocations({v: "default", limit: 100}).then(function (result) {
                allLocations = result;
                if (result.length == 100) {
                    LocationService.getLocations({v: "default", limit: 100, startIndex: 100}).then(function (more) {
                        allLocations = _.union(allLocations, more);
                    });
                }
            });

            $scope.init = function (setConfig) {
                config = setConfig;
                if (typeof config.currentWard !== 'undefined') {
                    for (x = 0; x < $scope.wardTypes.length; x++) {
                        if ($scope.strContains(config.currentWard.display, $scope.wardTypes[x].display)) {
                            $scope.changeToWardType = $scope.wardTypes[x];
                            break;
                        }
                    }
                    $scope.changeToWard = config.currentWard;
                    bedsForWard($scope.changeToWard);

                } else {
                    $scope.changeToWardType = null;
                    $scope.changeToWard = null;
                }

                $scope.changeToBed = typeof config.currentBed !== 'undefined' ? config.currentBed : null;
            }

            $scope.strContains = function (string, subString) {
                return string.indexOf(subString) != -1;
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
                } else {
                    bedsForWard($scope.changeToWard);
                }
            });

            $scope.$watch('bedAssignments', function (bedAssignments) {
                var beds = _.filter(allLocations, function (item) {
                    return item.parentLocation && item.parentLocation.uuid == currentWard.uuid
                        && _.some(item.tags, function (tag) {
                            return tag.uuid === bedTagUuid;
                        });
                })

                beds.sort(function (a, b) {
                    var nameA = parseInt(a.display.substr(a.display.indexOf('#') + 1));
                    var nameB = parseInt(b.display.substr(b.display.indexOf('#') + 1));
                    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
                });

                for (y = 0; y < beds.length; y++) {
                    if (isOccupied(beds[y], bedAssignments)) {
                        if (!$scope.strContains(beds[y].display, " Occupied")) {
                            beds[y].display += " Occupied";
                        }
                    }
                }

                $scope.bedsInWard = beds;
            });

            function isOccupied(bed, bedAssignments) {
                for (z = 0; z < bedAssignments.length; z++) {
                    if (bedAssignments[z].bed.uuid == bed.uuid) {
                        return true;
                    }
                }

                return false;
            }

            $scope.doTransfer = function () {
                if (isOccupied($scope.changeToBed, $scope.bedAssignments)) {
                    alert("Selected bed is already occupied");
                    return;
                }

                var url = emr.fragmentActionLink("ebolaexample", "overview/inpatientLocation", "transfer", {
                    patient: config.patientUuid,
                    location: $scope.changeToBed ? $scope.changeToBed.uuid : $scope.changeToWard.uuid
                });
                $http.post(url).success(function (data) {
                    baseURL = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "/"
                    location.href = baseURL + "openmrs/ebolaexample/ebolaOverview.page?patient=" + config.patientUuid;
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

            function bedsForWard(ward) {
                if (!ward) {
                    return [];
                }
                currentWard = ward;

                WardResource.get({uuid: ward.uuid}).$promise.then(function (bedAssignments) {
                    $scope.bedAssignments = bedAssignments.bedAssignments;
                });
            }

            $scope.changeLocationPatient = function () {
                location.href = '/' + OPENMRS_CONTEXT_PATH + '/ebolaexample/changeInPatientLocation.page?patientUuid=' + config.patientUuid;
            }
        }]);

