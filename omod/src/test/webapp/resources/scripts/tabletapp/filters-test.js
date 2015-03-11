describe('filter specs', function () {
    var Constants;

    beforeEach(module('filters'));

    beforeEach(inject(function (_Constants_) {
        Constants = _Constants_;
    }));

    describe('fluidOrder.infusion', function () {
        var order;

        beforeEach(function () {
            order = {administrationType: 'INFUSION', route: Constants.fluids.routeOptions[0]}
        });

        it('should format the rate and duration', inject(function (fluidOrderFilter) {
            order.infusionRate = 10;
            order.infusionDuration = 20;
            var filtered = fluidOrderFilter(order);

            expect(filtered).toContain('10');
            expect(filtered).toContain('20');
        }));

        it("should use the label 'KVO' when rate is zero", inject(function (fluidOrderFilter) {
            order.infusionRate = 0;
            order.infusionDuration = 20;
            var filtered = fluidOrderFilter(order);
            expect(filtered).toContain('KVO');
        }));

        it("should use the label 'continuous' when duration is zero", inject(function (fluidOrderFilter) {
            order.infusionRate = 10;
            order.infusionDuration = 0;
            var filtered = fluidOrderFilter(order);
            expect(filtered).toContain('continuous');
        }));

        it('should include the route name', inject(function (fluidOrderFilter) {
            var io = { display: 'IO Needle', uuid: '123'};
            spyOn(Constants.fluids, 'routeOptions').andReturn([io]);
            order.infusionRate = 10;
            order.infusionDuration = 20;
            order.route = io;
            var filtered = fluidOrderFilter(order);

            expect(filtered).toContain('IO Needle');
        }));

    });
});