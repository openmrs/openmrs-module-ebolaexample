$(function() {

  $(document).on('webkitfullscreenchange fullscreenchange', function(){
      screen.orientation.lock("portrait-primary");
  });

  $(".actions").on("click", "button", function(){
    document.documentElement.webkitRequestFullScreen();
  });

  var IS_CHECKED = "is-checked";

  $(".ebola-form .section").on("click", ".radio-button", function(event) {

    var scope = getScopeFromLabel($(this));
    var input = $("#" + $(this).attr("for"));
    var inputList = $("input[name='" + input.attr("name") + "']");

    if (isRadio(input)) {
      toggleRadio(input, scope, inputList);
    }

  });

  function toggleRadio(input, scope, inputList) {
    if (input.attr(IS_CHECKED) === "true") {
      uncheckRadio(input, scope, event);
    } else {
      checkRadio(inputList, input, scope);
    }
  }


  function getScopeFromLabel(label) {
    return angular.element(label.parents("ng-include")).scope();
  }

  function isRadio(input) {
    return input.attr("type") === "radio";
  }

  function uncheckRadio(input, scope, event) {
    input.prop("checked", false);
    input.attr(IS_CHECKED, false);

    scope.$apply(function() {
      scope.viewModel[input.attr("name")] = "";
    });

    event.preventDefault();
  }

  function checkRadio(inputList, input, scope) {
    inputList.attr(IS_CHECKED, false); //make all radios unchecked
    input.attr(IS_CHECKED, true);
    scope.$apply(function() {
      scope.viewModel[input.attr("name")] = input.val();
    });
  }

});
