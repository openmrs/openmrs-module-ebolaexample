describe('directives tests', function () {
    beforeEach(module('directives', 'iv-fluid-orders-service'));

    describe('fluidName', function () {
        var $compile,
            $rootScope,
            IvFluidOrderService;

        beforeEach(inject(function (_$compile_, _$rootScope_, _IvFluidOrderService_) {
            $compile = _$compile_;
            $rootScope = _$rootScope_;
            IvFluidOrderService = _IvFluidOrderService_;
        }));

        it('should return the name of the fluid', function () {
            spyOn(IvFluidOrderService, 'retrieveConcept').andReturn({display: ['name'], uuid: '123'});
            var element = $compile('<fluid-name uuid="123"></fluid-name>')($rootScope);
            expect(element.html()).toContain("name");
        });

        it('should return all parts of the name', function () {
            spyOn(IvFluidOrderService, 'retrieveConcept').andReturn({display: ['first', 'second'], uuid: '123'});
            var element = $compile('<fluid-name uuid="123"></fluid-name>')($rootScope);
            expect(element.html()).toContain("first second");
        });

        it('should use the tag content as a separator', function () {
            spyOn(IvFluidOrderService, 'retrieveConcept').andReturn({display: ['first', 'second'], uuid: '123'});
            var element = $compile('<fluid-name uuid="123"><br></fluid-name>')($rootScope);
            expect(element.html()).toContain("first<br>second");
        });
    });

});
