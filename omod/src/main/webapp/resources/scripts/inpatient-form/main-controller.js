var module = angular.module('inpatientForm');

module.controller('MainController', function ($scope, observationsFactory) {
    var createView = function (name, file, description, shouldShow) {
            return {
                name: name,
                file: file,
                description: description,
                shouldShow: shouldShow
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
        createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS', true),
        createView('vital-signs', 'vital-signs2.html', 'VITAL SIGNS 2', false),
        createView('hydration', 'hydration.html', 'HYDRATION', true),
        createView('hydration', 'hydration2.html', 'HYDRATION 2', false),
        createView('symptoms', 'symptoms.html', 'SYMPTOMS', true),
        createView('symptoms', 'symptoms2.html', 'SYMPTOMS 2', false),
        createView('daily-management', 'daily-management.html', 'DAILY MGMT', true),
        createView('daily-management', 'daily-management2.html', 'DAILY MGMT2', false)

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