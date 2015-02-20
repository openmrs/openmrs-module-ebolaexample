<p class="required">
    <label>
        ${config.label}
        <span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span>
    </label>
    <select name="${config.formFieldName}" size="3">
        <option value=""></option>
        <option value ="142177AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA">Suspected case</option>
        <option value ="159392AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA">Confirmed case</option>
    </select>
    <span class="field-error"></span>
</p>