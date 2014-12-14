angular.module("resources", ["ngResource"])

    .factory("WardResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/ward/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("PatientResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/patient/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("OrderResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/order/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("EncounterResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/encounter/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("DrugResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/drug/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }])

    .factory("ConceptResource", [ "$resource", function ($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/concept/:uuid", {
            uuid: "@uuid"
        }, {
            query: { method: "GET" }
        });
    }]);
