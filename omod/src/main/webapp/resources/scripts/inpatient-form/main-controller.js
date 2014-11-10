var module = angular.module('inpatientForm');

module.controller('MainController', function ($scope, observationsFactory, $http) {
    var createView = function (name, file, description, shouldShow) {
            return {
                name: name,
                file: file,
                description: description,
                shouldShow: shouldShow
            }
        },
        activeView = undefined,
        receivedResponses = 0,
        completeData = {},
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

    $scope.getProgress = function () {
        return $scope.views.indexOf(activeView) * 100 / 8;
    }

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
        receivedResponses = 0;
        $scope.$broadcast('request-patient-info');
    };

    $scope.$on('response-patient-info', function (event, data) {
        var post = {};

        post.patient="abc1469f-7274-4f29-8753-2dbca1fbf670";
        post.encounterType="e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
        post.location="b1a8b05e-3542-4037-bbd3-998ee9c40574";
        post.visit="dfa4c24a-ba02-4d47-92a7-f45d2eb0b1e7";

        receivedResponses = receivedResponses + 1;
        completeData = _.merge(completeData, data);
        if (receivedResponses === $scope.views.length) {
           post.obs = observationsFactory.get(completeData);
           $http.post("/openmrs/ws/rest/v1/encounter", post);
        }
    });


    $scope.display = function (view) {
        activeView = view;
    };

    initialize();



});
