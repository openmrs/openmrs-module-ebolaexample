angular.module('ward-service', [])

    .service('WardService', ['CurrentSession', function(CurrentSession) {
        var getBedForPatientObject = function(patient) {
            var assignment = _.find(CurrentSession.getRecentWard().bedAssignments, function(assignment, index) {
                    return patient.uuid == assignment.patient.uuid;
                });
            return assignment ? assignment.bed.display : "";
        };
        return {
            getBedDescriptionFor: function(patient) {
                if (patient.$promise && !patient.$resolved) {
                    var bed = {$resolved: false},
                        bedPromise = patient.$promise.then(function (value) {
                            bed['$resolved'] = true;
                            bed['display'] = getBedForPatientObject(value);
                        });
                    bed['$promise'] = bedPromise;
                    return bed;
                }
                return { display: getBedForPatientObject(patient) };
            },
        getWardDescription: function() {
                var ward = CurrentSession.getRecentWard();
                return ward ? ward.display : "";
            }
        }
    }])