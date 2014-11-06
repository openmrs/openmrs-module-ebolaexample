var module = angular.module('inpatientForm')

module.controller("dailyManagementController", function() {
  $scope.viewModel = {
    fluidManagement: '',
    volume: '',
    antimalarials: '',
    antibiotics: '',
    analgesics: ''
  };
});



