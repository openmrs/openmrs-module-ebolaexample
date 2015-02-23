<p>
    <label>
        ${config.label}
        <span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span>
    </label>
    <select name="${config.formFieldName}" size="4">
        <option value=""></option>
        <option value ="162829AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA">1 - Early / Dry</option>
        <option value ="162830AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA">2 - GI / Wet</option>
        <option value ="162831AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA">3 - Severe</option>
    </select>
    <span class="field-error"></span>
</p>