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
                    httpMock.expectGET(apiUrl + 'patient').respond({});
                    httpMock.expectGET(apiUrl + 'order?t=drugorder&v=full').respond({});
                    $controller('PatientController', {$scope: scope});
                }
            });
        });

        describe('loading a patient controller', function () {
            it('should set the statuses as an array of strings', function () {
                initController()
                expect(scope.administrationStatuses).toEqual(["Fully Given","Partially Given","Not Given"]);
            })

            it('needsAReason returns true if the status is not full', function () {
                initController()
                expect(scope.needsAReason({ })).toBeFalsy();
                expect(scope.needsAReason({status: constants.administrationStatuses.full })).toBeFalsy();
                expect(scope.needsAReason({status: constants.administrationStatuses.partial })).toBeTruthy();
                expect(scope.needsAReason({status: constants.administrationStatuses.notGiven })).toBeTruthy();
            })
        })
    });
});
