describe('app', function () {

    var apiUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('tabletapp');
    });

    beforeEach(function () {
        inject(function ($injector) {
            $injector.get('CurrentSession').setInfo({});
        })
    });

    describe('CurrentSession', function () {
        var httpMock,
            scope,
            currentSession,
            encounterResponseStub = {
                uuid: 'ENCOUNTER_ID'
            },
            constants;

        beforeEach(function () {
            inject(function ($controller, $rootScope, $httpBackend, $injector, Constants) {
                httpMock = $httpBackend;
                httpMock.when('GET', 'templates/wards.html').respond({});
                constants = Constants;
                scope = $rootScope.$new();
                httpMock.when('POST', apiUrl + 'encounter').respond(encounterResponseStub);
                currentSession = $injector.get('CurrentSession');
            });
        });

        describe('hasPrivilege', function() {
            it('should always return true for System Developer', function() {
                currentSession.setInfo({
                    "user": {
                        "privileges": [],
                        "roles": [
                            {
                                "uuid": "8d94f852-c2cc-11de-8d13-0010c6dffd0f",
                                "display": "System Developer"
                            }
                        ]
                    }
                });
                expect(currentSession.hasPrivilege("Any privilege")).toBeTruthy();
            });

            it('should return true if they have the specific privilege', function() {
                currentSession.setInfo({
                    "user": {
                        "privileges": [
                            {
                                "uuid": "8d94f852-c2cc-11de-8d13-0010c6dffd0f",
                                "display": "Privilege I have"
                            }
                        ],
                        "roles": []
                    }
                });
                expect(currentSession.hasPrivilege("Privilege I have")).toBeTruthy();
            });

            it('should return false if they do not have the privilege ', function() {
                currentSession.setInfo({
                    "user": {
                        "privileges": [],
                        "roles": [
                            {
                                "uuid": "8d94f852-c2cc-11de-8d13-0010c6dffd0f",
                                "display": "Provider"
                            }
                        ]
                    }
                });
                expect(currentSession.hasPrivilege("A privilege I don't have")).toBeFalsy();
            });
        });

        describe('getEncounter', function () {
            it('should create encounter using the rest endpoint', function () {
                httpMock.expectPOST(apiUrl + 'encounter', function (dataString) {
                    var data = JSON.parse(dataString),
                        patietnUUIDMatches = data['patient'] == 'PATIENT UUID',
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && encounterTypeMatches;
                })
                currentSession.getEncounter('PATIENT UUID');
                httpMock.flush();
            })

            it('should cache encounters', function () {
                httpMock.expectPOST(apiUrl + 'encounter', function (dataString) {
                    var data = JSON.parse(dataString),
                        patietnUUIDMatches = data['patient'] == 'PATIENT UUID',
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && encounterTypeMatches;
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
                        encounterTypeMatches = data['encounterType'] == '83413734-587d-11e4-af12-660e112eb3f5';
                    return patietnUUIDMatches && encounterTypeMatches;
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
