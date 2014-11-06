var module = angular.module('inpatientForm');

module.controller('symptomsController', function($scope) {
  $scope.viewModel = {
    overallSymptoms: '',
    bleeding: '',
    musclePain: '',
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
