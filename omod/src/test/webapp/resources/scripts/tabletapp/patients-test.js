describe('patients', function () {

    var apiUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('tabletapp');
    });

    beforeEach(function () {
        inject(function ($injector) {
            $injector.get('CurrentSession').setInfo({});
        })
    });

    describe('WardController', function () {
        var httpMock,
            sessionSpy,
            initController,
            injector;

        beforeEach(function () {
            inject(function ($controller, $httpBackend, $injector, $rootScope) {
                httpMock = $httpBackend;
                httpMock.when('GET', 'templates/wards.html').respond({});
                httpMock.when('GET', apiUrl + 'ebola/ward/NEWWARD').respond({uuid: "NEWWARD", display: "WARD display"});
                sessionSpy = jasmine.createSpyObj('CurrentSession', ['setRecentWard'])
                initController = function (stateParams) {
                    $controller('WardController', {$scope: $rootScope.$new(), $state: {params: stateParams}, CurrentSession: sessionSpy});
                }
            });
        });

        describe('loading a ward controller', function () {
            it('should save the ward object', function () {
                initController({uuid: "NEWWARD"});
                httpMock.flush();
                expect(sessionSpy.setRecentWard).toHaveBeenCalledWith({uuid: "NEWWARD", display: "WARD display"});
            })
        })
    });

    describe('PatientController', function () {
        var httpMock,
            scope,
            initController,
            constants;

        beforeEach(function () {
            inject(function ($controller, $httpBackend, $rootScope, Constants) {
                constants = Constants;
                httpMock = $httpBackend;
                scope = $rootScope.$new();
                initController = function (stateParams) {
                    httpMock.when('GET', 'templates/wards.html').respond({});
                    httpMock.when('GET', apiUrl + 'order?t=drugorder&v=full').respond({});
                    httpMock.when('GET', apiUrl + 'order?t=ivfluidorder&v=full').respond({});
                    httpMock.when('GET', apiUrl + 'order?expired=true&t=drugorder&v=full').respond({});
                    httpMock.when('GET', apiUrl + 'order?expired=true&t=ivfluidorder&v=full').respond({});
                    httpMock.when('GET', apiUrl + 'patient').respond({});
                    httpMock.when('GET', apiUrl + 'ebola/dosehistory').respond({});
                    httpMock.when('GET', apiUrl + 'ebola/feature-toggle').respond({});
                    $controller('PatientController', {$scope: scope});
                }
            });
        });

        describe('loading a patient controller', function () {
            it('needsAReason returns true if the status is not full', function () {
                initController()
                scope.closeThisDialog = function(){};
                expect(scope.needsAReason({ })).toBeFalsy();
                expect(scope.needsAReason({status: 'FULL' })).toBeFalsy();
                expect(scope.needsAReason({status: 'PARTIAL' })).toBeTruthy();
                expect(scope.needsAReason({status: 'NOT_GIVEN' })).toBeTruthy();
            })
        })

        describe('administering a dose', function () {
            it('sends data to server', function () {
                initController()
                var order = {uuid: 'ORDER UUID'},
                    dose = {status: 'FULL'},
                    expectedScheduledDosePost = {
                        status: 'FULL',
                        order: 'ORDER UUID'
                    };
                httpMock.expectPOST(apiUrl + 'ebola/scheduled-dose', expectedScheduledDosePost).respond({});
                scope.saveAdministeredDose(dose, order, function() {});
                httpMock.flush();
            })

            it('includes reason if needed', function () {
                initController()
                var order = {uuid: 'ORDER UUID'},
                    dose = {status: 'NOT_GIVEN', reasonNotAdministeredNonCoded: 'Patient Illnesses'},
                    expectedScheduledDosePost = {
                        status: 'NOT_GIVEN',
                        order: 'ORDER UUID',
                        reasonNotAdministeredNonCoded: 'Patient Illnesses'
                    };
                httpMock.expectPOST(apiUrl + 'ebola/scheduled-dose', expectedScheduledDosePost).respond({});
                scope.saveAdministeredDose(dose, order, function() {});
                httpMock.flush();
            })

            it('uses the callback', function () {
                initController()
                var order = {uuid: 'ORDER UUID'},
                    dose = {status: 'FULL', reasonNotAdministeredNonCoded: ''},
                    expectedScheduledDosePost = { status: 'FULL', order: 'ORDER UUID' };
                httpMock.expectPOST(apiUrl + 'ebola/scheduled-dose', expectedScheduledDosePost).respond({});
                var called = false;
                scope.saveAdministeredDose(dose, order, function() {
                    called = true;
                });
                httpMock.flush();
                expect(called).toBeTruthy();
            })

            it('dont save without status', function () {
                initController()
                httpMock.flush();
                var order = {uuid: 'ORDER UUID'},
                    dose = {status: null, reasonNotAdministeredNonCoded: ''};
                scope.saveAdministeredDose(dose, order, function() {});
                httpMock.verifyNoOutstandingExpectation();
                httpMock.verifyNoOutstandingRequest();
                expect(scope.hasErrors).toBeTruthy();
            })
        })
    });
});
