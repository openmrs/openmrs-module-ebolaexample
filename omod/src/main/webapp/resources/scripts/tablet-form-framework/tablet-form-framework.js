angular.module('tabletFormFramework', [])

    .config(function($locationProvider) {
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        });
    })


    .factory("answers", [ function() {
        function createAnswer(label, value) {
            return {
                label: label,
                value: value
            }
        }

        return {
            yes: createAnswer("Yes", "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            no: createAnswer("No", "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            none: createAnswer("None", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            mild: createAnswer("Mild", "1498AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            moderate: createAnswer("Moder", "1499AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            severe: createAnswer("Severe", "1500AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        }
    }])


    .factory("handlers", [ 'answers', function(answers) {
        var simpleObsHandler = {
            handle: function(data, question) {
                if (!question.value) {
                    return;
                }
                data.obs = data.obs || [];
                data.obs.push({
                    concept: question.concept,
                    value: question.value
                });
            }
        }

        var symptomPresentHandler = {
            defaults: {
                template: "selectOne",
                options: [answers.yes, answers.no],
                buttonClass: ""
            },
            handle: function(data, question) {
                if (!question.value) {
                    return;
                }
                var whichSymptomObs = {
                    concept: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    value: question.concept
                };

                var symptomPresentObs = {
                    concept: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    value: question.value
                }

                var obsGroup = {
                    concept: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    groupMembers: [ whichSymptomObs, symptomPresentObs ]
                };

                data.obs = data.obs || [];
                data.obs.push(obsGroup);
            }
        }

        var symptomSeverityHandler = {
            defaults: {
                template: "selectOne",
                options: [answers.none, answers.mild, answers.moderate, answers.severe],
                buttonClass: "medium"
            },
            handle: function(data, question) {
                if (!question.value) {
                    return;
                }
                var whichSymptomObs = {
                    concept: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    value: question.concept
                };

                var symptomSeverityObs = {
                    concept: "162642AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    value: question.value
                }

                var obsGroup = {
                    concept: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    groupMembers: [ whichSymptomObs, symptomSeverityObs ]
                };

                data.obs = data.obs || [];
                data.obs.push(obsGroup);
            }
        }


        return {
            simpleObs: simpleObsHandler,
            symptomPresent: symptomPresentHandler,
            symptomSeverity: symptomSeverityHandler
        }
    }])


    .factory("questionBuilder", [ 'handlers', function(handlers) {

        function simpleNumericQuestion(label, concept, unit, min, max) {
            return {
                handler: handlers.simpleObs,
                template: "numeric",
                label: label,
                min: min,
                max: max,
                unit: unit,
                unitClass: unit && unit.length > 3 ? "small" : "",
                concept: concept
            }
        }

        function symptomPresentQuestion(label, concept) {
            return {
                handler: handlers.symptomPresent,
                label: label,
                concept: concept
            }
        }

        function symptomSeverityQuestion(label, concept) {
            return {
                handler: handlers.symptomSeverity,
                label: label,
                concept: concept
            }
        }

        return {
            simpleNumeric: simpleNumericQuestion,
            symptomPresent: symptomPresentQuestion,
            symptomSeverity: symptomSeverityQuestion
        };

    }])


    .directive("question", [ function() {
        return {
            restrict: 'E',
            scope: {
                question: '='
            },
            link: function($scope, element, attrs) {
                $scope.id = emr.domId(null, "question");
            },
            templateUrl: '../tabletFormFramework/questionTemplate.html'
        }
    }])