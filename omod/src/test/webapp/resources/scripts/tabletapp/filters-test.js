describe('filter specs', function () {

    beforeEach(module('filters'));

    describe('fluidOrder.infusion', function () {
        var order;

        beforeEach(function () {
            order = {administrationType: 'INFUSION'}
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
    });
});