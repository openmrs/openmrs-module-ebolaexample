<script type="text/javascript">
    Validators["kerry-town-id"] = {
        validate: function(field) {
            var val = field.value();
            if (val == '') {
                return emrMessages['requiredField']; //"Required";
            }
            var query = { s: "patientByIdentifier", identifier: val };
            jq.getJSON('/' + OPENMRS_CONTEXT_PATH + '/ws/rest/v1/patient', query, function(result) {
                if (result.results.length > 0) {
                    console.log(field);
                    console.log(field.messagesContainer);
                    // TODO make the simple form ui framework support this with a single function call
                    field.parentQuestion.parentSection.click();
                    field.parentQuestion.click();
                    field.click();

                    field.messagesContainer.html("Already in use");
                    field.messagesContainer.show();
                    field.element.addClass("error");
                }
            });
            return null;
        }
    }
</script>

<p>
    <label>
        Kerry Town ID
    </label>
    <input id="kerry-town-id" type="text" autocomplete="off" name="kerryTownId" class="regex kerry-town-id" regex="KT-\\d-\\d{5}" placeholder="KT-#-#####"/>
    <span class="field-error"></span>
</p>