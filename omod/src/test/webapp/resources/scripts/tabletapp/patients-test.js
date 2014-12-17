describe('patients', function () {

    var apiUrl = '///ws/rest/v1/';

    beforeEach(function () {
        module('tabletapp');
    });

    describe('WardController', function () {
        var httpMock,
            sessionSpy,
            initController,
            injector;

        beforeEach(function () {
            inject(function ($controller, $httpBackend, $injector, $rootScope) {
                httpMock = $httpBackend;
                sessionSpy = jasmine.createSpyObj('CurrentSession', ['setRecentWard'])
                initController = function (stateParams) {
                    $controller('WardController', {$scope: $rootScope.$new(), $state: {params: stateParams}, CurrentSession: sessionSpy});
                }
            });
        });

        describe('loading a ward controller', function () {
            it('should save the ward uuid', function () {
                initController({uuid: "NEW WARD"})
                expect(sessionSpy.setRecentWard).toHaveBeenCalledWith('NEW WARD');
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
                    httpMock.when('GET', apiUrl + 'patient').respond({});
                    $controller('PatientController', {$scope: scope});
                }
            });
        });

        describe('loading a patient controller', function () {
            it('needsAReason returns true if the status is not full', function () {
                initController()
                expect(scope.needsAReason({ })).toBeFalsy();
                expect(scope.needsAReason({status: 'full' })).toBeFalsy();
                expect(scope.needsAReason({status: 'partial' })).toBeTruthy();
                expect(scope.needsAReason({status: 'not_given' })).toBeTruthy();
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
                scope.saveAdministeredDose(dose, order);
                httpMock.flush();
            })

            it('includes reason if needed', function () {
                initController()
                var order = {uuid: 'ORDER UUID'},
                    dose = {status: 'not_given', reasonNotAdministered: 'Patient Illnesses'},
                    expectedScheduledDosePost = {
                        status: 'not_given',
                        order: 'ORDER UUID',
                        reasonNotAdministeredNonCoded: 'Patient Illnesses'
                    };
                httpMock.expectPOST(apiUrl + 'ebola/scheduled-dose', expectedScheduledDosePost).respond({});
                scope.saveAdministeredDose(dose, order);
                httpMock.flush();
            })
        })
    });
});
