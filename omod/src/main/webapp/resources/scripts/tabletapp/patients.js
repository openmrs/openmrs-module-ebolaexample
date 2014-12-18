angular.module("patients", ["ui.router", "resources", "ngDialog", "constants", "session"])

    .controller("ListWardsController", [ "$scope", "WardResource", function ($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];
        $scope.loading = true;

        WardResource.query({ v: "default" }, function (response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, { type: "suspect"});
            $scope.confirmedWards = _.where(results, { type: "confirmed"});
            $scope.recoveryWards = _.where(results, { type: "recovery"});
            $scope.loading = false;
        });
    }])

    .controller("WardController", [ "$state", "$scope", "WardResource", "CurrentSession",
        function ($state, $scope, WardResource, CurrentSession) {
            $scope.loading = true;
            var wardId = $state.params.uuid;
            CurrentSession.setRecentWard(wardId);
            $scope.ward = WardResource.get({ uuid: wardId }, function() {
                $scope.loading = false;
            });

            function toCamelCase(sentenceCase) {
                var out = "";
                sentenceCase.split(" ").forEach(function (el, idx) {
                    var add = el.toLowerCase();
                    out += (idx === 0 ? add : add[0].toUpperCase() + add.slice(1) + ' ');
                });
                return out;
            };

            $scope.getPatientName = function (display) {
                return toCamelCase(display.split( / (.+)/)[1].split('-')[1]);
            };

            $scope.getPatientId = function (display) {
                return display.split( / (.+)/)[0];
            };
        }])

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog",
        "$rootScope", "Constants", "ScheduledDoseResource", "CurrentSession",
        function ($state, $scope, PatientResource, OrderResource,
                  ngDialog, $rootScope, Constants, ScheduledDoseResource, CurrentSession) {
            var patientId = $state.params.patientUUID;

            $scope.patient = PatientResource.get({ uuid: patientId });
            function reloadActiveOrders(event, toState, toParams, fromState, fromParams) {
                $scope.loading = true;
                $scope.comeFromPrescriptionForm = $state.params.prescriptionSuccess == 'true' && fromState && fromState.name == 'patient.addPrescriptionDetails';
                OrderResource.query({ t: "drugorder", v: 'full', patient: patientId }, function (response) {
                    $scope.activeOrders = response.results;
                    $scope.loading = false;
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

            $scope.getWard = function() {
                return CurrentSession.getRecentWard();
            };

            $scope.showAdminister = function (order) {
                $scope.problemSaving = false;
                $scope.hasErrors = false;
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
                    return administeredDose.status == 'PARTIAL'
                        || administeredDose.status == 'NOT_GIVEN'
                }
                return false;
            };
            $scope.needsAReason = needsAReason;
            $scope.saveAdministeredDose = function (dose, order, callback) {
                if(dose.status) {
                    var doseJSON = {
                        status: dose.status,
                        order: order.uuid
                    };
                    if(needsAReason(dose)) {
                        doseJSON['reasonNotAdministeredNonCoded'] = dose.reasonNotAdministeredNonCoded;
                    }
                    new ScheduledDoseResource(doseJSON).$save().then(function() {
                        callback();
                    }, function () {
                        $scope.problemSaving = true;
                    });
                } else {
                    $scope.hasErrors = true;
                }
            };
        }]);

