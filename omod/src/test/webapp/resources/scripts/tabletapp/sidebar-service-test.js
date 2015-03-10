describe('sidebar-service', function () {

    beforeEach(function () {
        module('directives');
    });

    describe('sidebarService tests', function () {
        var sidebarsrvc;

        beforeEach(function () {
            inject(function (sidebarService) {
                sidebarsrvc = sidebarService;
                sidebarsrvc.setView($(''));
            })
        });

        it('should return the opposite of visible', function () {
            sidebarsrvc.visible = false;

            sidebarsrvc.toggle();
            this.expect(sidebarsrvc.visible).toBeTruthy();

            sidebarsrvc.toggle();
            this.expect(sidebarsrvc.visible).toBeFalsy();
        });

        it('it should return anchors', function () {
            var flag;
            runs(function () {
                $.ajax({
                    url: '/src/main/webapp/resources/html/tabletapp/templates/sidebar.html',

                })
                    .done(function (data) {
                        sidebarsrvc.setView($(data));
                        flag = true;
                    });
            });

            waitsFor(function () {
                return flag;
            }, "Oops", 3000);

            runs(function () {
                anchors = sidebarsrvc.getActionElements().length;
                this.expect(anchors).toBeGreaterThan(0);
            });
        });


    });
});
