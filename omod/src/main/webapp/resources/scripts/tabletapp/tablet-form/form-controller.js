angular.module('tabletapp')

.controller('FormController',['$scope', 'questions', 'encounterTypes', '$http', '$location','CurrentSession',
 function ($scope, questions, encounterTypes, $http, $location, CurrentSession) {
    var path = $location.path().substring(0, $location.path().indexOf("/", 1));

        // hack for testing this form in hackathon dev environment
        var CONTEXT_PATH = (path === "/openmrs-module-ebolaexample") ? "/openmrs" : path;

        $scope.questions = questions;

        $scope.saveVitals = function () {
            var data = {};
            
            angular.forEach($scope.questions, function(question) {
                if (question.handler) {
                    question.handler.handle(data, question)
                }
                else {
                    console.log("no handler for question: " + question);
                }
            });

            visitUuid = CurrentSession.getEncounter().visit;
            encounterType = encounterTypes.vitalsSigns;
            
            data.patient = $scope.__proto__.patientInfo.uuid;
            data.visit = (typeof(visitUuid) != 'undefined' ? visitUuid : 'aaaaa'); //FIX THIS - uuid is empty in object
            data.location = CurrentSession.getRecentWard().uuid;
            data.provider = CurrentSession.getInfo().provider.uuid;
            data.encounterType = (typeof(encounterType) != 'undefined' ? visitUuid : 'aaaaa'); //FIX THIS - object does not exist in getInfo()
            console.log("data ",data);
            
            $http.post(CONTEXT_PATH + "/ws/rest/v1/encounter", data).success(function (result) {
                location.href = CONTEXT_PATH + "/ebolaexample/ebolaOverview.page?patient=" + $scope.patient.patientUuid;
            });
        };
    }]);