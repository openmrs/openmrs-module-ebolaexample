angular.module('tabletForm')

    .factory("views", [ function() {

        function createView(name, file, description, shouldShowTitle) {
            return {
                name: name,
                file: file,
                description: description,
                shouldShow: shouldShowTitle
            }
        }

        return [
            createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS', true),
            createView('vital-signs', 'vital-signs2.html', 'VITAL SIGNS 2', false),
            createView('hydration', 'hydration.html', 'HYDRATION', true),
            createView('hydration', 'hydration2.html', 'HYDRATION 2', false),
            createView('symptoms', 'symptoms.html', 'SYMPTOMS', true),
            createView('symptoms', 'symptoms2.html', 'SYMPTOMS 2', false),
            createView('daily-management', 'daily-management.html', 'DAILY MGMT', true),
            createView('daily-management', 'daily-management2.html', 'DAILY MGMT2', false)
        ];

    }]);