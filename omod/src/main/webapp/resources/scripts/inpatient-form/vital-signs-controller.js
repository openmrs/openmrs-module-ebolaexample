var module = angular.module('inpatientForm')

module.controller("vitalSignsController", function($scope) {
  $scope.viewModel = {
    currentConsciouness: "",
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
