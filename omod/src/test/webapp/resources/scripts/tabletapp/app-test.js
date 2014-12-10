describe('app', function () {

    var apiUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('tabletapp');
    });

    describe('AddPrescriptionController', function () {

        var httpMock,
            scope,
            encounterResponseStub = { uuid: 'ENCOUNTER_ID' },
            orderResponseStub = { uuid: 'NEW ORDER UUID' },
            sessionInfoResponseStub = {
                user: { uuid: "USER_UUID" },
                person: { uuid: "PERSON_UUID" },
                providers: [
                    { uuid: "PROVIDER_1_UUID" }
                ]
            },
            order,
            expectedOrderPost,
            initController;

        beforeEach(function () {
            expectedOrderPost = {
                "type": "drugorder",
                "patient": "PATIENT_UUID",
                "drug": "DRUG_UUID",
                "encounter": "ENCOUNTER_ID",
                "careSetting": "c365e560-c3ec-11e3-9c1a-0800200c9a66",
                "orderer": "PROVIDER_1_UUID"
            };
            order  = {
                patient: { uuid: 'PATIENT_UUID' },
                drug: { uuid: 'DRUG_UUID' },
                instructions: 'Drug instructions',
                rounds: {},
                freeTextInstructions: false
            };

            inject(function ($controller, $rootScope, $httpBackend) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                httpMock.when('POST', apiUrl + 'order').respond(orderResponseStub);
                httpMock.when('GET', apiUrl + 'ebola/session-info').respond(sessionInfoResponseStub);
                httpMock.when('GET', apiUrl + 'drug').respond({});
                httpMock.when('GET', 'templates/wards.html').respond({});
                initController = function (stateParams) {
                    var state = stateParams || {params: {}};
                    $controller('AddPrescriptionController', {$scope: scope, $state: state});
                }
            });
        });

        it('should save newly created order with free text instructions', function () {
            initController();
            order['freeTextInstructions'] = true;
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.FreeTextDosingInstructions",
                "dosingInstructions": "Drug instructions"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order);
            httpMock.flush();
        });

        it('should save newly created order with round based instructions', function () {
            initController();
            order.drug['dose'] = 1;
            order.drug['doseUnits'] = 'DOSE UNITS UUID'
            order.drug['route'] = 'ROUTE UUID'
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "dose": 1,
                "doseUnits": "DOSE UNITS UUID",
                "route": "ROUTE UUID",
                "frequency": "",
                "dosingInstructions": ""
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order);
            httpMock.flush();
        });

        it('should interpret round selections as dosing instructions', function () {
            initController();
            order['rounds'] = {
                Morning: false,
                Afternoon: true,
                Evening: true,
                Night: false
            }
            var expectedPost = $.extend({}, expectedOrderPost, {
                "dosingType": "org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "frequency": "",
                "dosingInstructions": "Afternoon,Evening"
            });
            httpMock.expectPOST(apiUrl + 'order', expectedPost)
            scope.save(order);
            httpMock.flush();
        });

        it('should load full drug information from web service', function () {
            httpMock.expectGET(apiUrl + 'drug/1234').respond({info: "Some drug info"});
            initController({params: { drugUUID: '1234'}});
            httpMock.flush();

            this.expect(scope.addOrder.drug.info).toEqual("Some drug info");
        });

    });

    describe('CurrentSession', function () {
        var httpMock,
            scope,
            currentSession,
            encounterResponseStub = {
                uuid: 'ENCOUNTER_ID'
            };

        beforeEach(function () {
            inject(function ($controller, $rootScope, $httpBackend, $injector) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                currentSession = $injector.get('CurrentSession');
            });
        });

        describe('getEncounter', function () {
            it('should create encounter using the rest endpoint', function () {
                httpMock.expectPOST(apiUrl + 'encounter', function (dataString) {
                    var data = JSON.parse(dataString),
                        patietnUUIDMatches = data['patient'] == 'PATIENT UUID',
                        dateExists = data['encounterDatetime'] != undefined,
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && dateExists && encounterTypeMatches;
                })
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
            })

            it('should cache encounters', function () {
                httpMock.expectPOST(apiUrl + 'encounter', function (dataString) {
                    var data = JSON.parse(dataString),
                        patietnUUIDMatches = data['patient'] == 'PATIENT UUID',
                        dateExists = data['encounterDatetime'] != undefined,
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && dateExists && encounterTypeMatches;
                })
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
                currentSession.getEncounter('PATIENT UUID');
                httpMock.verifyNoOutstandingExpectation();
                httpMock.verifyNoOutstandingRequest();
            })

            it('should only cache encounters for one patient at a time', function () {
                httpMock.expectPOST(apiUrl + 'encounter', function (dataString) {
                    var data = JSON.parse(dataString),
                        patietnUUIDMatches = data['patient'] == 'PATIENT UUID',
                        dateExists = data['encounterDatetime'] != undefined,
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && dateExists && encounterTypeMatches;
                })
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
                currentSession.getEncounter('PATIENT UUID');
                httpMock.verifyNoOutstandingExpectation();
                httpMock.verifyNoOutstandingRequest();

                httpMock.expectPOST(apiUrl + 'encounter')
                currentSession.getEncounter('DIFFERENT PATIENT UUID');
                httpMock.flush();

                httpMock.expectPOST(apiUrl + 'encounter')
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
            })
        })
    });
});
