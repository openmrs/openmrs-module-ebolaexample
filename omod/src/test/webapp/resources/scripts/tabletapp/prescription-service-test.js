describe('prescription-service', function () {

    beforeEach(function () {
        module('prescription-service');
    });

    describe('PrescriptionService', function () {

        var orderJson,
            service,
            constants;

        beforeEach(function () {
            orderJson = {
                "uuid":"48d9fbbd-36b6-4c16-be18-01411d41b23e",
                "orderNumber":"ORD-155",
                "patient":{"uuid":"2df9af7e-0e0f-420a-bbda-3817b01def9b","display":"10000X - Andrew Clarke"},
                "concept":{"uuid":"70879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"ALLOPURINOL SODIUM"},
                "action":"NEW","careSetting":{"uuid":"c365e560-c3ec-11e3-9c1a-0800200c9a66","display":"Inpatient"},
                "previousOrder":null,
                "dateActivated":"2015-01-14T11:15:42.000-0600",
                "dateStopped":null,
                "autoExpireDate":"2015-01-18T11:15:42.000-0600",
                "encounter":{"uuid":"c197807d-1a2a-4f8f-9168-e4951612ad31","display":"Ebola Inpatient Followup 01/14/2015"},
                "orderer":{"uuid":"f9badd80-ab76-11e2-9e96-0800200c9a66","display":"UNKNOWN - Unknown Provider"},
                "orderReason":null,
                "orderReasonNonCoded":null,
                "urgency":"ROUTINE",
                "instructions":null,
                "commentToFulfiller":null,
                "display":"Allopurinol 100 MG Oral Tablet: 33 Microgram Oral administration each Morning, Evening for 4 Days <span class=\"lozenge prn\">PRN Anxiety</span>",
                "drug":{"uuid":"1361AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF","display":"Allopurinol 100 MG Oral Tablet"},
                "dosingType":"org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions",
                "dose":33.0,
                "doseUnits":{"uuid":"162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Microgram"},
                "frequency":null,
                "asNeeded":true,
                "asNeededCondition":"Anxiety",
                "quantity":null,
                "quantityUnits":null,
                "numRefills":null,
                "dosingInstructions":"Morning, Evening",
                "duration":4,
                "durationUnits":{"uuid":"1072AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Days"},
                "route":{"uuid":"160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","display":"Oral administration"},
                "brandName":null,
                "dispenseAsWritten":false,
                "type":"drugorder",
                "resourceVersion":"1.10"};
            inject(function(PrescriptionService, Constants) {
                service = PrescriptionService;
                constants = Constants;
            })
        });

        it('should parse rounds from dosing instructions', function () {
            var order = service.formOrderFromResponse(orderJson);
            this.expect(order.rounds).toEqual({ Morning : true, Evening : true });
        });

        it('should parse drug from information from drug json', function () {
            var order = service.formOrderFromResponse(orderJson);
            this.expect(order.drug).toEqual({
                route : {
                    uuid : '160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
                    display : 'Oral administration' },
                concept : {
                    uuid : '70879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
                    display : 'ALLOPURINOL SODIUM' },
                uuid : '1361AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF',
                display: 'Allopurinol 100 MG Oral Tablet',
                dose : 33,
                doseUnits : '162366AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA',
                duration : 4,
                asNeeded : true,
                asNeededCondition : 'Anxiety'
            });
        });

        it('should parse free text dosing instructions', function () {
            orderJson['dosingType'] = constants.dosingType.unvalidatedFreeText;
            orderJson['dosingInstructions'] = "Some Free Text";
            var order = service.formOrderFromResponse(orderJson);
            this.expect(order.freeTextInstructions).toBeTruthy();
            this.expect(order.dosingInstructions).toEqual("Some Free Text");
            this.expect(order.drug).toEqual({ route: {}, concept : { uuid : '70879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', display : 'ALLOPURINOL SODIUM' }, uuid : '1361AFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF', display: 'Allopurinol 100 MG Oral Tablet'});
        });
    });
});
