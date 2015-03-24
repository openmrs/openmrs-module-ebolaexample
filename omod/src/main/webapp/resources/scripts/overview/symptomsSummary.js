

angular.module('symptomsSummary', ['feature-toggles', 'tabletapp', 'filters'])

    .controller('SymptomsSummaryController', ['$scope', '$http', 'FeatureToggles','dateFilter',
        'concepts', 'questions','SymptomAnswerBuilder',
        function ($scope, $http, FeatureToggles, dateFilter, concepts, questions, SymptomAnswerBuilder) {
            $scope.isFeatureEnabled = function(){
                return FeatureToggles.isFeatureEnabled("symptomsSummaryDesktop");
            };
            var config = {};
            $scope.init = function(setConfig){
                config = setConfig;
                $scope.getSymptomsEncounters(3);
            };

            $scope.questions = questions;

            $scope.getSymptomsEncounters = function(top){
                var url = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/encounter/vitals-and-symptoms";
                url += "?patientUuid=" + config.patientUuid;
                url  += "&formUuid=" + "c1d1b5b7-2d51-4f58-b8f3-7d9cb542fe4a";
                if(!!top){
                    url  += "&top=" + top;
                }
                $http.get(url).success(function(response) {
                    $scope.symptomsEncounters = response["encounters"];
                });
            };
            $scope.getObsDesc = function(obs){
                var answers = SymptomAnswerBuilder.getAnswers(obs);
                var row1 = answers.general.concat(answers.pain, answers.respiratory);
                var row2 = answers.GI.concat(answers.bleeding, answers.ebolaStage);
                return {row1:row1.toString(), row2:row2.toString()};
            };
        }])

        .factory("SymptomAnswerBuilder", ['concepts', "questions", function(concepts, questions){
            var questions = questions;
            function extractOptionLabel(question, value) {
                var option = _.find(question.options, function (option) {
                    return option.value == value;
                });
                if(!!option){
                    return option.label;
                }
                return null;
            }

            function extractName(question, ob) {
                var label = extractOptionLabel(question, ob.value);
                return question.label + "(" + label + ")";
            }

            function extractMultiName(question, ob) {
                var label = extractOptionLabel(question, ob.value);
                return label + " " + question.label.toLowerCase() ;
            }
            function match(options, questionValue) {
                return _.some(options, function (option) {
                    return option.value == questionValue;
                });
            }

            function getAnswers(obs){
                var answers = {ebolaStage:null, bleeding:[], pain:[], GI:[], general:[], respiratory:[]};
                _.each(obs, function(ob){

                    if(ob.concept == questions.ebolaStage.concept){
                        answers.ebolaStage = extractName(questions.ebolaStage, ob)
                    }else if(ob.concept == questions.bleeding.concept){
                        answers.bleeding.push(extractMultiName(questions.bleeding, ob))
                    }
                    else if(ob.concept == concepts.symptomConstruct){
                        var questionValue = ob.groupMembers[0].value;
                        if(ob.groupMembers[0].concept != concepts.whichSymptom){
                            questionValue = ob.groupMembers[1].value;
                        }
                        if(match(questions.GI.options, questionValue))
                        {
                            answers.GI.push(extractOptionLabel(questions.GI, questionValue))
                        }
                        else if(match(questions.pain.options, questionValue)){
                            answers.pain.push(extractMultiName(questions.pain, {value:questionValue}))
                        }
                        else if(match(questions.respiratory.options, questionValue)){
                            answers.respiratory.push(extractOptionLabel(questions.respiratory, questionValue))
                        }else if(match(questions.general.options, questionValue)){
                            answers.general.push(extractOptionLabel(questions.general, questionValue))
                        }
                    }
                });
                return answers;
            }
            return {getAnswers:getAnswers};
        }]);
