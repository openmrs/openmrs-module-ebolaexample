angular.module('tabletFormFramework').

    controller('FormController', function ($scope, $http, $location, questions, views) {
        var that = this;

        var path = $location.path().substring(0, $location.path().indexOf("/", 1));

        // hack for testing this form in hackathon dev environment
        var CONTEXT_PATH = (path === "/openmrs-module-ebolaexample") ? "/openmrs" : path;

        $scope.patient = {};
        $scope.patient.visitUuid = $location.search().visitUuid;
        $scope.patient.patientUuid = $location.search().patientUuid;
        $scope.patient.locationUuid = $location.search().locationUuid;
        $scope.patient.providerUuid = $location.search().providerUuid;

        $scope.questions = questions;
        $scope.views = views;

        var activeView = undefined,
            completeData = {},
            initialize = function () {
                activeView = $scope.views[0];
            },
            getActiveViewIndex = function () {
                return _.indexOf($scope.views, activeView);
            };

        function loadData() {

            $http.get(CONTEXT_PATH + "/ws/rest/v1/patient/" + $scope.patient.patientUuid, {
                params: {
                    v: "full"
                }
            }).success(function (result) {
                var patientId = _.filter(result.identifiers, function (id) {
                    return id.preferred;
                });
                $scope.patient.identifier = patientId[0].identifier;
                $scope.patient.name = result.person.preferredName.display;
                $scope.patient.gender = result.person.gender;
                $scope.patient.age = result.person.age;
            });
        }

        $scope.focusInput = function ($event) {
            angular.element($event.target).parent().addClass('highlight');
        };

        $scope.blurInput = function ($event) {
            angular.element($event.target).parent().removeClass('highlight');
        };

        $scope.shouldDisplay = function (view) {
            return activeView === view;
        };

        $scope.getCurrentViewNumber = function () {
            return $scope.views.indexOf(activeView) + 1;
        };

        $scope.getProgress = function () {
            return $scope.views.indexOf(activeView) * 100 / $scope.views.length;
        };

        $scope.shouldDisplayBackButton = function () {
            return getActiveViewIndex() !== 0;
        };

        $scope.shouldDisplayNextButton = function () {
            return getActiveViewIndex() !== $scope.views.length - 1;
        };

        $scope.shouldDisplayFinishButton = function () {
            return getActiveViewIndex() === $scope.views.length - 1;
        };

        $scope.next = function () {
            activeView = $scope.views[getActiveViewIndex() + 1];
        };

        $scope.back = function () {
            activeView = $scope.views[getActiveViewIndex() - 1];
        };

        $scope.finish = function () {
            var data = {};
            angular.forEach($scope.questions, function(question) {
                if (question.handler) {
                    question.handler.handle(data, question)
                }
                else {
                    console.log("no handler for question: " + question);
                }
            });
            data.patient = $scope.patient.patientUuid;
            data.visit = $scope.patient.visitUuid;
            data.location = $scope.patient.locationUuid;
            data.provider = $scope.patient.providerUuid;
            data.encounterType = "83413734-587d-11e4-af12-660e112eb3f5";

            $http.post(CONTEXT_PATH + "/ws/rest/v1/encounter", data).success(function (result) {
                location.href = CONTEXT_PATH + "/ebolaexample/ebolaOverview.page?patient=" + $scope.patient.patientUuid;
            });
        };

        $scope.display = function (view) {
            activeView = view;
        };

        initialize();
        loadData();

    });