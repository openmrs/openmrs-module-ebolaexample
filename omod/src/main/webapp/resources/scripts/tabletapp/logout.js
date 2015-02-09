angular.module("logout", [])
    .controller("LogoutController", ["$scope", "CurrentSession", "$state",
        function ($scope, CurrentSession, $state) {
            $scope.logout = function() {
                CurrentSession.clear();
                $state.go("login");
            };
        }]);