var OPENMRS_CONTEXT_PATH = location.pathname.substring(1, location.pathname.indexOf('/', 1));

angular.module("tabletapp", ['locationService', 'ui.router', 'ngResource', 'uicommons.widget.select-drug'])

.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/wards');

    $stateProvider
        .state('wards', {
           url: '/wards',
           templateUrl: 'templates/wards.html'
        })
        .state('ward', {
            url: '/wards/:uuid',
            templateUrl: 'templates/ward.html'
        })
        .state('patient', {
            url: '/patients/:uuid',
            templateUrl: 'templates/patient.html'
        })
        .state('patient.overview', {
            url: '/overview',
            templateUrl: 'templates/patient/overview.html'
        })
        .state('patient.addPrescription', {
            url: '/addPrescription',
            templateUrl: 'templates/patient/prescriptionForm.html'
        });
    })

    .factory("WardResource", [ '$resource', function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/ebola/ward/:uuid", {
            uuid: '@uuid'
        },{
            query: { method:'GET' }
        });
    }])

    .factory("PatientResource", [ '$resource', function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/patient/:uuid", {
            uuid: '@uuid'
        },{
            query: { method:'GET' }
        });
    }])

    .factory("OrderResource", [ '$resource', function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/order/:uuid", {
            uuid: '@uuid'
        },{
            query: { method:'GET' }
        });
    }])


    .controller("ListWardsController", [ '$scope', 'WardResource', function($scope, WardResource) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];

        WardResource.query({ v: 'default' }, function(response) {
            var results = response.results;
            $scope.suspectWards = _.where(results, { type: 'suspect'});
            $scope.confirmedWards = _.where(results, { type: 'confirmed'});
            $scope.recoveryWards = _.where(results, { type: 'recovery'});
        });
    }])

.controller("WardController", [ '$state', '$scope', 'WardResource', function($state, $scope, WardResource) {
    var wardId = $state.params.uuid;
    $scope.ward = WardResource.get({ uuid: wardId });

}])

.controller("PatientController", [ '$state', '$scope', 'PatientResource', 'OrderResource', function($state, $scope, PatientResource, OrderResource) {
    var patientId = $state.params.uuid;

    $scope.patient = PatientResource.get({ uuid: patientId });
    OrderResource.query({ t: "drugorder", patient: patientId }, function(response) {
        $scope.activeOrders = response.results;
    });
}])

.controller("AddPrescriptionController", [ '$state', '$scope', 'OrderResource', function($state, $scope, OrderResource) {
        $scope.addOrder = {
            patient: $scope.patient
        };

        $scope.save = function(order) {
            window.alert("Saving order for " + order.patient.display);
        }
    }]);