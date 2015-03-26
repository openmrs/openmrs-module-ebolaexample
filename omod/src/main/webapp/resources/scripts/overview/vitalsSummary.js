

angular.module('vitalsSummary', ['feature-toggles', 'tabletapp', 'filters'])

    .controller('VitalsSummaryController', ['$scope', '$http', 'FeatureToggles','dateFilter',
        'concepts', 'questions','VitalsAnswerBuilder',
        function ($scope, $http, FeatureToggles, dateFilter, concepts, questions, VitalsAnswerBuilder) {
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("vitalsAndSymptomsSummaryDesktop");
            };
            var config = {};
            $scope.init = function(setConfig){
                config = setConfig;
                $scope.getVitalsEncounters(3);
            };

            $scope.questions = questions;

            $scope.getVitalsEncounters = function(top){
                var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/encounter/vitals-and-symptoms";
                url += "?patientUuid=" + config.patientUuid;
                url  += "&formUuid=" + "a000cb34-9ec1-4344-a1c8-f692232f6edd";
                if(!!top){
                    url  += "&top=" + top;
                    $scope.showAll = false;
                }else{
                    $scope.showAll = true;

                }
                $http.get(url).success(function(response) {
                    $scope.vitalsEncounters = response["encounters"];
                });
            };
            $scope.getObsDesc = function(obs){
                var answers = VitalsAnswerBuilder.getAnswers(obs);
                var result = [answers.consciousness,
                                'T: ' + answers.temperature,
                                'Pulse: ' + answers.heartRate,
                                'Resp: ' + answers.respiratoryRate,
                                'O₂: ' + answers.o2sat,
                                'BP: ' + answers.systolicBP + ' / ' + answers.diastolicBP];
                return result;
            };
        }])

        .factory("VitalsAnswerBuilder", ['concepts', "questions", function(concepts, questions){
            function extractOptionLabel(question, value) {
                var option = _.find(question.options, function (option) {
                    return option.value == value;
                });
                if(!!option){
                    return option.label;
                }
                return null;
            }

            function getAnswers(obs){
                var answers = {consciousness:'-', temperature:'-', heartRate:'-',
                    respiratoryRate:'-', o2sat:'-', systolicBP:'-',
                    diastolicBP: '-'};

                _.each(obs, function(ob){

                    if(ob.concept == questions.consciousness.concept){
                        var consciousness = extractOptionLabel(questions.consciousness, ob.value);
                        var map = {
                            'A':'Alert',
                            'V':'Voice',
                            'P':'Pain',
                            'U':'Unresponsive'
                        };
                        answers.consciousness = map[consciousness];
                    }else if(ob.concept == questions.temperature.concept){
                        answers.temperature = ob.value + '˚C';
                    }else if(ob.concept == questions.heartRate.concept){
                        answers.heartRate = ob.value;
                    }else if(ob.concept == questions.respiratoryRate.concept){
                        answers.respiratoryRate = ob.value;
                    }else if(ob.concept == questions.o2sat.concept){
                        answers.o2sat = ob.value + "%";
                    }else if(ob.concept == questions.systolicBP.concept){
                        answers.systolicBP = ob.value;
                    }else if(ob.concept == questions.diastolicBP.concept){
                        answers.diastolicBP = ob.value;
                    }
                });
                return answers;
            }
            return {getAnswers:getAnswers};
        }]);
