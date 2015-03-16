describe('ivfliuidStatus filter specs', function () {
    var Constants;

    beforeEach(module('filters'));

    beforeEach(inject(function (_Constants_) {
        Constants = _Constants_;
    }));

    beforeEach(function () {
        ivfluidOrderStatus = {uuid: null, status:'NOT_STARTED', dateCreated:null, order:{uuid:'abcd'}}
    });

    it('should set status as NOT STARTED without dateCreated', inject(function (ivfliuidStatusFilter) {
        var result = ivfliuidStatusFilter(ivfluidOrderStatus);

        expect(result).toBe("NOT STARTED");
    }));

    it("should set status as STARTED with dateCreated", inject(function (ivfliuidStatusFilter, dateFilter) {
        var date = new Date();
        var expected = dateFilter(date, "d MMM H:mm");
        ivfluidOrderStatus.dateCreated = date.getTime();
        ivfluidOrderStatus.status = 'STARTED';
        var result = ivfliuidStatusFilter(ivfluidOrderStatus);

        expect(result).toBe("STARTED: " + expected);
    }));
});