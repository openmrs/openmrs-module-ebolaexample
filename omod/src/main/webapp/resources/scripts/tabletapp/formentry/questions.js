angular.module('tabletapp')

    .factory('questions', [ 'questionBuilder', 'questionHandlers', 'concepts', function(questionBuilder, questionHandlers, concepts) {
        return {
            consciousness: questionBuilder.selectOneObs("CURRENT CONSCIOUSNESS", "162643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                    { label: "A", value: "160282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "V", value: "162645AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "P", value: "162644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "U", value: "159508AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]),
            temperature: questionBuilder.simpleNumeric("Temp", "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "˚C", 30, 45),
            o2sat: questionBuilder.simpleNumeric("O2 Sat", "5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "%", 0, 100),
            respiratoryRate: questionBuilder.simpleNumeric("Resp. Rate", "5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "breaths/ min", 0, 120),
            heartRate: questionBuilder.simpleNumeric("Pulse", "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "beats/ min", 0, 999),
            // "SYSTOLIC BP" and "DIASTOLIC BP" for Blood Pressure
            systolicBP: questionBuilder.simpleNumeric("", "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 250),
            diastolicBP: questionBuilder.simpleNumeric("", "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 200),

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
            //vomiting: questionBuilder.symptomSeverity("VOMITING", "122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),

            //stoolFrequency: questionBuilder.simpleNumeric("STOOL FREQUENCY", "1837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "#/24h", 0, 9),
            //mainStoolType: questionBuilder.selectOneObs("MAIN STOOL TYPE", "162654AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
            //        { label: "None", value: concepts.none },
            //        { label: "Formed", value: "162655AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
            //        { label: "Semi-Formed", value: "162656AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
            //        { label: "Liquid", value: "162657AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
            //    ]),

            //overallSymptoms: questionBuilder.selectOneObs("OVERALL SYMPTOMS", "162676AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
            //    { label: "Same", value: "162679AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
            //    { label: "Worse", value: "162678AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
            //    { label: "Better", value: "162677AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
            //]),
            //fatigue: questionBuilder.symptomSeverity("FATIGUE", "140501AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            bleeding: questionBuilder.selectMultipleObs2("Bleeding", "162668AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", [
                { label: "Nose/Mouth", value: "160495AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Vomitus", value: "162670AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Cough", value: "162669AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "IV Site", value: "162920AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Stool", value: "162671AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Vagina", value: "162673AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                { label: "Urine", value: "162919AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
            ]),
            ebolaStage: questionBuilder.selectOneObs2("Ebola Stage", "162834AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",[
                { label: "1 - Early/Dry", value: "162829AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"},
                { label: "2 - GI/Wet", value: "162830AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"},
                { label: "3 - Severe", value: "162831AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
            ]),

            //headache: questionBuilder.symptomPresentCheckbox("Headache", "139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            general:{
                handler: questionHandlers.multipleSymptomsPresent,
                template: "selectMulti2",
                label: "General",
                buttonClass: "medium2 long2",
                options: [
                    { label: "Confusion", value: "120345AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Fatigue", value: "140501AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Dehydration", value: "142630AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Rash", value: "512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Pallor", value: "5245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    //{ label: "Other", value: "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]
            },
            respiratory:{
                handler: questionHandlers.multipleSymptomsPresent,
                template: "selectMulti2",
                label: "Respiratory",
                buttonClass: "medium2 long2",
                options: [
                    { label: "Short of breath", value: "122496AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Cough", value: "143264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Hiccups", value: "138662AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]
            },
            pain:{
                handler: questionHandlers.multipleSymptomsPresent,
                template: "selectMulti2",
                label: "Pain",
                buttonClass: "medium2 long2",
                options: [
                    { label: "Head", value: "139084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Muscle/Joint", value: "133632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Chest", value: "120749AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Abdomen", value: "151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]
            },
            //diarrhoea: questionBuilder.symptomPresentCheckbox("Diarrhoea", "142412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            //vomiting: questionBuilder.symptomPresentCheckbox("Vomiting", "122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            //nausea: questionBuilder.symptomPresentCheckbox("Nausea", "5978AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            GI:{
                handler: questionHandlers.multipleSymptomsPresent,
                template: "selectMulti2",
                label: "GI",
                buttonClass: "medium2 long2",
                options: [
                    { label: "Diarrhoea", value: "142412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Vomiting", value: "122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "Nausea", value: "5978AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ]
            },

            // TODO fluid management
            targetVolume: questionBuilder.simpleNumeric("TARGET VOLUME IN NEXT 24H", "162675AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mL", 0, 9999)
        };
    }]);