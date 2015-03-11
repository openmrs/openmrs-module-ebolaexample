angular.module("orders", [])

    .factory("DrugOrders", ['OrdersService', function (OrdersService) {
        return new OrdersService('drugorder');
    }])

    .factory("FluidOrders", ['OrdersService', function (OrdersService) {
        return new OrdersService('ivfluidorder');
    }])

    .factory("OrdersService", ['OrderResource', function (OrderResource) {
        return function (orderType) {
            this.orderType = orderType;

            var activeOnly = true;
            var cachedActive;
            var cachedPast;

            function sortable(date) {
                // Handle case where client and server aren't using the same timezone.
                // (I'm sure there's a better way to do this, but I have no internet now and can't search)
                if (typeof date === "string") {
                    date = new Date(date);
                }
                var ret = "";
                ret += date.getUTCFullYear();
                ret += date.getUTCMonth() < 10 ? ("0" + date.getUTCMonth()) : date.getUTCMonth();
                ret += date.getUTCDate() < 10 ? ("0" + date.getUTCDate()) : date.getUTCDate();
                ret += date.getUTCHours() < 10 ? ("0" + date.getUTCHours()) : date.getUTCHours();
                ret += date.getUTCMinutes() < 10 ? ("0" + date.getUTCMinutes()) : date.getUTCMinutes();
                ret += date.getUTCSeconds() < 10 ? ("0" + date.getUTCSeconds()) : date.getUTCSeconds();
                return ret;
            }

            function decorateOrders(orders) {
                var now = sortable(new Date());
                return _.map(orders, function (order) {
                    order.actualStopDate = order.dateStopped ? order.dateStopped : (
                        order.autoExpireDate && sortable(order.autoExpireDate) <= now ?
                            order.autoExpireDate :
                            null
                    );
                    return order;
                });
            }

            function groupByDrug(orders) {
                var grouped;
                if (orders) {
                    orders = _.sortBy(orders, 'dateActivated').reverse();
                    var drugs = _.map(_.uniq(_.pluck(_.pluck(orders, 'drug'), 'uuid')), function (uuid) {
                        return {
                            uuid: uuid,
                            orders: []
                        }
                    });
                    _.each(orders, function (order) {
                        _.findWhere(drugs, {uuid: order.drug.uuid}).orders.push(order);
                    });
                    grouped = _.sortBy(drugs, function (group) {
                        return group.orders[0].dateActivated;
                    }).reverse();
                }
                return grouped;
            }

            this.reload = function (scope, patientUuid) {
                scope.loading = true;
                OrderResource.query({t: orderType, v: 'full', patient: patientUuid}, function (response) {
                    scope.loading = false;
                    cachedActive = decorateOrders(response.results);

                });
                scope.loadingPastOrders = true;
                OrderResource.query({
                    t: orderType,
                    v: 'full',
                    patient: patientUuid,
                    expired: true
                }, function (response) {
                    scope.loadingPastOrders = false;
                    cachedPast = decorateOrders(response.results);
                });
            };
            this.setActiveOnly = function (newVal) {
                activeOnly = newVal;
            };
            this.getActiveOnly = function () {
                return activeOnly;
            };
            this.get = function () {
                var orders;
                if (activeOnly) {
                    orders = cachedActive;
                } else {
                    orders = _.union(cachedActive, cachedPast);
                }
                if (orderType == 'drugorder') {
                    var groupedOrders = groupByDrug(orders);
                }
                return {
                    activeOnly: activeOnly,
                    none: orders && (orders.length == 0),
                    orders: orders,
                    groupedOrders: groupedOrders
                };
            }
        }
    }]);

