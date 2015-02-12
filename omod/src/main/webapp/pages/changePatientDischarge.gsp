<%
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js")
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")
%>

<a href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.uuid])}">&larr; Back to Summary (discard changes)</a>

<style type="text/css">
.info-header h3 {
    display: inline-block;
    font-family: "OpenSansBold";
    font-size: 1em;
    margin: 0;
}

.info-header {
    border-bottom: 6px solid #00463f;
}

.info-body {
    background: none repeat scroll 0 0 #f9f9f9;
    border: 1px solid #eeeeee;
    color: #363463;
    padding: 5px;
}
</style>

<h2>
    ${ui.format(patient)}
</h2>
<span style="background-color: #00FF00;">Current Status:
    <% if (patientProgram.outcome) { %>
    ${ui.format(patientProgram.outcome)}, ${patientProgram.dateCompleted.format('dd MMM yyyy')}
    <% } else { %> None <% } %></span>

<div class="info-section" style="padding-top: 5px;">

    <div class="info-header">
        <i class="icon-hospital"></i>

        <h3>Discharge a Patient</h3>
    </div>

    <div class="info-body">

        <form action="${ui.pageLink("ebolaexample", "changePatientDischarge", [patientUuid: patient.uuid])}" method="post">

            <h3>Please select Final Outcome:</h3>

            <div style="float: left; width: 50%;">
                <% outComes.each { %>

                <label>
                    <input type="radio" class="outcome_checkboxes" name="outCome" uncheckable="true"
                        <% if (patientProgram.outcome && it.uuid.equals(patientProgram.outcome.uuid)) { %> checked="checked" <% } %>
                           value="${it.uuid}"/>
                    ${ui.format(it)}
                </label>

                <% } %>

                <label for="dateCompleted" class="required" style="font-weight: bold; margin-top: 10px;">
                    Discharge Date:
                </label>

                ${ui.includeFragment("uicommons", "field/datetimepicker", [
                        id           : "dateCompleted",
                        formFieldName: "dateCompleted",
                        label        : "",
                        useTime      : false,
                        startDate    : patientProgram.dateEnrolled,
                        endDate      : today,
                        defaultDate  : defaultDate
                ])}
            </div>

            <div style="float: right;width: 30%;">
                <% if (!success.equals("")) { %> <div style="width:100%; height:auto; background-color: #00FF00;">${success}</div><% } %>
            <% if (!error.equals("")) { %> <div style="width:100%; height:auto; background-color: red;">${error}</div><% } %>
            </div>

            <div style="clear: both; padding-top: 20px;"/>

            <a class="button cancel" href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.uuid])}">Cancel</a>
            <button type="submit" class="confirm right">Save</button>
        </form>
    </div>
</div>
