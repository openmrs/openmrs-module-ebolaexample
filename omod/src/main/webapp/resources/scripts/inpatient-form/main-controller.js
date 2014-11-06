var module = angular.module('inpatientForm');

module.controller('MainController', function ($scope, observationsFactory) {
    var createView = function (name, file, description) {
            return {
                name: name,
                file: file,
                description: description
            }
        },
        activeView = undefined,
        initialize = function () {
            activeView = $scope.views[0];
        },
        getActiveViewIndex = function () {
            return _.indexOf($scope.views, activeView);
        };

    $scope.views = [
        createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS'),
        createView('vital-signs', 'vital-signs2.html', 'VITAL SIGNS 2'),
        createView('hydration', 'hydration.html', 'HYDRATION'),
        createView('hydration', 'hydration2.html', 'HYDRATION 2'),
        createView('symptoms', 'symptoms.html', 'SYMPTOMS'),
        createView('symptoms', 'symptoms2.html', 'SYMPTOMS 2'),
        createView('daily-management', 'daily-management2.html', 'DAILY MGMT2'),
        createView('daily-management', 'daily-management.html', 'DAILY MGMT')
    ];

    $scope.shouldDisplay = function (view) {
        return activeView === view;
    };

    $scope.shouldDisplayBackButton = function () {
        return getActiveViewIndex() !== 0;
    };

    $scope.shouldDisplayNextButton = function () {
        return getActiveViewIndex() !== $scope.views.length - 1;
    };

    $scope.shouldDisplayFinishButton = function () {
        return getActiveViewIndex() === $scope.views.length - 1;
    };

    $scope.next = function () {
        activeView = $scope.views[getActiveViewIndex() + 1];
    };

    $scope.back = function () {
        activeView = $scope.views[getActiveViewIndex() - 1];
    };

    $scope.finish = function () {
        alert('Not there yet!');
    };

    $scope.display = function (view) {
        activeView = view;
    };

    initialize();

    console.table(observationsFactory.get({
        oralFluids: '',
        dehydration: '',
        urineOutput: '',
        vomiting: '',
        stoolFreq: '',
        mainStool: '',
        respiratoryRate: ''
    }));

});