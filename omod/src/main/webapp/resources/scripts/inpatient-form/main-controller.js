var module = angular.module('inpatientForm');


module.controller('MainController', function ($scope, observationsFactory, $http) {
    var that = this;

    this.getParameterByName = function (name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

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

    function loadData(){
        var patientUuid = that.getParameterByName("patientUuid");
        var visitUuid = that.getParameterByName("visitUuid");
        var locationUuid = that.getParameterByName("locationUuid");
        var providerUuid = that.getParameterByName("providerUuid");

        $scope.patient = {};
        $scope.patient.visitUuid = visitUuid;
        $scope.patient.patientUuid = patientUuid;
        $scope.patient.locationUuid = locationUuid;
        $scope.patient.providerUuid = providerUuid;


        $http.get("/openmrs/ws/rest/v1/patient/" + patientUuid, {params:{ v: "full"}}).success(function(result){
            var patientId = _.filter(result.identifiers, function(id) {
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

        post.encounterType="e22e39fd-7db2-45e7-80f1-60fa0d5a4378";

        receivedResponses = receivedResponses + 1;
        completeData = _.merge(completeData, data);
        if (receivedResponses === $scope.views.length) {
           post.patient = $scope.patient.patientUuid;
           post.visit = $scope.patient.visitUuid;
           post.location = $scope.patient.locationUuid;
           post.provider = $scope.patient.providerUuid;
           post.obs = observationsFactory.get(completeData);
           $http.post("/openmrs/ws/rest/v1/encounter", post).success(function(result){
             alert("Success \o/");
           });
        }
    });


    $scope.display = function (view) {
        activeView = view;
    };

    initialize();
    loadData();


});
