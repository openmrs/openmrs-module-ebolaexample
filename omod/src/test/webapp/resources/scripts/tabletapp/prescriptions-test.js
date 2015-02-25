describe('prescriptions', function () {

    var apiUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('prescriptions');
    });

    beforeEach(function () {
        inject(function ($injector) {
            $injector.get('CurrentSession').setInfo({});
        })
    });

    describe('NewPrescriptionRouteController', function () {

        var oral = {
            uuid: '160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
            display: 'Oral administration'
        };
        var iv = {
            "uuid": "160299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "display": "IV"
        };
        var tablet = {
            uuid : '1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
            display : 'Tablet'
        };
        var suspension = {
            "uuid": "1517AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "display": "Suspension"
        };

        var httpMock,
            scope,
            state,
            initController,
            drugsResponse = {
                "results": [
                    {
                        "display": "Acetaminophen 160 MG Oral Tablet",
                        "uuid": "1326AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 160 MG Oral Tablet",
                        "dosageForm": tablet,
                        "route": oral
                    },
                    {
                        "display": "Acetaminophen 360 MG Oral Tablet",
                        "uuid": "1328AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 360 MG Oral Tablet",
                        "dosageForm": tablet,
                        "route": oral
                    },
                    {
                        "display": "Acetaminophen 10 MG/KG IV",
                        "uuid": "1329AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 10 MG/KG IV",
                        "dosageForm": suspension,
                        "route": iv
                    },
                    {
                        "display": "Acetaminophen 25 MG/ML Oral Solution",
                        "uuid": "1327AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 25 MG/ML Oral Solution",
                        "dosageForm": null,
                        "route": null
                    }
                ]
            },
            justOneDrugResponse = {
                "results": [
                    {
                        "display": "Acetaminophen 160 MG Oral Tablet",
                        "uuid": "1326AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 160 MG Oral Tablet",
                        "dosageForm": tablet,
                        "route": oral
                    }
                ]
            };

        beforeEach(function () {
            inject(function ($controller, $rootScope, $state, $httpBackend) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                state = $state;
                spyOn(state, 'go');
                httpMock.when('GET', apiUrl + 'concept/654321').respond({uuid: '654321'});
                httpMock.when('GET', apiUrl + 'drug?concept=654321&v=full').respond(drugsResponse);
                httpMock.when('GET', apiUrl + 'drug?concept=1&v=full').respond(justOneDrugResponse);
                httpMock.when('GET', 'templates/wards.html').respond({});
                initController = function (stateParams) {
                    angular.extend(state, stateParams || { params: {conceptUUID: '654321' } });
                    $controller('NewPrescriptionRouteController', {$scope: scope, $state: state});
                }
            });
        });

        it('should create drug display from route/forms if possible', function () {
            initController();
            httpMock.flush();
            scope.$digest();
            expect(scope.drugs).toEqual([
                {
                    group: 'Oral - Tablet',
                    sort: '000 - 000',
                    drugs: [
                        {
                            display: 'Acetaminophen 160 MG Oral Tablet',
                            uuid: '1326AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                            name: 'Acetaminophen 160 MG Oral Tablet',
                            dosageForm: tablet,
                            route: oral,
                            groupDisplay: 'Oral - Tablet',
                            groupSort: '000 - 000',
                            drugSort : 'Tablet - 1326AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
                        }, {
                            display: 'Acetaminophen 360 MG Oral Tablet',
                            uuid: '1328AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                            name: 'Acetaminophen 360 MG Oral Tablet',
                            dosageForm : tablet,
                            route : oral,
                            groupDisplay : 'Oral - Tablet',
                            groupSort : '000 - 000',
                            drugSort : 'Tablet - 1328AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
                        }
                    ]
                }, {
                    group : 'IV - Suspension',
                    sort : '9999 - 9999',
                    drugs : [
                        {
                            display : 'Acetaminophen 25 MG/ML Oral Solution',
                            uuid : '1327AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                            name : 'Acetaminophen 25 MG/ML Oral Solution',
                            dosageForm : null,
                            route : null,
                            groupDisplay : 'Need to specify route manually',
                            groupSort : '9999 - 9999',
                            drugSort : ' - 1327AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
                        }, {
                            display : 'Acetaminophen 10 MG/KG IV',
                            uuid : '1329AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                            name : 'Acetaminophen 10 MG/KG IV',
                            dosageForm : suspension,
                            route : iv,
                            groupDisplay : 'IV - Suspension',
                            groupSort : '9999 - 9999',
                            drugSort : 'Suspension - 1329AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF'
                        }
                    ]
                }
            ]);
        });

        it('should go to next state with automatic selection if there is just one drug option', function() {
            var concept = {uuid: '1', display: 'Concept from Service'};
            initController({ params: { conceptUUID: concept.uuid } });
            httpMock.flush();
            scope.$digest();

            expect(state.go).toHaveBeenCalledWith('patient.addPrescriptionDetails',
                { prescriptionInfo: justOneDrugResponse.results[0], skipPrescriptionRoute: true });
        });

    });

    describe('NewPrescriptionDetailsController', function () {

        var httpMock,
            scope,
            encounterResponseStub = { uuid: 'ENCOUNTER_ID' },
            orderResponseStub = { uuid: 'NEW ORDER UUID' },
            sessionInfoResponseStub = {
                user: { uuid: "USER_UUID" },
                person: { uuid: "PERSON_UUID" },
                provider: { uuid: "PROVIDER_1_UUID" }
            },
            order,
            expectedOrderPost,
            initController,
            state,
            injector,
            window;

        beforeEach(function () {
            expectedOrderPost = {
                "type": "drugorder",
                "patient": "PATIENT_UUID",
                "drug": "DRUG_UUID",
                "encounter": "ENCOUNTER_ID",
                "careSetting": "c365e560-c3ec-11e3-9c1a-0800200c9a66",
                "orderer": "PROVIDER_1_UUID",
                "concept": 'CONCEPT_UUID'
            };
            order = {
                patient: { uuid: 'PATIENT_UUID' },
                drug: {
                    uuid: 'DRUG_UUID',
                    concept: { uuid: 'CONCEPT_UUID' }
                },
                doseUnits: {
                    uuid: '1234AAAA'
                },
                rounds: {Morning: true},
                dosingInstructions: 'Drug instructions',
                dosingType: 'rounds',
                form: {$valid: true}
            };

            inject(function ($state, $controller, $rootScope, $httpBackend, $injector, $window) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                scope.addOrder = {};
                httpMock.when('GET', 'templates/wards.html').respond({});
                httpMock.flush();
                state = $state;
                spyOn(state, 'go');
                window = $window;
                spyOn(window.history, 'back');
                injector = $injector;
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                httpMock.when('POST', apiUrl + 'order').respond(orderResponseStub);
                httpMock.when('GET', apiUrl + 'drug/999').respond({concept: {uuid: '0987654'}});
                httpMock.when('GET', apiUrl + 'order?t=drugorder&v=full').respond({});
                httpMock.when('GET', apiUrl + 'order?expired=true&t=drugorder&v=full').respond({});
                initController = function (stateParams, session) {
                    if (!session) {
                        session = $injector.get('CurrentSession');
                        spyOn(session, 'getRecentWard').andReturn({uuid: 'ward uuid'});
                    }
                    spyOn(session, "getInfo").andReturn(sessionInfoResponseStub);
                    state['params'] = stateParams || {prescriptionInfo: {uuid: '999'}};
                    $controller('NewPrescriptionDetailsController', {$scope: scope, $state: state});
                }
            });
        });

        it('should save newly created order with free text instructions', function () {
            initController();
            order['dosingType'] = 'text';
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.UnvalidatedFreeTextDosingInstructions",
                "dosingInstructions": "Drug instructions"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order, 'anywhere');
            httpMock.flush();
        });

        it('should not save if the form is not valid', function () {
            initController();
            httpMock.flush();
            order.form = {$valid: false};
            scope.save(order, 'anywhere');
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
            expect(scope.hasErrors).toBeTruthy();
        });

        it('should not save if no round is selected', function () {
            initController();
            httpMock.flush();
            scope.addOrder['rounds'] = {
                Morning: false,
                Afternoon: false,
                Evening: false,
                Night: false
            }
            order['dosingType'] = 'rounds';
            order['rounds'] = scope.addOrder['rounds'];
            order['dosingInstructions'] = null;
            scope.save(order, 'anywhere');
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
            expect(scope.hasErrors).toBeTruthy();
            expect(scope.roundSelected).toBeFalsy();
        });

        it('should set serverError if there is a problem saving', function () {
            initController({prescriptionInfo: 'some wild params'});
            order['dosingType'] = 'text';
            scope.addOrder['rounds'] = {
                Morning: true
            }
            order['rounds'] = scope.addOrder['rounds']
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.UnvalidatedFreeTextDosingInstructions",
                "dosingInstructions": "Drug instructions"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost).respond(500, {});
            scope.save(order, 'anywhere');
            httpMock.flush();
            this.expect(scope.serverError).toBeTruthy();
        });

        it('should save direct to desired state', function () {
            var currentSession = injector.get('CurrentSession');
            spyOn(currentSession, 'getRecentWard').andReturn({uuid: 'ward uuid'});
            initController({prescriptionInfo: 'some wild params'}, currentSession);
            order['dosingType'] = 'text';
            scope.addOrder['rounds'] = {
                Morning: false,
                Afternoon: false,
                Evening: false,
                Night: false
            }
            order['rounds'] = scope.addOrder['rounds']
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.UnvalidatedFreeTextDosingInstructions",
                "dosingInstructions": "Drug instructions"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order, 'anywhere');
            httpMock.flush();
            expect(state.go).toHaveBeenCalledWith('anywhere', {prescriptionInfo: 'some wild params', uuid: 'ward uuid', prescriptionSuccess: true});
        });

        it('should save newly created order with round based instructions', function () {
            initController();
            order.drug['dose'] = 1;
            order.drug['doseUnits'] = 'DOSE UNITS UUID';
            order.drug['route'] = { uuid: 'ROUTE UUID' };
            scope.addOrder['rounds'] = {
                Morning: true,
                Afternoon: false,
                Evening: false,
                Night: false
            }
            order['rounds'] = scope.addOrder['rounds']
            scope.$digest();
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "dose": 1,
                "doseUnits": "DOSE UNITS UUID",
                "route": "ROUTE UUID",
                "frequency": "",
                "dosingInstructions": "Morning",
                "durationUnits": "1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order);
            httpMock.flush();
        });

        it('should interpret round selections as dosing instructions', function () {
            initController();
            scope.addOrder['rounds'] = {
                Morning: false,
                Afternoon: true,
                Evening: true,
                Night: false
            }
            order['rounds'] = scope.addOrder['rounds']
            scope.$digest();
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "frequency": "",
                "dosingInstructions": "Afternoon, Evening",
                "durationUnits": "1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order);
            httpMock.flush();
        });

        it('should set and reset asNeededCondition according to asNeeded value', function () {
            initController();
            httpMock.flush();

            this.expect(scope.addOrder.drug.asNeeded).toBeFalsy();
            scope.addOrder.drug.asNeededCondition = "Something";
            scope.$digest();
            scope.addOrder.drug.asNeeded = false;
            scope.$digest();
            this.expect(scope.addOrder.drug.asNeededCondition).toEqual("");
        });

        it('should load full drug information from web service', function () {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({info: "Some drug info"});
            initController({prescriptionInfo: {uuid: "1234"}});
            httpMock.flush();

            this.expect(scope.addOrder.drug.info).toEqual("Some drug info");
        });

        it('should not load full drug information from web service if there is no drug uuid', function () {
            initController({prescriptionInfo: {uuid: null}});
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
        });

        it('should return to drug selection state when there is only one route', function() {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({info: "Some drug info"});
            initController({prescriptionInfo: {uuid: "1234"}, skipPrescriptionRoute: true});
            httpMock.flush();

            scope.goToPrev();

            expect(state.go).toHaveBeenCalledWith('patient.addPrescription', {});
        });

        it('should return to route selection state when there is more than one route', function() {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({info: "Some drug info"});
            initController({prescriptionInfo: {uuid: "1234"}, skipPrescriptionRoute: false});
            httpMock.flush();

            scope.goToPrev();

            expect(window.history.back).toHaveBeenCalled();
        });
    });

    describe('EditPrescriptionDetailsController', function () {

        var httpMock,
            scope,
            encounterResponseStub = { uuid: 'ENCOUNTER_ID' },
            orderResponseStub = { uuid: 'NEW ORDER UUID' },
            sessionInfoResponseStub = {
                user: { uuid: "USER_UUID" },
                person: { uuid: "PERSON_UUID" },
                provider: { uuid: "PROVIDER_1_UUID" }
            },
            order,
            orderJson,
            initController,
            state;

        beforeEach(function () {

            orderJson = {
                "uuid":"48d9fbbd-36b6-4c16-be18-01411d41b23e",
                "instructions":null,
                "display":"Allopurinol 100 MG Oral Tablet: 33 Microgram Oral administration each Morning, Evening for 4 Days <span class=\"lozenge prn\">PRN Anxiety</span>",
                "drug":{"uuid":"1361AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF","display":"Allopurinol 100 MG Oral Tablet"},
                "dosingType":"org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "dose":33.0,
                "doseUnits":{"uuid":"162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Microgram"},
                "asNeeded":true,
                "asNeededCondition":"Anxiety",
                "quantity":null,
                "quantityUnits":null,
                "dosingInstructions":"Morning, Evening",
                "duration":4,
                "durationUnits":{"uuid":"1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Days"},
                "route":{"uuid":"160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Oral administration"},
                "type":"drugorder"
            };


            inject(function ($state, $controller, $rootScope, $httpBackend, $injector) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                scope.addOrder = {};
                httpMock.when('GET', 'templates/wards.html').respond({});
                httpMock.flush();
                state = $state;
                spyOn(state, 'go');
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                httpMock.when('POST', apiUrl + 'order').respond(orderResponseStub);
                initController = function (stateParams) {
                    var session = $injector.get('CurrentSession');
                    spyOn(session, 'getRecentWard').andReturn({uuid: 'ward uuid'});
                    spyOn(session, 'getInfo').andReturn(sessionInfoResponseStub);
                    state['params'] = stateParams || {orderUuid: '999'};
                    $controller('EditPrescriptionDetailsController', {$scope: scope, $state: state});
                }
            });
        });

        it('should load order information from web service', function () {
            httpMock.expectGET(apiUrl + 'order/4321').respond(orderJson);
            initController({orderUuid: '4321'});
            httpMock.flush();
            this.expect(scope.addOrder.drug).toEqual({
                route : {
                    uuid : '160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
                    display : 'Oral administration'
                },
                uuid : '1361AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                display: 'Allopurinol 100 MG Oral Tablet',
                dose : 33,
                doseUnits : '162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
                duration : 4,
                asNeeded : true,
                asNeededCondition : 'Anxiety'
            });
            this.expect(scope.addOrder.rounds).toEqual({
                Morning : true,
                Afternoon : false,
                Evening : true,
                Night : false
            });
        });
    });
});
