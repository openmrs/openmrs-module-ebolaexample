angular.module('prescription-service', ['tabletapp'])
    .service('PrescriptionService', ['Orders', 'Constants', 'CurrentSession', 'OrderResource',
        function (Orders, Constants, CurrentSession, OrderResource) {

            var orderJson = function(order, encounter) {
                var sessionInfo = CurrentSession.getInfo();
                var orderJson = {
                    "type": Constants.orderType.drugorder,
                    "patient": order.patient.uuid,
                    "concept": order.concept.uuid,
                    "encounter": encounter.uuid,
                    "careSetting": Constants.careSetting.inpatient,
                    "orderer": sessionInfo["provider"]["uuid"]
                }
                if (order.admType == 'Bolus') {
                    orderJson["bolusQuantity"] = order.bolusQuantity;
                    orderJson["bolusUnits"] = order.bolusUnits;
                    orderJson["duration"] = order.bolusRate;
                    orderJson["durationUnits"] = Constants.fluids.bolusRateUnit.uuid;
                } else if (order.dosingType == 'rounds') {

                }
            }


        }]
);