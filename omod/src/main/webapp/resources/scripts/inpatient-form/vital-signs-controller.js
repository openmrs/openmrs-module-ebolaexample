var module = angular.module('inpatientForm')

module.controller("vitalSignsController", function($scope, $location) {
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

  $scope.sendData = function() {
    console.log("potato");
    $scope.emit("vital-sign-data", $scope.viewModel);
    $location.url('/hydratation');
  };
});
