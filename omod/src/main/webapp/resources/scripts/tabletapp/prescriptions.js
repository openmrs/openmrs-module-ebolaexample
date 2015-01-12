angular.module("prescriptions", ["tabletapp", "constants", "patients"])

    .controller("NewPrescriptionDetailsController", [ "$state", "$scope", "OrderResource", "Constants", "CurrentSession", "DrugResource",
        "ActiveOrders",
        function ($state, $scope, OrderResource, Constants, CurrentSession, DrugResource, ActiveOrders) {
            function setDosing(order, orderJson) {
                if (order.freeTextInstructions) {
                    orderJson["dosingType"] = Constants.dosingType.unvalidatedFreeText;
                    orderJson["dosingInstructions"] = order.instructions;
                } else {
                    var rounds = _.filter(Object.keys(order.rounds),function (key) {
                        return order.rounds[key];
                    }).join(", ");
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
            $scope.$watch('addOrder.rounds', function() {
                $scope.roundSelected = _.some(Object.keys($scope.addOrder.rounds), function (key) {
                    return $scope.addOrder.rounds[key];
                });
            }, true);
            $scope.save = function (order, newState) {
                if (order.form.$valid && (order.freeTextInstructions || $scope.roundSelected)) {
                    CurrentSession.getEncounter(order.patient.uuid).then(function (encounter) {
                        var sessionInfo = CurrentSession.getInfo();
                        var orderJson = {
                            "type": Constants.orderType.drugorder,
                            "patient": order.patient.uuid,
                            "drug": order.drug.uuid,
                            "encounter": encounter.uuid,
                            "careSetting": Constants.careSetting.inpatient,
                            "orderer": sessionInfo["provider"]["uuid"],
                            "concept": order.drug.concept.uuid
                        }
                        setDosing(order, orderJson);
                        new OrderResource(orderJson).$save().then(function (order) {
                            $state.params['uuid'] = CurrentSession.getRecentWard().uuid;
                            $state.params['prescriptionSuccess'] = true;
                            ActiveOrders.reload($scope, $state.params['patientUUID']);
                            $state.go(newState, $state.params);
                        }, function() {
                            $scope.serverError = true;
                        });
                    })
                } else {
                    $scope.hasErrors = true;
                }
            }
        }])

    .controller("NewPrescriptionController", [ '$state', '$scope', 'ConceptResource',
        function ($state, $scope, ConceptResource) {
            $scope.commonDrugConcepts = ConceptResource.query({formulary: true});
            $scope.$watch('concept', function (concept) {
                if(concept) {
                    $state.go('patient.addPrescriptionRoute', { concept: concept });
                }
            })
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

                    if($scope.drugs.length === 1) {
                        $state.go('patient.addPrescriptionDetails', { prescriptionInfo: $scope.drugs[0] });
                    }
                });
            }

            loadDrugs($state.params.concept);
        }
    ]);
