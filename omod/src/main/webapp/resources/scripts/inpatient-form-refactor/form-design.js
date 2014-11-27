angular.module('tabletFormFramework')

    .factory("questions", [ 'handlers', 'questionBuilder', function(handlers, questionBuilder) {

        var questions = {
            consciousness: {
                handler: handlers.simpleObs,
                template: "selectOne",
                label: "CURRENT CONSCIOUSNESS",
                options: [
                    { label: "A", value: "160282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "V", value: "162645AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "P", value: "162644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
                    { label: "U", value: "159508AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
                ],
                concept: "162643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            },
            temperature: questionBuilder.simpleNumeric("TEMPERATURE", "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "˚C", 30, 45),
            o2sat: questionBuilder.simpleNumeric("OXYGEN SATURATION", "5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "%", 0, 100),
            respiratoryRate: questionBuilder.simpleNumeric("RESPIRATORY RATE", "5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "breaths/ min", 0, 100),
            heartRate: questionBuilder.simpleNumeric("HEART RATE", "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "beats/ min", 0, 999),
            systolicBP: questionBuilder.simpleNumeric("SYSTOLIC BP", "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 250),
            diastolicBP: questionBuilder.simpleNumeric("DIASTOLIC BP", "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mmHg", 0, 200),
            raisedJVP: questionBuilder.simpleNumeric("RAISED JVP", "162646AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "cm", 0, 100),
            capillaryRefillTime: questionBuilder.simpleNumeric("CAPILLARY REFILL", "162513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "sec", 0, 100),
            abdomenTender: questionBuilder.symptomPresent("ABDOMEN TENDER", "5105AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
            paleAnemia: questionBuilder.symptomSeverity("PALE/ANAEMIA", "131004AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        }

        angular.forEach(questions, function(question) {
            if (question.handler && question.handler.defaults) {
                angular.forEach(question.handler.defaults, function(value, key) {
                    if (!question[key]) {
                        question[key] = value;
                    }
                })
            }
        });
        return questions;
    }])

    .factory("views", [ function() {

        var createView = function (name, file, description, shouldShow) {
            return {
                name: name,
                file: file,
                description: description,
                shouldShow: shouldShow
            }
        }

        return [
            createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS', true),
            createView('vital-signs', 'vital-signs2.html', 'VITAL SIGNS 2', false)
//            createView('hydration', 'hydration.html', 'HYDRATION', true),
//            createView('hydration', 'hydration2.html', 'HYDRATION 2', false),
//            createView('symptoms', 'symptoms.html', 'SYMPTOMS', true),
//            createView('symptoms', 'symptoms2.html', 'SYMPTOMS 2', false),
//            createView('daily-management', 'daily-management.html', 'DAILY MGMT', true),
//            createView('daily-management', 'daily-management2.html', 'DAILY MGMT2', false)
        ]
    }]);
