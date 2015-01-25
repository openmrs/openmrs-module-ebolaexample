angular.module("login", [])
    .controller("LoginController", ["$scope", "UserResource", "Constants", "LoginService", "$state",
        function ($scope, UserResource, Constants, LoginService, $state) {
            $scope.loginForm = {};
            $scope.login = function (loginModel) {
                LoginService.login(loginModel.username, loginModel.provider).then(function() {
                    $state.go("wards");
                });
            };
            $scope.teams = UserResource.query({role: Constants.roles.wardRoundingTeam});

            $scope.$watch("loginForm.username", function(newSelection) {
                if (newSelection) {
                    angular.element(".login-form input[type=text]").focus();
                }
            });
        }])

    .service("LoginService", ["$http", "CurrentSession",
        function ($http, CurrentSession) {
            return {
                login: function (username, provider) {
                    return $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/ebola/login", {'username': username, 'provider': String(provider)})
                        .success(function (response) {
                            CurrentSession.setInfo(response);
                        });
                }
            }
        }]);