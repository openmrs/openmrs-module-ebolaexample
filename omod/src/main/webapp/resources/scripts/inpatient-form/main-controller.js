var module = angular.module('inpatientForm');

module.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

module.controller('MainController', function ($scope, observationsFactory, $http, $location) {
    var that = this;

    var path = $location.path().substring(0, $location.path().indexOf("/", 1));

    var CONTEXT_PATH = (path === "/openmrs-module-ebolaexample") ? "/openmrs" : path;

    $scope.patient = {};
    $scope.patient.visitUuid = $location.search().visitUuid;
    $scope.patient.patientUuid = $location.search().patientUuid;
    $scope.patient.locationUuid = $location.search().locationUuid;
    $scope.patient.providerUuid = $location.search().providerUuid;

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

    function loadData() {

        $http.get(CONTEXT_PATH + "/ws/rest/v1/patient/" + $scope.patient.patientUuid, {
            params: {
                v: "full"
            }
        }).success(function (result) {
            var patientId = _.filter(result.identifiers, function (id) {
                return id.preferred;
            });
            $scope.patient.identifier = patientId[0].identifier;
        });
    }

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

    $scope.focusInput = function ($event) {
        angular.element($event.target).parent().addClass('highlight');
    };

    $scope.blurInput = function ($event) {
        angular.element($event.target).parent().removeClass('highlight');
    };

    $scope.shouldDisplay = function (view) {
        return activeView === view;
    };

    $scope.getCurrentViewNumber = function () {
        return $scope.views.indexOf(activeView) + 1;
    };

    $scope.getProgress = function () {
        return $scope.views.indexOf(activeView) * 100 / 8;
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
        receivedResponses = 0;
        $scope.$broadcast('request-patient-info');
    };

    $scope.$on('response-patient-info', function (event, data) {
        var post = {};

        post.encounterType = "83413734-587d-11e4-af12-660e112eb3f5";

        receivedResponses = receivedResponses + 1;
        completeData = _.merge(completeData, data);
        if (receivedResponses === $scope.views.length) {
            post.patient = $scope.patient.patientUuid;
            post.visit = $scope.patient.visitUuid;
            post.location = $scope.patient.locationUuid;
            post.provider = $scope.patient.providerUuid;
            post.obs = observationsFactory.get(completeData);
            $http.post(CONTEXT_PATH + "/ws/rest/v1/encounter", post).success(function (result) {
                location.href = CONTEXT_PATH + "/ebolaexample/ebolaOverview.page?patient=" + $scope.patient.patientUuid;
            });
        }
    });


    $scope.display = function (view) {
        activeView = view;
    };

    initialize();
    loadData();


});
