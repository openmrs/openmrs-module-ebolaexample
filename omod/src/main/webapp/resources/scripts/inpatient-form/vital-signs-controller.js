var module = angular.module('inpatientForm');

module.controller("vitalSignsController", function($scope, $location) {
  $scope.viewModel = {
    currentConsciousness: "",
    temperature: "",
    oxygenSaturation: "",
    respiratoryRate: "",
    heartRate: "",
    systolicBP: "",
    diastolicBP: "",
    raisedJVP: "",
    capillaryRefillTime: "",
    abdomenTender: "",
    paleAnaemia: ""
  };
});
