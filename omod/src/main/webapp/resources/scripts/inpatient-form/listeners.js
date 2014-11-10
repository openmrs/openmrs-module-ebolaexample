$(function(){


  $(".ebola-form .section").on("click", ".radio-button", function(event){
    var $radio = $("#" + $(this).attr("for"));

    var previousValue = $radio.attr("test");

    if(previousValue === "true"){
      $radio.prop("checked", false);
      $radio.attr("test", false);
      event.preventDefault();
    } else {
      $radio.attr("test", true);
    }

    event.stopPropagation();

  });

});
