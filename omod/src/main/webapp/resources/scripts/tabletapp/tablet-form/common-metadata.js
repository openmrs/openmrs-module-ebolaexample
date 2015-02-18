angular.module('tabletapp')

    .factory('encounterTypes', [ function() {
        return {
                vitalsSigns: "181820aa-ffff-ffff-ffff-af92f5364127" //substitute this by real uuid 
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