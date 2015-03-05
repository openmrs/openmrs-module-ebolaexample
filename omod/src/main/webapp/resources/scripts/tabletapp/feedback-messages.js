angular.module('feedback-messages', [])
    .factory("FeedbackMessages", [ function() {
        var messages = {
            success: []
        }

        /*
         * Messages should look like:
         * {
         *   display: "Vitals recorded successfully"
         * }
         *
         * (Making these be an object gives us flexibility to add more interesting behavior later, like a link to the
         * relevant object, or an undo button.)
         */

        return {
            getSuccessMessages: function() {
                return messages.success;
            },
            showSuccessMessage: function(message) {
                messages.success.push(message);
            },
            clearMessages: function() {
                messages.success = [];
            }
        };
    }]);