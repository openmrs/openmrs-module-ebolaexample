angular.module("patients", ["ui.router", "resources", "ngDialog", "constants"])

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

    .controller("WardController", [ "$state", "$scope", "WardResource", function ($state, $scope, WardResource) {
        var wardId = $state.params.uuid;
        $scope.ward = WardResource.get({ uuid: wardId });

    }])

    .controller("PatientController", [ "$state", "$scope", "PatientResource", "OrderResource", "ngDialog", function ($state, $scope, PatientResource, OrderResource, ngDialog) {
        var patientId = $state.params.patientUUID;

        $scope.patient = PatientResource.get({ uuid: patientId });
        OrderResource.query({ t: "drugorder", patient: patientId }, function (response) {
            $scope.activeOrders = response.results;
        });

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

