angular.module('tabletapp')

    .controller('MainController', function ($scope, $state, forms, questions,concepts, $http, $location, EncounterResource,
                                            VisitResource, ProviderResource, CurrentSession, FeedbackMessages) {
        var that = this;

        $scope.formName = $state.current.data.form;
        var currentForm = forms[$scope.formName];

        $scope.views = currentForm.views;
        $scope.questions = questions;
        $scope.saveButtonActive = true;

        _.each($scope.questions, function(question){
            delete question.value;
        });

        var unknownProvider = ProviderResource.query({ q: "UNKNOWN", v: "default" });

        var activeView = undefined;

        var activeVisits = VisitResource.query({
            patient: $scope.patientUuid,
            includeInactive: false
        });

        function initialize() {
            activeView = $scope.views[0];
        }

        function getActiveViewIndex() {
            return _.indexOf($scope.views, activeView);
        }

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
            $scope.saveButtonActive = false;
            var data = {};
            angular.forEach($scope.questions, function(question) {
                if (question.handler) {
                    question.handler.handle(data, question)
                }
                else {
                    console.log("no handler for question: " + question);
                }
            });

            if(data.obs == undefined){
                window.alert("Error: no values specified");
                $scope.saveButtonActive = true;
                return;
            }

            data.patient = $scope.patientUuid;
            if (activeVisits.results.length == 0) {
                window.alert("Error: patient doesn't have an active visit");
                $scope.saveButtonActive = true;
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
                //_.each($scope.questions, function(question){
                //    //if(question.template == "selectOne2" || question.template == "selectOne"){
                //    //    question.value = null;
                //    //}else if(question.template == "selectMulti" || question.template == "selectMulti2"){
                //    //    question.value = null;
                //    //}
                //    delete question.value;
                //});

                $state.go("^.overview").then(function() {
                    // set this after transitioning state, because messages are cleared on $stateChangeSuccess
                    FeedbackMessages.showSuccessMessage({
                        display: "Saved " + $scope.formName.toLowerCase() + " for " + $scope.patient.display
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