angular.module('inpatientLocation', [ 'locationService', 'ui.bootstrap' ]).
    controller('InpatientLocationCtrl', [ '$scope', '$http', 'LocationService', function($scope, $http, LocationService) {

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

        $scope.init = function(setConfig) {
            config = setConfig;
        }

        $scope.makingChange = false;

        $scope.changeToWardType = null;
        $scope.changeToWard = null;
        $scope.changeToBed = null;

        $scope.$watch('changeToWardType', function() {
            $scope.changeToWard = null;
            $scope.changeToBed = null;
        });

        $scope.$watch('changeToWard', function() {
            $scope.changeToBed = null;
        });

        $scope.doTransfer = function() {
            var url = emr.fragmentActionLink("ebolaexample", "overview/inpatientLocation", "transfer", {
                patient: config.patientUuid,
                location: $scope.changeToBed ? $scope.changeToBed.uuid : $scope.changeToWard.uuid
            });
            $http.post(url).success(function(data) {
                location.href = location.href;
            });
        }

        $scope.wardsOfType = function(wardType) {
            if (!wardType) {
                return [];
            }
            return _.filter(allLocations, function(item) {
                return _.some(item.tags, function(tag) {
                    return tag.uuid === wardType.uuid;
                });
            });
        }

        $scope.bedsForWard = function(ward) {
            if (!ward) {
                return [];
            }
            return _.filter(allLocations, function(item) {
                return item.parentLocation
                    && item.parentLocation.uuid == ward.uuid
                    && _.some(item.tags, function(tag) { return tag.uuid === bedTagUuid; });
            })
        }

        LocationService.getLocations({v:"default", limit:100}).then(function(result) {
            allLocations = result;
            if (result.length == 100) {
                LocationService.getLocations({v:"default", limit:100, startIndex:100}).then(function(more) {
                    allLocations = _.union(allLocations, more);
                });
            }
        });

    }]);

