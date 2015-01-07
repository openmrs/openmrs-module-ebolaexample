angular.module("login", [])
    .controller("LoginController", ["$scope", "UserResource", "Constants",
        function ($scope, UserResource, Constants) {
            $scope.login = function () {};
            $scope.teams = UserResource.query({role: Constants.roles.wardRoundingTeam});
        }]);