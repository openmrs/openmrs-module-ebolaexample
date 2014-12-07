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

        it('should retrieve current users permissions', function () {
            scope.save(order);
            httpMock.flush();
            expect(scope.newOrder['uuid']).toEqual('NEW ORDER UUID');
        });

    });
});
