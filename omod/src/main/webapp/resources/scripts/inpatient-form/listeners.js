$(function(){


  $(".ebola-form .section").on("click", ".radio-button", function(event){
    var $radio = $("#" + $(this).attr("for"));

    var previousValue = $radio.attr("check");

    if(previousValue === "true"){
      $radio.prop("checked", false);
      $radio.attr("check", false);
      event.preventDefault();
    } else {
      $radio.attr("check", true);
    }

    event.stopPropagation();

  });

});
