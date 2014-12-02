var OPENMRS_CONTEXT_PATH = 'ebola';

angular.module("tabletapp", ['locationService', 'ui.router'])

.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/wards');

    $stateProvider
        .state('wards', {
           url: '/wards',
           templateUrl: 'templates/wards.html'
        })
        .state('ward', {
            url: '/ward/:uuid',
            templateUrl: 'templates/ward.html'
        });
})

.controller("listWardsCtrl", [ '$scope', 'LocationService', function($scope, LocationService) {

        $scope.suspectWards = [];
        $scope.confirmedWards = [];
        $scope.recoveryWards = [];

        LocationService.getLocations({tag: "Ebola Suspect Ward"}).then(function(result) {
            $scope.suspectWards = result;
        });
        LocationService.getLocations({tag: "Ebola Confirmed Ward"}).then(function(result) {
            $scope.confirmedWards = result;
        });
        LocationService.getLocations({tag: "Ebola Recovery Ward"}).then(function(result) {
            $scope.recoveryWards = result;
        });

    }])

.controller("WardController", [ '$state', '$scope', 'LocationService', function($state, $scope, LocationService) {
    var wardId = $state.params.uuid;

    PatientService.getPatients({ward: wardId}).then(function (patients) {
        $scope.patients = patients.results;
    })
}]);