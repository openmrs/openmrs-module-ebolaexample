angular.module("constants", [])
    .factory("Constants", function () {
        return {
            encounterType: {
                ebolaInpatientFollowup: "83413734-587d-11e4-af12-660e112eb3f5"
            },
            careSetting: {
                inpatient: "c365e560-c3ec-11e3-9c1a-0800200c9a66"
            },
            dosingType: {
                freeText: "org.openmrs.FreeTextDosingInstructions",
                simple: "org.openmrs.SimpleDosingInstructions",
                roundBased: "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions"
            },
            orderType: {
                drugorder: "drugorder"
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
                    display: "grams",
                    uuid: "161554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "tablets",
                    uuid: "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mL",
                    uuid: "162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "puffs",
                    uuid: "162372AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "topical",
                    uuid: "162467AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                },
                {
                    display: "mg/kg",
                    uuid: ""
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
                }
            ]
        }
    });