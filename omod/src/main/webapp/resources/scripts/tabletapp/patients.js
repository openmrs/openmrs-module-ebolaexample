angular.module("patients", ["ui.router", "resources", "ngDialog", "constants", "session"])

    .controller("ListWardsController", [ "$scope", "WardResource", function ($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];

        WardResource.query({ v: "default" }, function (response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, { type: "suspect"});
            $scope.confirmedWards = _.where(results, { type: "confirmed"});
            $scope.recoveryWards = _.where(results, { type: "recovery"});
        });
    }])

    .controller("WardController", [ "$state", "$scope", "WardResource", "CurrentSession",
        function ($state, $scope, WardResource, CurrentSession) {
            var wardId = $state.params.uuid;
            CurrentSession.setRecentWard(wardId);
            $scope.ward = WardResource.get({ uuid: wardId });

            function toCamelCase(sentenceCase) {
                var out = "";
                sentenceCase.split(" ").forEach(function (el, idx) {
                    var add = el.toLowerCase();
                    out += (idx === 0 ? add : add[0].toUpperCase() + add.slice(1) + ' ');
                });
                return out;
            }

            $scope.getPatientName = function (display) {
                return toCamelCase(display.split( / (.+)/)[1].split('-')[1]);
            };

            $scope.getPatientId = function (display) {
                return display.split( / (.+)/)[0];
            };
        }])

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog",
        "$rootScope", "Constants", "ScheduledDoseResource",
        function ($state, $scope, PatientResource, OrderResource,
                  ngDialog, $rootScope, Constants, ScheduledDoseResource) {
            var patientId = $state.params.patientUUID;

            $scope.patient = PatientResource.get({ uuid: patientId });
            function reloadActiveOrders() {
                OrderResource.query({ t: "drugorder", v: 'full', patient: patientId }, function (response) {
                    $scope.activeOrders = response.results;
                });
            }

            reloadActiveOrders();
            $rootScope.$on('$stateChangeSuccess', reloadActiveOrders);

            $scope.focusInput = function ($event) {
                angular.element($event.target).parent().addClass('highlight');
            };

            $scope.blurInput = function ($event) {
                angular.element($event.target).parent().removeClass('highlight');
            };

            $scope.getPatientId = function () {
                return $scope.patient && $scope.patient.display && $scope.patient.display.split(" ")[0];
            };

            $scope.showAdminister = function (order) {
                $scope.administerOrder = order;
                ngDialog.open({
                    template: "administerDialog",
                    controller: "PatientController",
                    className: "ngdialog-theme-plain",
                    closeByDocument: false,
                    scope: $scope
                });
            };

            $scope.administrationStatuses = Constants.administrationStatuses;
            $scope.reasonsNotAdministered = Constants.reasonsNotAdministered;
            var needsAReason = function(administeredDose) {
                if(administeredDose && administeredDose.status) {
                    return administeredDose.status == 'partial'
                        || administeredDose.status == 'not_given'
                }
                return false;
            };
            $scope.needsAReason = needsAReason;
            $scope.saveAdministeredDose = function (dose, order) {
                var doseJSON = {
                    status: dose.status,
                    order: order.uuid
                };
                if(needsAReason(dose)) {
                    doseJSON['reasonNotAdministeredNonCoded'] = dose.reasonNotAdministered;
                }
                new ScheduledDoseResource(doseJSON).$save();
            };
        }]);

