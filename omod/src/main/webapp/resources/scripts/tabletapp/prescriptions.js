angular.module("prescriptions", ["tabletapp", "constants", "patients"])

    .controller("NewPrescriptionDetailsController", ["$state", "$scope", "PrescriptionService", "PrescriptionSetup",
        function ($state, $scope, PrescriptionService, PrescriptionSetup) {
            var drug = PrescriptionSetup.buildDrug($state, $scope);
            PrescriptionSetup.setupScopeConstants($scope, $state.params.prescriptionInfo.uuid);
            PrescriptionSetup.setupStandardFunctions($scope);
            PrescriptionSetup.setupDrugOrder($scope, drug, $scope.patient);
            $scope.save = PrescriptionService.buildSaveHandler($scope, $state);
        }])

    .controller("EditPrescriptionDetailsController", ["$state", "$scope", "PrescriptionService",
        "PrescriptionSetup", "OrderResource",
        function ($state, $scope, PrescriptionService, PrescriptionSetup, OrderResource) {
            OrderResource.get({uuid: $state.params.orderUuid}, function (response) {
                var preexistingOrder = PrescriptionService.formOrderFromResponse(response);
                PrescriptionSetup.setupScopeConstants($scope);
                PrescriptionSetup.setupStandardFunctions($scope);
                PrescriptionSetup.setupDrugOrder($scope, undefined, $scope.patient, preexistingOrder);
                $scope.save = PrescriptionService.buildUpdateHandler($scope, $state);
            });
        }])

    .factory('PrescriptionSetup', ['Constants', 'DrugResource', 'PrescriptionService',
        function (Constants, DrugResource, PrescriptionService) {
            return {
                setupScopeConstants: function ($scope, drugUuid) {
                    $scope.orderedRoundNames = _.map(angular.copy(Constants.rounds), function (el) {
                        return el.name;
                    });

                    drugConfig = angular.copy(Constants.drugConfig);
                    drugDoseUnits = drugConfig[drugUuid];
                    allDoseUnits = angular.copy(Constants.doseUnits);
                    $scope.doseUnits = []

                    if (drugDoseUnits && drugDoseUnits.allowedDoseUnits.length > 0) {
                        for (i = 0; i < drugDoseUnits.allowedDoseUnits.length; i++) {
                            for (x = 0; x < allDoseUnits.length; x++) {
                                if (allDoseUnits[x].display == drugDoseUnits.allowedDoseUnits[i]) {
                                    $scope.doseUnits[$scope.doseUnits.length] = allDoseUnits[x];
                                    break;
                                }
                            }
                        }
                    }
                    if ($scope.doseUnits.length == 0) {
                        $scope.doseUnits = allDoseUnits;
                    }

                    $scope.routes = angular.copy(Constants.routes);
                    $scope.asNeededConditions = angular.copy(Constants.asNeededConditions);
                },
                setupStandardFunctions: function ($scope) {
                    $scope.clearServerError = function () {
                        $scope.serverError = null;
                    }
                }
                ,
                setupDrugOrder: function ($scope, drug, patient, preexistingOrder) {

                    $scope.$watch('addOrder.drug', function () {
                        console.log($scope.addOrder.drug);
                    });

                    var rounds = _.reduce(angular.copy(Constants.rounds), function (memo, val) {
                        memo[val.name] = false;
                        return memo
                    }, {});
                    var order = {
                        drug: drug,
                        patient: patient,
                        rounds: rounds
                    }
                    if (preexistingOrder) {
                        order = $.extend(true, {}, order, preexistingOrder);
                    }
                    else {
                        order.drug.doseUnits = $scope.doseUnits[0].uuid;
                    }

                    if ($scope.addOrder && $scope.addOrder.form) {
                        order.form = $scope.addOrder.form;
                    }
                    $scope.addOrder = order;
                    $scope.$watch('addOrder.drug.asNeeded', function () {
                        if ($scope.addOrder.drug && !$scope.addOrder.drug.asNeeded) {
                            $scope.addOrder.drug.asNeededCondition = '';
                        }
                    });
                    $scope.$watch('addOrder.rounds', function () {
                        $scope.roundSelected = _.some(Object.keys($scope.addOrder.rounds), function (key) {
                            return $scope.addOrder.rounds[key];
                        });
                    }, true);
                }
                ,
                buildDrug: function ($state, $scope) {
                    var drug = {};
                    if ($state.params.prescriptionInfo && $state.params.prescriptionInfo.uuid) {
                        drug = DrugResource.get({uuid: $state.params.prescriptionInfo.uuid}, function (response) {
                            $scope.routeProvided = drug.route;
                            drug.route = drug.route || {};
                        });
                    } else {
                        drug = $state.params.prescriptionInfo;
                        $scope.routeProvided = drug.route;
                        drug.route = drug.route || {};
                    }
                    return drug;
                }
            }
        }])

    .
    controller("NewPrescriptionController", ['$state', '$scope', 'ConceptResource',
        function ($state, $scope, ConceptResource) {
            $scope.commonDrugConcepts = ConceptResource.query({formulary: true});
            $scope.$watch('concept', function (concept) {
                if (concept) {
                    $state.go('patient.addPrescriptionRoute', {concept: concept});
                }
            })
        }
    ])

    .controller("NewPrescriptionRouteController", ['$state', '$scope', 'DrugResource', 'ConceptResource',
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
                    var rep = {
                        display: drug.display,
                        route: null,
                        uuid: drug.uuid,
                        concept: concept
                    };
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

                    if ($scope.drugs.length === 1) {
                        $state.go('patient.addPrescriptionDetails', {prescriptionInfo: $scope.drugs[0]});
                    }
                });
            }

            loadDrugs($state.params.concept);
        }
    ]);
