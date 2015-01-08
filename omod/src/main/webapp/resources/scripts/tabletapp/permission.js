angular.module('tabletPermissions', ['permission', 'session'])
    .run(['Permission', 'CurrentSession', function (Permission, CurrentSession) {
        Permission.defineRole('loggedIn', function (stateParams) {
            return CurrentSession.getInfo();
        });
    }]);
