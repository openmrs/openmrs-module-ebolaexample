angular.module('inpatientForm', ['ngRoute']).config(function($routeProvider) {

  $routeProvider.
  when('/vitalSigns', {
    templateUrl: 'vital-signs.html',
    controller: 'VitalSignsController'
  }).
  when('/hydration', {
    templateUrl: 'hydration.html',
    controller: 'HydrationController'
  }).
  when('/symptoms', {
    templateUrl: 'symptoms.html',
    controller: 'SymptomsController'
  }).
  when('/dailyManagement', {
    templateUrl: 'daily-management.html',
    controller: 'DailyManagementController'
  }).
  otherwise({
    redirectTo: '/'
  });

});
