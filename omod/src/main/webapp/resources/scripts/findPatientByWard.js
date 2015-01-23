angular.module('findPatientByWard', [ 'resources' ])

    .controller("FindPatientByWardCtrl", [ '$scope', 'WardResource', function($scope, WardResource) {

        $scope.wards = [];
        $scope.selectedWard = null;

        WardResource.query({v: 'default'}, function (response) {
            var results = response.results;
            $scope.wards = results;
        });

        $scope.selectWard = function(ward) {
            $scope.selectedWard = WardResource.get({ uuid: ward.uuid });
        }

        $scope.selectPatient = function(patient) {
            location.href = '/' + OPENMRS_CONTEXT_PATH + '/ebolaexample/ebolaOverview.page?patient=' + patient.uuid;
        }

        $scope.init = function(wardUuid) {
            $scope.selectWard({ uuid: wardUuid });
        }

    }]);