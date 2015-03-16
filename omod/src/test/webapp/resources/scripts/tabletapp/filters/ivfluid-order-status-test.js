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

    it("should set status as STARTED with dateCreated", inject(function (ivfliuidStatusFilter) {
        ivfluidOrderStatus.dateCreated = 1426484460000;//"2015-03-16 13:41")
        ivfluidOrderStatus.status = 'STARTED';
        var result = ivfliuidStatusFilter(ivfluidOrderStatus);

        expect(result).toBe("STARTED: 16 Mar 13:41");
    }));
});