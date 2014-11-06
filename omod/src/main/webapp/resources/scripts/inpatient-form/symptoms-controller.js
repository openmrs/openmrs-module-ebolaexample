var module = angular.module('inpatientForm')

module.controller('symptomsController', function() {
  $scope.viewModel = {
    overallSymptoms: '',
    dehydration: '',
    bleeding: '',
    jointMusclePain: '',
    headache: '',
    urinePain: '',
    abdominalPain: '',
    unableToEat: '',
    unableToDrink: '',
    difficultToSwallow: '',
    difficultToBreathe: '',
    fatigue: '',
    hiccups: '',
    cough: '',
    rash: ''
  };
});
