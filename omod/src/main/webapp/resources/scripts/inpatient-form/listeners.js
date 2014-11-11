$(function(){


  $(".ebola-form .section").on("click", ".radio-button", function(event){
    var $input = $("#" + $(this).attr("for"));

    var type = $input.attr("type");

    if (type !== "radio"){
      return;
    }


    var previousValue = $input.attr("check");

    if(previousValue === "true"){
      $input.prop("checked", false);
      $input.attr("check", false);
      event.preventDefault();
    } else {
      $input.attr("check", true);
    }

    event.stopPropagation();

  });

});
