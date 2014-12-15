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

        }])

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog", "$rootScope",
        function ($state, $scope, PatientResource, OrderResource, ngDialog, $rootScope) {
            var patientId = $state.params.patientUUID;

            $scope.patient = PatientResource.get({ uuid: patientId });
            function reloadActiveOrders() {
                OrderResource.query({ t: "drugorder", v: 'full', patient: patientId }, function (response) {
                    $scope.activeOrders = response.results;
                });
            }

            reloadActiveOrders();
            $rootScope.$on('$stateChangeSuccess', reloadActiveOrders);

            $scope.getPatientId = function () {
                return $scope.patient && $scope.patient.display && $scope.patient.display.split(" ")[0];
            };

            $scope.showAdminister = function (order) {
                $scope.administerDialogFor = order;
                ngDialog.open({
                    template: "administerDialog",
                    controller: "PatientController",
                    className: "ngdialog-theme-plain",
                    closeByDocument: false,
                    scope: $scope
                });
            };
        }]);

