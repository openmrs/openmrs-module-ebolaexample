angular.module('drugConceptService', [])
    .factory('DrugConceptService', ['$http', function($http) {

        return {
            getDrugConcepts: function(phrase) {
                return $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/drug-concept?query=" + phrase);
            }
        }
    }]);