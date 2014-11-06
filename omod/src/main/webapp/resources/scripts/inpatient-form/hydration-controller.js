var module = angular.module('inpatientForm')

module.controller("hydrationController", function() {
  $scope.viewModel = {
    oralFluids: '',
    dehydration: '',
    urineOutput: '',
    vomiting: '',
    stoolFreq: '',
    mainStool: ''
  };
});
