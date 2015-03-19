angular.module('tabletapp')

    .factory("forms", [ 'Constants', function(Constants) {

        function createView(name, file, description, shouldShowTitle) {
            return {
                name: name,
                file: file,
                description: description,
                shouldShow: shouldShowTitle
            }
        }

        return {
            vitals: {
                encounterType: Constants.encounterType.ebolaInpatientFollowup,
                views: [
                    createView('vital-signs', 'templates/formScreens/vital-signs.html', 'VITAL SIGNS', true),
                    createView('vital-signs', 'templates/formScreens/vital-signs2.html', 'VITAL SIGNS 2', false),
                ]
            },
            Symptoms: {
                encounterType: Constants.encounterType.ebolaInpatientFollowup,
                views: [
                    createView('symptoms', 'templates/formScreens/symptoms.html', 'SYMPTOMS', true),
                    createView('symptoms', 'templates/formScreens/symptoms2.html', 'SYMPTOMS 2', false)
                ]
            },
            vitalsAndSymptoms: {
                encounterType: Constants.encounterType.ebolaInpatientFollowup,
                views: [
                    createView('vital-signs', 'templates/formScreens/vital-signs.html', 'VITAL SIGNS', true),
                    createView('vital-signs', 'templates/formScreens/vital-signs2.html', 'VITAL SIGNS 2', false),
                    createView('symptoms', 'templates/formScreens/symptoms.html', 'SYMPTOMS', true),
                    createView('symptoms', 'templates/formScreens/symptoms2.html', 'SYMPTOMS 2', false)
                ]
            },
            other_views_not_in_a_form_yet: {
                views: [
                    createView('hydration', 'templates/formScreens/hydration.html', 'HYDRATION', true),
                    createView('hydration', 'templates/formScreens/hydration2.html', 'HYDRATION 2', false),
                    createView('daily-management', 'templates/formScreens/daily-management.html', 'DAILY MGMT', true),
                    createView('daily-management', 'templates/formScreens/daily-management2.html', 'DAILY MGMT2', false)
                ]
            }
        }

    }]);