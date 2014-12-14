var OPENMRS_CONTEXT_PATH = location.pathname.substring(1, location.pathname.indexOf("/", 1));

angular.module("tabletapp", ["ui.router", "ngResource", "ngDialog", "uicommons.widget.select-drug", "constants"])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("/wards");

        $stateProvider
            .state("wards", {
                url: "/wards",
                templateUrl: "templates/wards.html"
            })
            .state("ward", {
                url: "/wards/:uuid",
                templateUrl: "templates/ward.html"
            })
            .state("patient", {
                url: "/patients/:patientUUID",
                templateUrl: "templates/patient.html"
            })
            .state("patient.overview", {
                url: "/overview/:patientUUID",
                templateUrl: "templates/patient/overview.html"
            })
            .state("patient.addPrescription", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescription.html"
            })
            .state("patient.addPrescriptionRoute", {
                url: "/addPrescription",
                templateUrl: "templates/patient/newPrescriptionRoute.html",
                params: { concept: null }
            })
            .state("patient.addPrescriptionDetails", {
                url: "/addPrescription",
                templateUrl: "templates/patient/prescriptionForm.html",
                params: { prescriptionInfo: null }
            });
    })

    .factory("WardResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/ward/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("PatientResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("OrderResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/order/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("EncounterResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("DrugResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/drug/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("ConceptResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/concept/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])


    .controller("ListWardsController", [ "$scope", "WardResource", function ($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];

        WardResource.query({ v: "default" }, function (response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, { type: "suspect"});
            $scope.confirmedWards = _.where(results, { type: "confirmed"});
            $scope.recoveryWards = _.where(results, { type: "recovery"});
        });
    }])

    .controller("WardController", [ "$state", "$scope", "WardResource", function ($state, $scope, WardResource) {
        var wardId = $state.params.uuid;
        $scope.ward = WardResource.get({ uuid: wardId });

    }])

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog", function ($state, $scope, PatientResource, OrderResource, ngDialog) {
        var patientId = $state.params.patientUUID;

        $scope.patient = PatientResource.get({ uuid: patientId });
        OrderResource.query({ t: "drugorder", patient: patientId }, function (response) {
            $scope.activeOrders = response.results;
        });

        $scope.getPatientId = function () {
            return $scope.patient && $scope.patient.display && $scope.patient.display.split(" ")[0];
        };

        $scope.showAdminister = function (order) {
            $scope.administerDialogFor = order;
            ngDialog.open({
                template: "administerDialog",
                controller: "PatientController",
                className: "ngdialog-theme-plain",
                closeByDocument: false,
                scope: $scope
            });
        };
    }])

    .factory("CurrentSession", ["$http", "Constants", "EncounterResource",
        function ($http, Constants, EncounterResource) {
            var cachedInfo,
                cachedEncounter,
                cachedEncounterPatientUUID;
            return {
                getInfo: function () {
                    if (!cachedInfo) {
                        cachedInfo = $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/session-info");
                    }
                    return cachedInfo;
                },
                getEncounter: function (patientUUID) {
                    if (cachedEncounter && cachedEncounterPatientUUID == patientUUID) {
                        return cachedEncounter;
                    }
                    cachedEncounterPatientUUID = patientUUID;
                    cachedEncounter = new EncounterResource({
                        "encounterDatetime": new Date().toJSON(),
                        "patient": patientUUID,
                        "encounterType": Constants.encounterType.ebolaInpatientFollowup
                    }).$save();
                    return cachedEncounter;
                }
            };
        }])

    .controller("AddPrescriptionController", [ "$state", "$scope", "OrderResource", "Constants", "CurrentSession", "DrugResource",
        function ($state, $scope, OrderResource, Constants, CurrentSession, DrugResource) {
            function setDosing(order, orderJson) {
                if (order.freeTextInstructions) {
                    orderJson["dosingType"] = Constants.dosingType.unvalidatedFreeText;
                    orderJson["dosingInstructions"] = order.instructions;
                } else {
                    var rounds = _.filter(Object.keys(order.rounds),function (key) {
                        return order.rounds[key];
                    }).join();
                    orderJson["dosingType"] = Constants.dosingType.roundBased;
                    orderJson["dose"] = order.drug.dose;
                    orderJson["doseUnits"] = order.drug.doseUnits;
                    orderJson["route"] = order.drug.route && order.drug.route.uuid;
                    orderJson["frequency"] = "";
                    orderJson["dosingInstructions"] = rounds;
                    orderJson["duration"] = order.drug.duration;
                    orderJson["durationUnits"] = Constants.durationUnits.days;
                    orderJson["asNeeded"] = order.drug.asNeeded;
                    orderJson["asNeededCondition"] = order.drug.asNeededCondition;
                }
            }

            var rounds = _.reduce(angular.copy(Constants.rounds), function (memo, val) {
                memo[val.name] = false;
                return memo
            }, {});

            $scope.orderedRoundNames = _.map(angular.copy(Constants.rounds), function (el) {
                return el.name;
            });

            var drug = {}
            if ($state.params.prescriptionInfo && $state.params.prescriptionInfo.uuid) {
                drug = DrugResource.get({ uuid: $state.params.prescriptionInfo.uuid }, function (response) {
                    $scope.routeProvided = drug.route;
                    drug.route = drug.route || {};
                });
            } else {
                drug = $state.params.prescriptionInfo;
                $scope.routeProvided = drug.route;
                drug.route = drug.route || {};
            }
            $scope.doseUnits = angular.copy(Constants.doseUnits);
            $scope.routes = angular.copy(Constants.routes);
            $scope.asNeededConditions = Constants.asNeededConditions;
            $scope.addOrder = {
                drug: drug,
                patient: $scope.patient,
                rounds: rounds
            };
            $scope.$watch('addOrder.drug.asNeeded', function () {
                if ($scope.addOrder.drug && !$scope.addOrder.drug.asNeeded) {
                    $scope.addOrder.drug.asNeededCondition = '';
                }
            })
            $scope.save = function (order, newState) {
                $scope.roundSelected = _.some(Object.keys(order.rounds),function (key) {
                    return order.rounds[key];
                });
                if (order.form.$valid && (order.freeTextInstructions || $scope.roundSelected)) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        CurrentSession.getInfo().then(function (response) {
                            var orderJson = {
                                "type": Constants.orderType.drugorder,
                                "patient": order.patient.uuid,
                                "drug": order.drug.uuid,
                                "encounter": encounter.uuid,
                                "careSetting": Constants.careSetting.inpatient,
                                "orderer": response.data.providers[0]["uuid"],
                                "concept": order.drug.concept.uuid
                            }
                            setDosing(order, orderJson);
                            new OrderResource(orderJson).$save().then(function (order) {
                                $state.go(newState, $state.params);
                            });
                        })

                    })
                } else {
                    $scope.hasErrors = true;
                }
            }
        }])

    .controller("NewPrescriptionController", [ '$state', '$scope', 'ConceptResource',
        function ($state, $scope, ConceptResource) {
            $scope.commonDrugConcepts = ConceptResource.query({formulary: true});
        }
    ])

    .controller("NewPrescriptionRouteController", [ '$state', '$scope', 'DrugResource', 'ConceptResource',
        function ($state, $scope, DrugResource, ConceptResource) {
            function removeDrugIdsFromDuplicates(mappedDrugs) {
                $.each(mappedDrugs, function (index, drug) {
                    var lastIndex = _.findLastIndex(mappedDrugs, function (d) {
                            return d.display == drug.display;
                        }),
                        firstIndex = _.findIndex(mappedDrugs, function (d) {
                            return d.display == drug.display;
                        });
                    if (lastIndex != firstIndex) {
                        drug.uuid = null;
                    }
                });
                return mappedDrugs;
            }

            function mapDrugsToSimpleRepresentation(drugs, concept) {
                return _.map(drugs, function (drug) {
                    var rep = { display: drug.display,
                        route: null,
                        uuid: drug.uuid,
                        concept: concept };
                    if (drug.route) {
                        var display = drug.route.display;
                        if (drug.dosageForm) {
                            display = display + " - " + drug.dosageForm.display
                        }
                        rep['display'] = display;
                        rep['route'] = drug.route;
                    }
                    return rep;
                });
            }

            function loadDrugs(concept) {
                DrugResource.query({concept: concept.uuid, v: 'full'}, function (response) {
                    var mappedDrugs = mapDrugsToSimpleRepresentation(response.results, concept);
                    mappedDrugs = removeDrugIdsFromDuplicates(mappedDrugs);
                    $scope.drugs = _.uniq(mappedDrugs, function (elem) {
                        return JSON.stringify(elem);
                    });
                });
            }
            loadDrugs($state.params.concept);
        }
    ]);
