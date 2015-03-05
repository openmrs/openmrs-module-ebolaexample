angular.module('tabletForm')

    .factory('questions', [ 'questionBuilder', 'questionHandlers', 'concepts', function(questionBuilder, questionHandlers, concepts) {
        return {
            consciousness: questionBuilder.selectOneObs("CURRENT CONSCIOUSNESS", "162643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                    { label: "A", value: "160282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "V", value: "162645AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "P", value: "162644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "U", value: "159508AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]),
            temperature: questionBuilder.simpleNumeric("TEMPERATURE", "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "˚C", 30, 45),
            o2sat: questionBuilder.simpleNumeric("OXYGEN SATURATION", "5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "%", 0, 100),
            respiratoryRate: questionBuilder.simpleNumeric("RESPIRATORY RATE", "5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "breaths/ min", 0, 120),
            heartRate: questionBuilder.simpleNumeric("HEART RATE", "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "beats/ min", 0, 999),
            systolicBP: questionBuilder.simpleNumeric("SYSTOLIC BP", "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 250),
            diastolicBP: questionBuilder.simpleNumeric("DIASTOLIC BP", "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 200),

            raisedJVP: questionBuilder.simpleNumeric("RAISED JVP", "162646AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "cm", 0, 19),
            capillaryRefillTime: questionBuilder.simpleNumeric("CAPILLARY REFILL", "162513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "sec", 0, 60),
            abdomenTender: questionBuilder.symptomPresent("ABDOMEN TENDER", "5105AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            paleAnemia: questionBuilder.symptomSeverity("PALE/ANAEMIA", "131004AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),

            fluidsIn24h: questionBuilder.simpleNumeric("ORAL FLUIDS IN PAST 24H", "162658AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mL", 0, 10000),
            dehydration: questionBuilder.symptomSeverity("DEHYDRATION", "142630AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            urineOutput: questionBuilder.selectOneObs("URINE OUTPUT", "162647AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                { label: "Normal", value: concepts.normal },
                { label: "Reduced", value: concepts.reduced },
                { label: "None", value: concepts.none },
                { label: "Unknown", value: concepts.unknown }
            ]),
            vomiting: questionBuilder.symptomSeverity("VOMITING", "122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),

            stoolFrequency: questionBuilder.simpleNumeric("STOOL FREQUENCY", "1837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "#/24h", 0, 9),
            mainStoolType: questionBuilder.selectOneObs("MAIN STOOL TYPE", "162654AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                    { label: "None", value: concepts.none },
                    { label: "Formed", value: "162655AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Semi-Formed", value: "162656AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Liquid", value: "162657AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]),

            overallSymptoms: questionBuilder.selectOneObs("OVERALL SYMPTOMS", "162676AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                { label: "Same", value: "162679AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Worse", value: "162678AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Better", value: "162677AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
            ]),
            fatigue: questionBuilder.symptomSeverity("FATIGUE", "140501AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            bleeding: questionBuilder.selectMultipleObs("BLEEDING", "162668AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                { label: "Nose/Oral", value: "160495AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Cough", value: "162669AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Vomit", value: "162670AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Stool", value: "162671AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Vaginal (not menstrual)", value: "162673AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Other", value: "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
            ]),

            headache: questionBuilder.symptomPresentCheckbox("Headache", "139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            moreSymptoms: {
                handler: questionHandlers.multipleSymptomsPresent,
                template: "selectMulti",
                label: "SYMPTOMS",
                buttonClass: "medium long",
                options: [
                    { label: "Headache", value: "139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Muscle Pain", value: "133632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Unable to Drink", value: "1983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Unable to Eat", value: "162706AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Difficult to Swallow", value: "118789AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Difficult to Breathe", value: "122496AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Abdominal Pain", value: "151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Hiccups", value: "138662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Urine Pain", value: "118771AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Cough", value: "143264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Rash", value: "512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]
            },

            // TODO fluid management
            targetVolume: questionBuilder.simpleNumeric("TARGET VOLUME IN NEXT 24H", "162675AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mL", 0, 9999)
        };
    }]);