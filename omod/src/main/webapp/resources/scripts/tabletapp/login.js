angular.module("login", [])
    .controller("LoginController", ["$scope", "UserResource", "Constants", "LoginService",
        function ($scope, UserResource, Constants, LoginService) {
            $scope.loginForm = {};
            $scope.login = function (loginModel) {
                LoginService.login(loginModel.username, loginModel.provider);
            };
            $scope.teams = UserResource.query({role: Constants.roles.wardRoundingTeam});
        }])

    .service("LoginService", ["$http", "CurrentSession", "$state",
        function ($http, CurrentSession, $state) {
            return {
                login: function (username, provider) {
                    $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/login", {'username': username, 'provider': String(provider)})
                        .success(function (response) {
                            CurrentSession.setInfo(response);
                            $state.go("wards");
                        });
                }
            }
        }]);