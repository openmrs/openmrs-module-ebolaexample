var module = angular.module('inpatientForm');

module.controller('symptomsController', function($scope) {
  $scope.viewModel = {
    overallSymptoms: '',
    noseOral: '',
    cough: '',
    vomit: '',
    stool: '',
    vaginal: '',
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
