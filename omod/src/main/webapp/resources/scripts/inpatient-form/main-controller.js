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
        receivedResponses = receivedResponses + 1;
        completeData = _.merge(completeData, data);
        if (receivedResponses === $scope.views.length) {
            console.log('all data received');
            console.table(completeData);
            //TODO: Post to openMRS magic
            console.log(observationsFactory.get(completeData));

           $http.post("/openmrs/ws/rest/v1/encounter", observationsFactory.get(completeData));
        }
    });

    $scope.display = function (view) {
        activeView = view;
    };

    initialize();

    

});
