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

        var httpMock,
            scope,
            initController,
            drugsResponse = {
                "results": [
                    {
                        "display": "Acetaminophen 160 MG Oral Tablet",
                        "uuid": "1326AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 160 MG Oral Tablet",
                        "dosageForm": {
                            "uuid": "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "Tablet"
                        },
                        "route": {
                            "uuid": "160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "Oral administration"
                        }
                    },
                    {
                        "display": "Acetaminophen 360 MG Oral Tablet",
                        "uuid": "1328AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 360 MG Oral Tablet",
                        "dosageForm": {
                            "uuid": "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "Tablet"
                        },
                        "route": {
                            "uuid": "160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "Oral administration"
                        }
                    },
                    {
                        "display": "Acetaminophen 10 MG/KG IV",
                        "uuid": "1329AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 10 MG/KG IV",
                        "dosageForm": {
                            "uuid": "1517AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "Suspension"
                        },
                        "route": {
                            "uuid": "160299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                            "display": "IV"
                        }
                    },
                    {
                        "display": "Acetaminophen 25 MG/ML Oral Solution",
                        "uuid": "1327AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                        "name": "Acetaminophen 25 MG/ML Oral Solution",
                        "dosageForm": null,
                        "route": null
                    }
                ]
            };

        beforeEach(function () {
            inject(function ($controller, $rootScope, $httpBackend) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                httpMock.when('GET', apiUrl + 'concept/654321').respond({uuid: '654321'});
                httpMock.when('GET', apiUrl + 'drug?concept=654321&v=full').respond(drugsResponse);
                httpMock.when('GET', 'templates/wards.html').respond({});
                initController = function (stateParams) {
                    var state = stateParams || { params: {concept: {uuid: '654321'} } };
                    $controller('NewPrescriptionRouteController', {$scope: scope, $state: state});
                }
            });
        });

        it('should create drug display from route/forms if possible', function () {
            initController();
            httpMock.flush();
            scope.$digest();
            expect(scope.drugs).toEqual([
                { display: "Oral administration - Tablet",
                    route: {
                        "uuid": "160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "display": "Oral administration"
                    },
                    uuid: null,
                    concept: { uuid: '654321' } },
                { display: "IV - Suspension",
                    route: {
                        "uuid": "160299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "display": "IV"
                    },
                    uuid: "1329AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                    concept: { uuid: '654321' } },
                { display: "Acetaminophen 25 MG/ML Oral Solution",
                    route: null,
                    uuid: "1327AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                    concept: { uuid: '654321' } }
            ]);
        });

        it('should set concept on order from state params', function () {
            var concept = {uuid: '654321', display: 'Concept from Service'};
            initController({ params: { concept: concept } });
            httpMock.flush();
            scope.$digest();
            expect(scope.drugs[0].concept).toEqual(concept);
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
            injector;

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
                freeTextInstructions: false,
                form: {$valid: true}
            };

            inject(function ($state, $controller, $rootScope, $httpBackend, $injector) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                scope.addOrder = {};
                httpMock.when('GET', 'templates/wards.html').respond({});
                httpMock.flush();
                state = $state;
                spyOn(state, 'go');
                injector = $injector;
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                httpMock.when('POST', apiUrl + 'order').respond(orderResponseStub);
                httpMock.when('GET', apiUrl + 'drug/999').respond({concept: {uuid: '0987654'}});
                httpMock.when('GET', apiUrl + 'order?t=drugorder&v=full').respond({});
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
            order['freeTextInstructions'] = true;
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
            order['rounds'] = scope.addOrder['rounds']
            scope.save(order, 'anywhere');
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
            expect(scope.hasErrors).toBeTruthy();
            expect(scope.roundSelected).toBeFalsy();
        });

        it('should set serverError if there is a problem saving', function () {
            initController({prescriptionInfo: 'some wild params'});
            order['freeTextInstructions'] = true;
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
            order['freeTextInstructions'] = true;
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

        it('should set routeProvided if the route is provided by params', function () {
            initController({
                prescriptionInfo: {
                    uuid: null,
                    route: {
                        uuid: "",
                        display: ""}
                }});
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
            this.expect(scope.routeProvided).toBeTruthy();
        });

        it('should set routeProvided to falsey if the route is not provided by params', function () {
            initController({
                prescriptionInfo: {
                    uuid: null,
                    route: null
                }});
            httpMock.verifyNoOutstandingExpectation();
            httpMock.verifyNoOutstandingRequest();
            this.expect(scope.routeProvided).toBeFalsy();
        });

        it('should set routeProvided to truthy if the route is provided by web service', function () {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({route: {uuid: '12345678'}});
            initController({prescriptionInfo: {uuid: "1234"}});
            httpMock.flush();
            scope.$digest();
            this.expect(scope.routeProvided).toBeTruthy();
        });

        it('should set routeProvided to falsy if the route is not provided by web service', function () {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({route: null});
            initController({prescriptionInfo: {uuid: "1234"}});
            httpMock.flush();
            scope.$digest();
            this.expect(scope.routeProvided).toBeFalsy();
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
