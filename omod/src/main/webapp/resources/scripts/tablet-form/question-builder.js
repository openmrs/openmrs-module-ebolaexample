angular.module('tabletForm')

.factory('questionHandlers', [ 'concepts', function(concepts) {
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
    };

    var multipleObsHandler = {
        handle: function(data, question) {
            if (!question.value || question.value.length == 0) {
                return;
            }
            data.obs = data.obs || [];
            angular.forEach(question.value, function(selectedValue) {
                data.obs.push({
                    concept: question.concept,
                    value: selectedValue
                });
            });
        }
    };

    function symptomPresentObsGroup(symptomConcept, isPresent) {
        var whichSymptomObs = {
            concept: concepts.whichSymptom,
            value: symptomConcept
        };

        var symptomPresentObs = {
            concept: concepts.symptomPresent,
            value: isPresent
        }

        return {
            concept: concepts.symptomConstruct,
            groupMembers: [ whichSymptomObs, symptomPresentObs ]
        };
    }

    var symptomPresentHandler = {
        handle: function(data, question) {
            if (!question.value) {
                return;
            }
            data.obs = data.obs || [];
            data.obs.push(symptomPresentObsGroup(question.concept, question.value));
        }
    };

    var symptomPresentCheckboxHandler = {
        handle: function(data, question) {
            if (question.value) { // checked -> true
                data.obs = data.obs || [];
                data.obs.push(symptomPresentObsGroup(question.concept, concepts.yes));
            }
        }
    };

    var multipleSymptomsPresentHandler = {
        handle: function(data, question) {
            if (!question.value || question.value.length == 0) {
                return;
            }
            data.obs = data.obs || [];
            angular.forEach(question.value, function(selectedValue) {
                data.obs.push(symptomPresentObsGroup(selectedValue, concepts.yes));
            });
        }
    };

    var symptomSeverityHandler = {
        handle: function(data, question) {
            if (!question.value) {
                return;
            }
            var whichSymptomObs = {
                concept: concepts.whichSymptom,
                value: question.concept
            };

            var symptomSeverityObs = {
                concept: concepts.symptomSeverity,
                value: question.value
            }

            var obsGroup = {
                concept: concepts.symptomConstruct,
                groupMembers: [ whichSymptomObs, symptomSeverityObs ]
            };

            data.obs = data.obs || [];
            data.obs.push(obsGroup);
        }
    };

    return {
        simpleObs: simpleObsHandler,
        multipleObs: multipleObsHandler,
        symptomPresent: symptomPresentHandler,
        symptomPresentCheckbox: symptomPresentCheckboxHandler,
        multipleSymptomsPresent: multipleSymptomsPresentHandler,
        symptomSeverity: symptomSeverityHandler
    }
}])

    .factory('questionBuilder', [ 'questionHandlers', 'answers', function(questionHandlers, answers) {
        function simpleNumericQuestion(label, concept, unit, min, max) {
            return {
                handler: questionHandlers.simpleObs,
                template: "numeric",
                label: label,
                min: min,
                max: max,
                unit: unit,
                unitClass: unit && unit.length > 3 ? "small" : "",
                concept: concept
            };
        }

        function symptomPresentQuestion(label, concept) {
            return {
                handler: questionHandlers.symptomPresent,
                template: "selectOne",
                buttonClass: "",
                options: [answers.yes, answers.no],
                label: label,
                concept: concept
            };
        }

        function symptomPresentCheckboxQuestion(label, concept) {
            return {
                handler: questionHandlers.symptomPresentCheckbox,
                template: "checkboxOnly",
                buttonClass: "medium long",
                label: label,
                concept: concept
            }
        }

        function symptomSeverityQuestion(label, concept) {
            return {
                handler: questionHandlers.symptomSeverity,
                template: "selectOne",
                options: [answers.none, answers.mild, answers.moderate, answers.severe],
                buttonClass: "medium",
                label: label,
                concept: concept
            };
        }

        function selectOneObs(label, concept, options) {
            return {
                handler: questionHandlers.simpleObs,
                template: "selectOne",
                label: label,
                buttonClass: options.length > 4 ? "small" : "medium",
                options: options,
                concept: concept
            };
        }

        function selectMultipleObs(label, concept, options) {
            return {
                handler: questionHandlers.multipleObs,
                template: "selectMulti",
                label: label,
                buttonClass: options.length > 4 ? "small" : "medium",
                options: options,
                concept: concept
            };
        }

        return {
            simpleNumeric: simpleNumericQuestion,
            symptomPresent: symptomPresentQuestion,
            symptomPresentCheckbox: symptomPresentCheckboxQuestion,
            symptomSeverity: symptomSeverityQuestion,
            selectOneObs: selectOneObs,
            selectMultipleObs: selectMultipleObs
        };
    }])