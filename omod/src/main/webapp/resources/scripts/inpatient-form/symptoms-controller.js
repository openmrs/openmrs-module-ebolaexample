var module = angular.module('inpatientForm')

module.controller('symptomsController', function() {
  $scope.viewModel = {
    overallSymptoms: '',
    dehydration: '',
    bleeding: '',
    jointMusclePain: '',
    headche: '',
    urinePain: '',
    abdominalPain: '',
    unableToEat: '',
    unableToDrink: '',
    difficultToSwallow: '',
    difficultToBreathe: '',
    fatique: '',
    hiccups: '',
    cought: '',
    rash: ''
  };
});
