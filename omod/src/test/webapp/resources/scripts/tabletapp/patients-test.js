describe('patients', function () {



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
});
