angular.module('tabletForm')

    .factory('encounterTypes', [ function() {
        return {
            inpatientFollowup: "83413734-587d-11e4-af12-660e112eb3f5"
        }
    }])

    .factory('concepts', [ function() {
        return {
            yes: "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            no: "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            unknown: "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            none: "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            mild: "1498AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            moderate: "1499AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            severe: "1500AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            normal: "1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            reduced: "162648AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            symptomConstruct: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            whichSymptom: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", // concept set with symptomPresent and symptomSeverity
            symptomPresent: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", // yes/no
            symptomSeverity: "162642AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" // none/mild/moderate/severe
        }
    }])

    .factory('answers', [ 'concepts', function(concepts) {
        function createAnswer(label, value) {
            return {
                label: label,
                value: value
            }
        }

        return {
            yes: createAnswer("Yes", concepts.yes),
            no: createAnswer("No", concepts.no),
            none: createAnswer("None", concepts.none),
            mild: createAnswer("Mild", concepts.mild),
            moderate: createAnswer("Moder", concepts.moderate),
            severe: createAnswer("Severe", concepts.severe)
        }
    }]);