angular.module('iv-fluid-orders-service', ['tabletapp'])
    .service('IvFluidOrdersService', ['Orders', 'Constants', 'CurrentSession', 'OrderResource',
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
                    orderJson["bolusRate"] = order.bolusRate;
                    orderJson["bolusRateUnits"] = Constants.fluids.bolusRateUnits.uuid;
                } else if (order.admType == 'Infusion') {
                    orderJson["infusionRate"] = order.infusionRate;
                    orderJson["infusionRateNumeratorUnit"] = Constants.fluids.infusionRateNumeratorUnit;
                    orderJson["infusionRateDenominatorUnit"] = Constants.fluids.infusionRateDenominatorUnit;
                    orderJson["infusionDuration"] = order.infusionDuration;
                    orderJson["infusionDurationUnits"] = order.infusionDurationUnits;
                }
            }


        }]
);