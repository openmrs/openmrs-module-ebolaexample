angular.module('tabletapp')

    .controller('MainController', function ($scope, $state, forms, questions, $http, $location, EncounterResource,
                                            VisitResource, ProviderResource, CurrentSession, FeedbackMessages) {
        var that = this;

        var currentForm = forms[$state.current.data.form];

        $scope.views = currentForm.views;
        $scope.questions = questions;

        var unknownProvider = ProviderResource.query({ q: "UNKNOWN", v: "default" });

        var activeView = undefined;

        var activeVisits = VisitResource.query({
            patient: $scope.patient.uuid,
            includeInactive: false
        });

        function initialize() {
            activeView = $scope.views[0];
            var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/vitals-and-symptoms-obs";

            $http.get(url + "?patientUuid=" + $scope.patient.uuid).success(function(response){
                console.log('set data' + response)
            })
        }
        function getActiveViewIndex() {
            return _.indexOf($scope.views, activeView);
        };

        $scope.shouldDisplay = function (view) {
            return activeView === view;
        };

        $scope.getCurrentViewNumber = function () {
            return $scope.views.indexOf(activeView) + 1;
        };

        $scope.getProgress = function () {
            return $scope.views.indexOf(activeView) * 100 / 8;
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

        $scope.shouldDisplayCancelButton = function () {
            return getActiveViewIndex() === 0;
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
            data.patient = $scope.patient.uuid;
            if (activeVisits.results.length == 0) {
                window.alert("Error: patient doesn't have an active visit");
                return;
            }
            data.visit = activeVisits.results[0].uuid; // the API will enforce that there's never more than one

            data.location = CurrentSession.getRecentWard().uuid;

            // Because of https://issues.openmrs.org/browse/RESTWS-443 we can't do:
            // data.provider = CurrentSession.getInfo().provider.uuid;
            data.provider = unknownProvider.results[0].person.uuid;

            data.encounterType = currentForm.encounterType.uuid;
            data.form = currentForm.form_uuid;

            EncounterResource.save(data).$promise.then(function() {
                $state.go("^.overview").then(function() {
                    // set this after transitioning state, because messages are cleared on $stateChangeSuccess
                    FeedbackMessages.showSuccessMessage({
                        display: "Saved vitals for " + $scope.patient.display
                    });
                });
            });
        };

        $scope.cancel = function() {
            $state.go("^.overview");
        };

        $scope.display = function (view) {
            activeView = view;
        };

        initialize();

    });