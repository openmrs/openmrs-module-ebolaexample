angular.module("prescriptions", ["tabletapp", "constants", "patients", "filters", "constants"])

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

                    drugDoseUnits = null;
                    if (angular.copy(Constants.drugConfig)) {
                        drugDoseUnits = angular.copy(Constants.drugConfig)[drugUuid];
                    }

                    $scope.doseUnits = drugDoseUnits && drugDoseUnits.allowedDoseUnits && drugDoseUnits.allowedDoseUnits.length > 0 ?
                        drugDoseUnits.allowedDoseUnits : angular.copy(Constants.doseUnits);

                    $scope.routes = angular.copy(Constants.routes);
                    $scope.asNeededConditions = angular.copy(Constants.asNeededConditions);
                    $scope.dosingTypes = angular.copy(Constants.dosingType);
                },
                setupStandardFunctions: function ($scope) {
                    $scope.clearServerError = function () {
                        $scope.serverError = null;
                    }
                },
                setupDrugOrder: function ($scope, drug, patient, preexistingOrder) {
                    var rounds = _.reduce(angular.copy(Constants.rounds), function (memo, val) {
                        memo[val.name] = false;
                        return memo
                    }, {});
                    var order = {
                        drug: drug,
                        patient: patient,
                        rounds: rounds,
                        dosingType: 'rounds'
                    }
                    if (preexistingOrder) {
                        order = $.extend(true, {}, order, preexistingOrder);
                    }
                    else {
                        if (order.drug.$promise) {
                            order.drug.$promise.then(function () {
                                drugDoseUnits = null;
                                if (angular.copy(Constants.drugConfig)) {
                                    drugDoseUnits = angular.copy(Constants.drugConfig)[order.drug.uuid];
                                }
                                if (drugDoseUnits && drugDoseUnits.allowedDoseUnits
                                    && drugDoseUnits.allowedDoseUnits.length > 0) {
                                    order.drug.doseUnits = $scope.doseUnits[0].uuid;
                                }
                            })
                        } else {
                            drugDoseUnits = null;
                            if (angular.copy(Constants.drugConfig)) {
                                drugDoseUnits = angular.copy(Constants.drugConfig)[order.drug.uuid];
                            }
                            if (drugDoseUnits && drugDoseUnits.allowedDoseUnits
                                && drugDoseUnits.allowedDoseUnits.length > 0) {
                                order.drug.doseUnits = $scope.doseUnits[0].uuid;
                            }
                        }
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
                    $scope.$watch('addOrder.dosingType', function() {
                        $scope.complexDosing = $scope.addOrder.dosingType === 'text';
                    })
                },
                buildDrug: function ($state, $scope) {
                    var drug = {};
                    if ($state.params.prescriptionInfo && $state.params.prescriptionInfo.uuid) {
                        drug = DrugResource.get({uuid: $state.params.prescriptionInfo.uuid}, function (response) {
                            drug.route = drug.route || {};
                        });
                    } else {
                        drug = $state.params.prescriptionInfo;
                        drug.route = drug.route || {};
                    }
                    return drug;
                }
            }
        }])

    .controller("NewPrescriptionController", ['$state', '$scope', 'ConceptResource',
        function ($state, $scope, ConceptResource) {
            $scope.commonDrugConcepts = ConceptResource.query({formulary: true});
            $scope.$watch('concept', function (concept) {
                if (concept) {
                    $state.go('patient.addPrescriptionRoute', {concept: concept});
                }
            })
        }
    ])

    .controller("NewPrescriptionRouteController", ['$state', '$scope', 'DrugResource', 'ConceptResource', 'conceptFilter', 'Constants',
        function ($state, $scope, DrugResource, ConceptResource, conceptFilter, Constants) {

            function indexIn(value, referenceList) {
                if (!value) {
                    return "9999";
                }
                var found = _.findWhere(referenceList, {uuid: value.uuid});
                if (found) {
                    var index = "" + _.indexOf(referenceList, found);
                    while (index.length < 3) {
                        index = "0" + index;
                    }
                    return index;
                } else {
                    return "9999";
                }
            }

            function loadDrugs(conceptUUID) {
                DrugResource.query({concept: conceptUUID, v: 'full'}, function (response) {
                    if (response.results.length == 1) {
                        $state.go('patient.addPrescriptionDetails', {prescriptionInfo: response.results[0]});
                        return;
                    }
                    var results = _.map(response.results, function(drug) {
                        var groupSort = [indexIn(drug.route, Constants.routes), indexIn(drug.dosageForm, Constants.dosageForms)];
                        var groupDisplay = [conceptFilter(drug.route), conceptFilter(drug.dosageForm)];
                        var drugSort = [conceptFilter(drug.dosageForm), drug.uuid]; //drug.uuid for repeatable sorting
                        groupDisplay = _.filter(groupDisplay, function(it) { return it !== '' });
                        if (groupDisplay.length > 0) {
                            drug.groupDisplay = groupDisplay.join(" - ");
                        }
                        else {
                            drug.groupDisplay = "Need to specify route manually";
                        }
                        drug.groupSort = groupSort.join(" - ");
                        drug.drugSort = drugSort.join(" - ");
                        return drug;
                    });

                    var groups = _.groupBy(results, "groupSort");
                    $scope.drugs = _.map(groups, function(listOfDrugs) {
                        return {
                            group: listOfDrugs[0].groupDisplay,
                            sort: listOfDrugs[0].groupSort,
                            drugs: _.sortBy(listOfDrugs, "drugSort")
                        }
                    });
                    $scope.drugs = _.sortBy($scope.drugs, "sort");
                });
            }

            $scope.conceptUUID = $state.params.conceptUUID;
            loadDrugs($scope.conceptUUID);
        }
    ]);
