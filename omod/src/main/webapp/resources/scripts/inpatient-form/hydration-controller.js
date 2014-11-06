var module = angular.module('inpatientForm');

module.controller("hydrationController", function($scope) {
  $scope.viewModel = {
    oralFluids: '',
    dehydration: '',
    urineOutput: '',
    vomiting: '',
    stoolFreq: '',
    mainStool: ''
  };
});
