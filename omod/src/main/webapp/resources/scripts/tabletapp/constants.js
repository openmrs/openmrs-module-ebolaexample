angular.module("constants", [])
    .factory("Constants", function () {

        var continuous = 0;
        var kvo = 0;

        var tablets = {
            display: "tablet(s)",
            uuid: "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mg = {
            display: "mg",
            uuid: "161553AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mgkg = {
            display: "mg/kg",
            uuid: "162761AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mL = {
            display: "mL",
            uuid: "162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mLkg = {
            display: "mL/kg",
            uuid: "162762AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var IU = {
            display: "IU",
            uuid: "162264AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mcg = {
            display: "mcg",
            uuid: "162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mcgkg = {
            display: "mcg/kg",
            uuid: "162766AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var grams = {
            display: "grams",
            uuid: "161554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var ampule = {
            display: "Ampule(s)",
            uuid: "125519BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
        };
        var application = {
            display: "Application(s)",
            uuid: "125625BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
        };
        var units = {
            display: "Unit(s)",
            uuid: "125637BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
        };
        var puffs = {
            display: "Puff(s)",
            uuid: "125611BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
        };
        var drops = {
            display: "Drop(s)",
            uuid: "125564BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"
        };
        var mEq = {
            display: "mEq(s)",
            uuid: "162364AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var mcgkgmin = {
            display: "mcg/kg/min",
            uuid: "162838AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var minutes = {
            display: "min",
            uuid: "1733AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        var hours = {
            display: "hr",
            uuid: "1822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };
        return {
            wardKey: 'wardKey',
            encounterType: {
                ebolaInpatientFollowup: {
                    uuid: "83413734-587d-11e4-af12-660e112eb3f5",
                    display: "Inpatient Followup"
                }
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
            orderFrequency: {
                oneTime: "SNOMED CT:307486002"
            },
            durationUnits: {
                days: {
                    uuid: "1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    display: "days"
                },
                hours: {
                    uuid: "1822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    display: "hours"
                }
            },
            orderType: {
                drugorder: "drugorder",
                ivfluidorder: "ivfluidorder"
            },
            orderAction: {
                discontinue: "DISCONTINUE",
                revise: "REVISE"
            },
            rounds: [
                {name: "Morning"},
                {name: "Afternoon"},
                {name: "Evening"},
                {name: "Night"}
            ],
            doseUnits: [
                tablets, mg, mgkg, mL, mLkg, IU, mcg, mcgkg, grams, ampule, application, units, puffs, drops
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
            dosageForms: [
                {
                    display: 'Tablet',
                    uuid: '1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                },
                {
                    display: 'Oral suspension',
                    uuid: '1940AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                }
                ,
                {
                    display: 'Capsule',
                    uuid: '1608AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                }
                // for now we only use this to sort, so add any more dosageForms that need to be sorted on the route selection page
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
                "20b32b96-9daa-11e4-b773-e0fe58bba1f4": {
                    allowedDoseUnits: [tablets, mg]
                },
                "cfb2ee06-a728-4d60-9d71-f70dfe3cee3a": {
                    allowedDoseUnits: [tablets, mg]
                },
                "d56095f8-b241-44d4-a9a0-b18df6b011e1": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "777be8c9-0726-469c-8177-03f497f00013": {
                    allowedDoseUnits: [tablets]
                },
                "52ae8190-2354-4a8a-8099-61d47e85d66b": {
                    allowedDoseUnits: [tablets, mg]
                },
                "d4a1c9e5-8bdd-45cc-8b42-1a2b0f808748": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                },
                "fc6d9876-542f-4708-ae13-875886c97541": {
                    allowedDoseUnits: [tablets]
                },
                "a7487c5a-b0cb-4ec9-8d80-de93478d97a3": {
                    allowedDoseUnits: [tablets, mg]
                },
                "bcb44a2f-a248-47af-8e66-eba3f3c633ba": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "0bc02b2c-b547-46c5-b47d-c1f72630bee7": {
                    allowedDoseUnits: [tablets, mg]
                },
                "3ce45bb1-249a-4584-94e1-0abafba3e79e": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "59f04f46-ba91-46bb-94ac-dca4cc487f22": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                },
                "927fca5b-aa1a-4bbd-8b97-2035ded8634b": {
                    allowedDoseUnits: [tablets, mg]
                },
                "9467173e-c734-41c0-977a-68438f50181b": {
                    allowedDoseUnits: [mEq, mL]
                },
                "f45dd5d2-a2c3-4e9f-844d-39a48d48262e": {
                    allowedDoseUnits: [tablets]
                },
                "e73665c2-03f2-488e-8b20-a04536f82d65": {
                    allowedDoseUnits: [tablets, mg]
                },
                "b0e31bfb-5e0f-4753-a8ea-879a0a193e6c": {
                    allowedDoseUnits: [tablets]
                },
                "cf6e78df-4fcf-48a9-988f-d6c5fa65e350": {
                    allowedDoseUnits: [tablets]
                },
                "fa93993a-1283-4005-be18-c6019a362743": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "a6a2dca3-89f4-4f0f-99c8-2f7251b66030": {
                    allowedDoseUnits: [tablets, mg]
                },
                "2566fe0b-54a5-4705-b0fa-7247952e659d": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "c7254173-449d-4606-a63f-3b5ad76c9001": {
                    allowedDoseUnits: [tablets, mg]
                },
                "2e8ed1bd-3f33-463a-a7c5-18c773fe755e": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                },
                "16784ed5-75d5-4f39-9b38-9c7074897040": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "168a8192-cda4-4ce5-9224-77ae9b3b5ed8": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "af770c96-0b02-4efd-8622-0a9f1ae126b6": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "a38aa6c3-6676-44a2-a6f2-2bb25a7db16c": {
                    allowedDoseUnits: [tablets, mg]
                },
                "2d9e86ab-0ff3-48ed-b2ac-d6bf319b1551": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "0e9101f3-0e78-4325-a87b-f8911990ee80": {
                    allowedDoseUnits: [tablets, mg]
                },
                "0122347e-09ea-45b9-abe4-199fccf64125": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "e185bc0a-f46c-4a0e-a8eb-a7bd5991324c": {
                    allowedDoseUnits: [tablets, mg]
                },
                "8f6e550c-fee8-43b6-b9ae-5590de2ff44a": {
                    allowedDoseUnits: [tablets, mg]
                },
                "f5799e34-3a24-4ae9-b2d0-5385b554ddd8": {
                    allowedDoseUnits: [tablets, mg]
                },
                "9b53bcac-1f26-4d5c-af03-404c13e8e0fb": {
                    allowedDoseUnits: [tablets, mg]
                },
                "95e0777f-86fb-4341-ab2d-494338d131f0": {
                    allowedDoseUnits: [tablets, mg]
                },
                "ba04e9a7-8dd1-491e-b069-b87047841e89": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "74744b0a-2282-413a-8b0b-0d1cbd0e6479": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "56b6720f-c5ee-4388-a16c-3a2ca44c55a9": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "5d31dcd9-e227-43f5-bf82-1e4ec573c43e": {
                    allowedDoseUnits: [tablets, mg]
                },
                "7a0dece3-da44-4fea-9d48-ac2aaf211335": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "e66c73bc-b56a-4723-80af-e643183e8111": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "f75afb5e-9a64-4aef-bf60-fd5cfbc28d9c": {
                    allowedDoseUnits: [units]
                },
                "efc1b731-c801-4f8c-9af4-1356b0d49668": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "f02f8f79-c476-4ac8-9f64-43a72d1ef505": {
                    allowedDoseUnits: [tablets, mg]
                },
                "4d544b2e-bc78-41e9-9fc9-6075845d0ee5": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "69653f10-a98a-44fa-b34b-d60137b34dba": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                },
                "f8a816c7-1aa8-4375-9ec7-67a4b542201e": {
                    allowedDoseUnits: [application]
                },
                "79dacc67-82a1-4666-bf0b-f7f80bf83b4b": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "af310374-c28c-4768-bb4c-35854aa22dbc": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "aebda825-c36d-4e23-9b49-31a178672a80": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                },
                "52fdc84b-50af-44d7-9910-28285fd1e0b7": {
                    allowedDoseUnits: [drops]
                },
                "69732ff8-9faa-4923-8d5e-3dbeeb0d0993": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "5a1d40db-7cb5-44c5-b777-171d8df0f2cb": {
                    allowedDoseUnits: [application, mL]
                },
                "f88bc864-af9e-4dcf-a083-e5e433b21fca": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "ed96939b-a210-43de-b41a-d302d7480293": {
                    allowedDoseUnits: [tablets, mg]
                },
                "8b1d5805-0631-46b9-a646-1b579efb603f": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "63949b88-fee2-4c47-8d86-b4672300b0de": {
                    allowedDoseUnits: [application]
                },
                "00783558-07cc-45ef-98b2-403580632392": {
                    allowedDoseUnits: [tablets, mg]
                },
                "a8665977-feb8-4a11-a0e9-98ffd3f854a6": {
                    allowedDoseUnits: [tablets, mg]
                },
                "8fe6f453-2432-4347-aa18-99669c505214": {
                    allowedDoseUnits: [tablets, mg]
                },
                "4e6d8abe-2ced-4f2e-96f1-436e343b13d2": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "bed3a9b7-e62b-4cbf-9231-85de748e0b5b": {
                    allowedDoseUnits: [tablets, mg]
                },
                "4929beef-b569-4826-8658-6ced8f4344b1": {
                    allowedDoseUnits: [mg, mgkg, mcgkgmin]
                },
                "65c70378-c9b8-4cfd-b7a2-a2912e7b6db5": {
                    allowedDoseUnits: [mg, mgkg, mcgkgmin]
                },
                "00fbdb9d-0778-4526-9e6b-89bb1372779c": {
                    allowedDoseUnits: [tablets, mg]
                },
                "73f69f3d-a0c5-42f8-9084-665575e6b309": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "71d93f7f-be94-434e-a9cc-fff7abc3114c": {
                    allowedDoseUnits: [tablets, mg]
                },
                "064405b9-3d5f-4d4a-8b6b-6a410677990b": {
                    allowedDoseUnits: [mcg, mcgkg]
                },
                "b87e94f5-6e6b-4f7b-a5b3-eac4e7b8458e": {
                    allowedDoseUnits: [application]
                },
                "5087fea0-2dd7-435c-aed9-3e06fb43d84f": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "86c91534-dead-45b7-9910-60b375a0e3eb": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "ed624d34-e38e-46ee-b19e-7b0611f92318": {
                    allowedDoseUnits: [grams]
                },
                "5fd01459-8ed2-4976-9ecc-f7af4f23ac8a": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "1b06cd0f-d682-41e8-93b5-5364811d8b42": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "3bb845d5-e493-4149-aa0a-47dcc32b11f9": {
                    allowedDoseUnits: [tablets, mg]
                },
                "59bb14a0-570b-4e94-9131-81f590f3b0f5": {
                    allowedDoseUnits: [tablets, mg]
                },
                "90f7ed98-a91f-476b-ad62-4642991986f5": {
                    allowedDoseUnits: [tablets, mcg]
                },
                "d9ffb359-cf0f-4e1a-925f-83bdbf707284": {
                    allowedDoseUnits: [mL]
                },
                "76fddce2-0e3a-4aff-afcd-a6f9967ea85e": {
                    allowedDoseUnits: [tablets, mg]
                },
                "490b265e-c56a-4345-bec6-534557c5a786": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "bce30e31-14a2-4379-9f80-0c48c146ad59": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "ed696be8-58b9-401a-bc50-c4e10bc2655f": {
                    allowedDoseUnits: [tablets, mcg]
                },
                "ea63703f-aa20-4ac6-bf89-372c6e6738d5": {
                    allowedDoseUnits: [mcg, mcgkg]
                },
                "efdfbb8f-a806-4447-9c90-4a6195928dd2": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "0ac6f272-391f-48d0-9547-aa2d85808f39": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "cf7ce5ff-7ec9-4c3e-a24e-d243c7043339": {
                    allowedDoseUnits: [ampule]
                },
                "4fc4aa05-17bb-4f25-9466-2835fb7105fc": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "7ab1ef69-d3c1-4b46-b540-15d3c3ccc727": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "0cbde2ce-ac79-49dd-a56e-d471ff207015": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "6f6a32f1-ec19-444c-9ff1-f89bea447ed4": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "3fb4b076-26dc-49d1-937f-f0fac3aa21d9": {
                    allowedDoseUnits: [application]
                },
                "b4aeb78f-fcad-495b-ab2d-d45b125ece86": {
                    allowedDoseUnits: [tablets, mg]
                },
                "92277aec-fd64-4f6a-840d-d62b8b751ff5": {
                    allowedDoseUnits: [tablets, mg]
                },
                "342a8572-332c-40dc-8bcc-868877d720e4": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "1af0414c-54e9-4e55-a46f-75d5f1d9db2a": {
                    allowedDoseUnits: [tablets, mg]
                },
                "19e1034f-d2ef-4b62-ad38-9d53bcd0b4ea": {
                    allowedDoseUnits: [tablets, mg]
                },
                "f9fdb287-440e-44ef-944f-182c5a2b297b": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "d8d23c3a-83d1-4c4f-9a9d-07e63d6560b5": {
                    allowedDoseUnits: [tablets, mg]
                },
                "fa716ca9-9bbf-4a24-8b5d-f2f733078c5f": {
                    allowedDoseUnits: [mg]
                },
                "9c84da48-fc97-49de-af83-4da42de89ee4": {
                    allowedDoseUnits: [tablets, mg]
                },
                "eaf42edf-3d1b-451b-bdc6-58ca9cb6c995": {
                    allowedDoseUnits: [tablets, mg]
                },
                "99e1eca8-e58d-48cb-ba1c-6e98cdb38f50": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "371644da-08b3-49fd-8689-c6c9e88fa774": {
                    allowedDoseUnits: [tablets, mg]
                },
                "b4198f42-81ee-43e6-8544-de8d6fd2efac": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "04e6539d-03e5-4eb1-b5bb-2f71b00b9fc0": {
                    allowedDoseUnits: [tablets, mg]
                },
                "9a95d145-19f4-4f2b-9df8-a595642c5716": {
                    allowedDoseUnits: [tablets, mg]
                },
                "8a0ab286-821a-4d77-bb7f-c5b54584dfdf": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "3bf57114-d8d3-4535-ab5a-c63a9ca076d7": {
                    allowedDoseUnits: [mcg, mcgkg]
                },
                "51e83055-cda6-4219-8211-11cc6433905e": {
                    allowedDoseUnits: [tablets, mg]
                },
                "46c4d4ce-ef1f-4e0f-b34c-8719e7fd1ebf": {
                    allowedDoseUnits: [grams, mg, mgkg]
                },
                "aa6ffbb2-9219-47eb-9d77-fb6f77899c1c": {
                    allowedDoseUnits: [drops]
                },
                "04a459b3-e4d9-44a2-a3c2-703d26cfe8cc": {
                    allowedDoseUnits: [grams]
                },
                "47345476-6109-4247-abfa-bf24b89d1bf8": {
                    allowedDoseUnits: [mL]
                },
                "1d8a1cd0-f09f-4b39-bea0-404865183320": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "21062ba0-74b3-4230-ad1e-30552fa4f540": {
                    allowedDoseUnits: [grams]
                },
                "6667c944-6fd3-41bf-80e9-63ef614dfdfe": {
                    allowedDoseUnits: [tablets, mg]
                },
                "72de7da9-4101-476b-bcd2-3678a8d72f61": {
                    allowedDoseUnits: [tablets, mg]
                },
                "ac53fa7f-56c0-4fc1-accc-c089ca347b22": {
                    allowedDoseUnits: [tablets, mg]
                },
                "93a93840-1af9-48e9-8380-7a5913da74a0": {
                    allowedDoseUnits: [units]
                },
                "aafd4167-b5b0-43dd-90ee-a281a1948a1f": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "4ae1a572-dd5d-41fb-bfa3-1b73e4e43900": {
                    allowedDoseUnits: [tablets, mg]
                },
                "4ff38ddf-442d-42df-91cd-1071734a2299": {
                    allowedDoseUnits: [mEq]
                },
                "041f6836-6fb1-4e7d-bf12-237276ca1f42": {
                    allowedDoseUnits: [tablets, mg]
                },
                "853386b3-95a5-42bc-8781-04546c5ba79e": {
                    allowedDoseUnits: [tablets, mg]
                },
                "7c3c7c17-1e09-41fa-81eb-50e050628e81": {
                    allowedDoseUnits: [tablets, mg]
                },
                "734af8cb-55a3-4fcd-86e9-934285ff2da8": {
                    allowedDoseUnits: [tablets, mg]
                },
                "ff55378d-46a4-466b-ade1-040c04717960": {
                    allowedDoseUnits: [mL]
                },
                "0380f68b-6638-4d2e-ac4b-44661ad26e09": {
                    allowedDoseUnits: [mg, mgkg]
                },
                "4e15a74c-c05c-4a1d-ade6-7e598ee36ee1": {
                    allowedDoseUnits: [puffs]
                },
                "2245650a-39df-4187-9f09-24759de1b69e": {
                    allowedDoseUnits: [tablets, mg]
                },
                "2ac777c0-5467-49f3-bf8f-853deeb60189": {
                    allowedDoseUnits: [application]
                },
                "d4165513-2929-4ab0-ad60-457aa26b76fa": {
                    allowedDoseUnits: [mL]
                },
                "a0f0a008-747a-42f5-8052-decd5013cafa": {
                    allowedDoseUnits: [application]
                },
                "4147feff-1962-4fbb-b523-0e58551b9eef": {
                    allowedDoseUnits: [tablets, mg]
                },
                "a0f0a008-747a-42f5-8052-decd5013cfff": {
                    allowedDoseUnits: [tablets, mg]
                },
                "4147feff-1962-4fbb-b523-0e58551b9fff": {
                    allowedDoseUnits: [mg, mgkg, mL, mLkg]
                }
            },
            fluids: {
                list: [
                    {
                        display: ["Normal Saline"],
                        uuid: "80804AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Ringer's Lactate"],
                        uuid: "78617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Normal saline", "KCL 20 mmol/L"],
                        uuid: "162855AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Dextrose 5% in water"],
                        uuid: "104184AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Normal saline", "KCL 40 mmol/L"],
                        uuid: "162856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Dextrose 10% in water"],
                        uuid: "161922AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Phosphate Polyfusor"],
                        uuid: "162860AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Dextrose 5% in", "normal saline"],
                        uuid: "104745AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    },
                    {
                        display: ["Sodium Bicarbonate", "1.26% Polyfusor"],
                        uuid: "162861AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                    }
                ],
                routeOptions: [
                    {
                        display: 'IV',
                        uuid: '160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                    },
                    {
                        display: 'IO Needle',
                        uuid: '162624AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
                    }
                ],
                bolusAmountOptions: [50, 75, 100, 250, 500, 1000],
                bolusRateOptions: [15, 30, 60, 120],
                bolusUnits: mL,
                bolusRateUnits: minutes,
                infusionRateOptions: [kvo, 50, 75, 100, 125, 150, 200, 500],
                infusionDurationOptions: [continuous, 1, 2, 4, 6, 8, 12, 24],
                infusionRateNumeratorUnit: mL,
                infusionRateDenominatorUnit: hours,
                infusionDurationUnits: hours
            }
        }
    });
