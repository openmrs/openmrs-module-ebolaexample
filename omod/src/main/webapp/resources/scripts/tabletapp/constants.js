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
                discontinue: "DISCONTINUE",
                revise: "REVISE"
            },
            rounds: [
                { name: "Morning"},
                { name: "Afternoon"},
                { name: "Evening"},
                { name: "Night"}
            ],
            doseUnits: [
                {
                    display: "tablet(s)",
                    uuid: "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
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
                    display: 'Oral',
                    uuid: '160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
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
                    display: 'Subcutaneous',
                    uuid: '160245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Inhaled',
                    uuid: '160241AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Nasal',
                    uuid: '161253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Left Eye',
                    uuid: '162388AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Right Eye',
                    uuid: '162389AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Both Eyes',
                    uuid: '162390AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Vaginal',
                    uuid: '162392AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Rectal',
                    uuid: '162393AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Nasogastric',
                    uuid: '162798AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'IO Needle',
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
            },
            drugConfig: {
                "fc6d9876-542f-4708-ae13-875886c97541": {
                    allowedDoseUnits: [ "tablet(s)" ]
                }
            }
        }
    });