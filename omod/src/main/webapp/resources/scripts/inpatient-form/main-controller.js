var module = angular.module('inpatientForm');

module.controller('MainController', function($scope) {
    var activeView = 'vital-signs';

    $scope.shouldDisplay = function (target) {
        return activeView === target;
    };

    $scope.display = function (target) {
        activeView = target;
    };
});
