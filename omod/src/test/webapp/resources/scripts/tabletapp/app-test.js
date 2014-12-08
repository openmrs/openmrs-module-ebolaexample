describe('app', function(){

    var appUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('tabletapp');
    });

    describe('AddPrescriptionController', function () {

        var httpMock,
            scope,
            encounterResponseStub = {
                uuid: 'ENCOUNTER_ID'
            },
            orderResponseStub = {
                uuid: 'NEW ORDER UUID'
            },
            sessionInfoResponseStub = {
                "userId": "USER_UUID",
                "personId": "PERSON_UUID",
                "providers": [
                    {
                        "uuid": "PROVIDER_1_UUID"
                    }
                ]
            },
            order = {
                patient: {
                    uuid: 'PATIENT_UUID'
                },
                drug: {
                    uuid: 'DRUG_UUID',
                    instructions: 'Drug instructions'
                }
            };

        beforeEach(function () {
            inject(function ($controller, $rootScope, $httpBackend) {
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                httpMock.when('POST', appUrl + 'encounter').respond(encounterResponseStub);
                httpMock.when('POST', appUrl + 'order').respond(orderResponseStub);
                httpMock.when('GET', appUrl + 'ebola/session-info').respond(sessionInfoResponseStub);
                httpMock.when('GET', 'templates/wards.html').respond({});
                $controller('AddPrescriptionController', {$scope: scope});
            });
        });

        it('should save newly created order', function () {
            scope.save(order);
            httpMock.flush();
            expect(scope.newOrder['uuid']).toEqual('NEW ORDER UUID');
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
                httpMock.when('POST', appUrl + 'encounter').respond(encounterResponseStub);
                currentSession = $injector.get('CurrentSession');
            });
        });

        describe('getEncounter', function() {
            it('should create encounter using the rest endpoint', function () {
                httpMock.expectPOST(appUrl + 'encounter', function(dataString) {
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
                httpMock.expectPOST(appUrl + 'encounter', function(dataString) {
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
                httpMock.expectPOST(appUrl + 'encounter', function(dataString) {
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

                httpMock.expectPOST(appUrl + 'encounter')
                currentSession.getEncounter('DIFFERENT PATIENT UUID');
                httpMock.flush();

                httpMock.expectPOST(appUrl + 'encounter')
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
            })
        })
    });
});
