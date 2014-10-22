angular.module('manageWards', ['locationService', 'ngDialog']).

    controller('ManageWardsCtrl', ['$scope', '$window', '$timeout', 'LocationService', 'ngDialog',
        function($scope, $window, $timeout, LocationService, ngDialog) {

            function reloadPage() {
                location.href = location.href;
            }

            var config = { };

            $scope.locations = [ ];

            $scope.newLocation = { };

            $scope.init = function(parentLocationUuid, tagsForAll) {
                $scope.locations = $window.locations;
                config.parentLocationUuid = parentLocationUuid;
                config.tagsForAll = tagsForAll;
            }

            $scope.locationsWithTag = function(tagName) {
                return _.filter($scope.locations, function(item) {
                    return _.findWhere(item.tags, { name: tagName });
                });
            }

            $scope.retire = function(location) {
                LocationService.retireLocation(location).then(function() {
                    reloadPage();
                });
            }

            $scope.unretire = function(location) {
                LocationService.unretireLocation(location).then(function() {
                    reloadPage();
                });
            }

            $scope.showAddDialog = function() {
                ngDialog.open({
                    template: 'addEditTemplate',
                    data: { title: emr.message('ebolaexample.manageWards.addDialogTitle') },
                    controller: ['$scope', function($scope) {
                        $scope.save = function() {
                            if ($scope.name && $scope.tagUuid) {
                                LocationService.saveLocation({
                                    name: $scope.name,
                                    description: $scope.description,
                                    parentLocation: config.parentLocationUuid,
                                    tags: _.union(config.tagsForAll, [ $scope.tagUuid ])
                                }).then(function() {
                                    reloadPage();
                                });
                            } else {
                                emr.errorMessage("name and type are required");
                            }
                        }
                    }]
                });
            }

            $scope.showEditDialog = function(location) {
                ngDialog.open({
                    template: 'addEditTemplate',
                    data: { title: emr.message('ebolaexample.manageWards.editDialogTitle') + ': ' + location.name },
                    controller: ['$scope', function($scope) {

                        $scope.editing = location;
                        $scope.name = location.name;
                        $scope.description = location.description;

                        $scope.displayTags = function(location) {
                            return _.chain(location.tags)
                                .reject(function(it) { return _.contains(config.tagsForAll, it.uuid); })
                                .pluck('display')
                                .value()
                                .join(", ");
                        }

                        $scope.save = function() {
                            if ($scope.name) {
                                LocationService.saveLocation({
                                    uuid: location.uuid,
                                    name: $scope.name,
                                    description: $scope.description
                                }).then(function() {
                                    reloadPage();
                                });
                            } else {
                                emr.errorMessage("name is required");
                            }
                        }
                    }]
                })
            }

            $scope.$on('ngDialog.opened', function (e, $dialog) {
                $dialog.find('input').first().focus()
            });

        }]);