angular.module('formentry', ['uicommons.filters', 'ui.bootstrap']).

    filter('format', function() {
        function formatSingle(value) {
            if (value.label) {
                return value.label;
            } else {
                return value;
            }
        }

        return function(screen) {
            if (!screen.value) {
                return "";
            }
            else if (_.isArray(screen.value)) {
                return _.map(screen.value, formatSingle).join(", ");
            }
            else {
                return formatSingle(screen.value);
            }
        }
    }).

    directive('includeScreen', [ function() {
        return {
            restrict: 'E',
            scope: {
                screen: '='
            },
            link: function($scope, element, attrs) {
                $scope.appendToValue = function(txt) {
                    if (!$scope.screen.value) {
                        $scope.screen.value = '';
                    }
                    $scope.screen.value += txt;
                }

                $scope.setValue = function(val) {
                    $scope.screen.value = val;
                }

                $scope.resetValue = function() {
                    $scope.screen.value = null;
                }

                $scope.toggleValue = function(option) {
                    if (!$scope.screen.value) {
                        $scope.screen.value = [ option ];
                        return;
                    }
                    var ind = _.indexOf($scope.screen.value, option);
                    if (ind >= 0) {
                        $scope.screen.value = _.without($scope.screen.value, option);
                    }
                    else {
                        $scope.screen.value.push(option);
                    }
                }

                $scope.hasOption = function(option) {
                    return _.indexOf($scope.screen.value, option) >= 0;
                }
            },
            template: '<div ng-include="screen.template"></div>'
        }
    }]).

    controller('FormEntryCtrl', ['$scope',
        function($scope) {
            $scope.form = {
                title: "Inpatient Followup",
                sections: [
                    {
                        title: "Vitals",
                        screens: [
                            {
                                title: "Consciousness", template: "single-concept", config: {
                                    options: [
                                        { label: "A", value: "1" },
                                        { label: "V", value: "2" },
                                        { label: "P", value: "3" },
                                        { label: "U", value: "4" },
                                    ]
                                }
                            },
                            {
                                title: "Temperature", template: "single-numeric",
                                config: {
                                    suffix: "degrees C",
                                    concept: "5080"
                                }
                            },
                            {
                                title: "Symptoms", template: "multiple-concepts",
                                config: {
                                    concept: "999",
                                    options: [
                                        { label: "Headache", value: "1" },
                                        { label: "Joint/muscle pains", value: "2" },
                                        { label: "Unable to drink", value: "3" }
                                    ]
                                }
                            },
                            {
                                title: "Bleeding from where?", template: "multiple-concepts",
                                require: "***",
                                config: {
                                    options: [
                                        { label: "Nose/oral", value: "1" },
                                        { label: "Cough", value: "2" },
                                        { label: "Vomit", value: "3" },
                                        { label: "Stool", value: "4" },
                                        { label: "Vaginal (not menstrual)", value: "5" },
                                        { label: "Other (list)", value: "6" }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }

            var sectionIndex = 0;
            var screenIndex = 0;
            function refreshCurrentStatus() {
                $scope.currentSection = $scope.form.sections[sectionIndex];
                $scope.currentScreen = $scope.currentSection.screens[screenIndex];
            }
            refreshCurrentStatus();

            $scope.nextScreen = function() {
                if (screenIndex + 1 < $scope.currentSection.screens.length) {
                    screenIndex += 1;
                }
                else if (sectionIndex + 1 < $scope.form.sections.length) {
                    sectionIndex += 1;
                    screenIndex = 0;
                }
                else {
                    console.log("Done!");
                }
                refreshCurrentStatus();
            }

            $scope.prevScreen = function() {
                if (screenIndex > 0) {
                    screenIndex -= 1;
                }
                else if (sectionIndex > 1) {
                    sectionIndex -= 1;
                    screenIndex = $scope.form.sections[sectionIndex].screens.length;
                }
                else {
                    console.log("At beginning!");
                }
                refreshCurrentStatus();
            }

        }]);