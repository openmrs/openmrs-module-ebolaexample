angular.module("constants", [])
    .factory("Constants", function () {
        return {
            wardKey: 'wardKey',
            encounterType: {
                ebolaInpatientFollowup: "83413734-587d-11e4-af12-660e112eb3f5"
            },
            careSetting: {
                inpatient: "c365e560-c3ec-11e3-9c1a-0800200c9a66"
            },
            dosingType: {
                freeText: "org.openmrs.module.ebolaexample.domain.UnvalidatedFreeTextDosingInstructions",
                unvalidatedFreeText: "org.openmrs.module.ebolaexample.domain.UnvalidatedFreeTextDosingInstructions",
                simple: "org.openmrs.SimpleDosingInstructions",
                roundBased: "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions"
            },
            durationUnits: {
                days: '1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
            },
            orderType: {
                drugorder: "drugorder"
            },
            orderAction: {
                discontinue: "DISCONTINUE"
            },
            rounds: [
                { name: "Morning"},
                { name: "Afternoon"},
                { name: "Evening"},
                { name: "Night"}
            ],
            doseUnits: [
                {
                    display: "mg",
                    uuid: "161553AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mg/kg",
                    uuid: "162761AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mL",
                    uuid: "162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mL/kg",
                    uuid: "162762AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "IU",
                    uuid: "162264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mcg",
                    uuid: "162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mcg/kg",
                    uuid: "162766AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "grams",
                    uuid: "161554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                }
            ],
            routes: [
                {
                    display: 'Rectal suppository',
                    uuid: '162458AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'IV',
                    uuid: '160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'IM',
                    uuid: '160243AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Oral',
                    uuid: '160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Nasogastric',
                    uuid: '1766AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'IO',
                    uuid: '162624AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                }
            ],
            asNeededConditions: [
                "Pain",
                "Insomnia",
                "Diarrhea",
                "Nausea",
                "Fever",
                "Indigestion",
                "Agitation",
                "Anxiety",
                "Constipation",
                "Cough"
            ],
            administrationStatuses: {
                FULL: "Fully Given",
                PARTIAL: "Partially Given",
                NOT_GIVEN: "Not Given"
            },
            reasonsNotAdministered: [
                "Patient asleep",
                "Patient vomiting",
                "Aggressive mood",
                "Cannot access vein for IV"
            ],
            roles: {
                wardRoundingTeam: "Ward Rounding Team"
            }
        }
    });