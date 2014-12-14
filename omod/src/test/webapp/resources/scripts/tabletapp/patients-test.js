describe('patients', function () {



    beforeEach(function () {
        module('tabletapp');
    });

    describe('WardController', function () {
        var httpMock,
            currentSession,
            initController;

        beforeEach(function () {
            inject(function ($controller, $httpBackend, $injector, $rootScope) {
                httpMock = $httpBackend;
                currentSession = $injector.get('CurrentSession');
                initController = function (stateParams) {
                    $controller('WardController', {$scope: $rootScope.$new(), $state: {params: stateParams}});
                }
            });
        });

        describe('loading a ward controller', function () {
            it('should save the ward uuid', function () {
                this.expect(currentSession.getRecentWard()).toEqual("");
                initController({uuid: "NEW WARD"})
                this.expect(currentSession.getRecentWard()).toEqual("NEW WARD");
            })
        })
    });
});
