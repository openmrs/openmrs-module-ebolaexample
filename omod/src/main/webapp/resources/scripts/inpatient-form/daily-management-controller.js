var module = angular.module('inpatientForm')

module.controller("dailyManagementController", function($scope) {
  $scope.viewModel = {
    fluidManagement: '',
    volume: '',
    alAct: '',
    artesunate: '',
    quinine: '',
    ceftriaxone: '',
    cefixime: '',
    metronidazole: '',
    paracetamol: '',
    tramadol: '',
    morphine: ''
  };
});
