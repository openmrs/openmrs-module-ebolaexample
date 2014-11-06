var module = angular.module('inpatientForm');

module.controller('MainController', function ($scope) {
    var createView = function (name, file, description) {
            return {
                name: name,
                file: file,
                description: description
            }
        }, activeView = undefined,
        initialize = function () {
            activeView = $scope.views[0];
        };

    $scope.views = [
        createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS'),
        createView('hydration', 'hydration.html', 'HYDRATION'),
        createView('symptoms', 'symptoms.html', 'SYMPTOMS'),
        createView('daily-management', 'daily-management.html', 'DAILY MGMT')
    ];

    $scope.shouldDisplay = function (view) {
        return activeView === view;
    };

    $scope.shouldDisplayBackButton = function () {
        return _.indexOf($scope.views, activeView) !== 0;
    };

    $scope.shouldDisplayNextButton = function () {
        return _.indexOf($scope.views, activeView) !== $scope.views.length-1;
    };

    $scope.shouldDisplayFinishButton = function () {
        return _.indexOf($scope.views, activeView) === $scope.views.length-1;
    };

    $scope.display = function (view) {
        activeView = view;
    };

    initialize();
});
