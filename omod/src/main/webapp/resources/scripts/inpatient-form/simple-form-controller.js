var module = angular.module('inpatientForm');

module.controller('SimpleFormController', function($scope) {
    $scope.viewModel = {};

    $scope.$on('request-patient-info', function() {
        $scope.$emit('response-patient-info', $scope.viewModel);
    });
});
