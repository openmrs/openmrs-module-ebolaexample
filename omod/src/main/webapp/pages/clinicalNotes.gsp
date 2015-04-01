<%
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")
    def timeFormat = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss")

%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        <% if (wardAndBed && wardAndBed.ward) { %>
        {
            label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "activePatients", [ ward: wardAndBed.ward.uuid ])) }'
        },
        <% } %>
        {
            label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }",
            link: '${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.patient.uuid ]) }'
        },
        {label: "Clinical Notes"}
    ]
    var patient = {id: ${ patient.id }};
</script>

${ui.includeFragment("ebolaexample", "overview/patientHeader", [patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel])}

<div class="clear"></div>

<div class="container">
    <div class="dashboard clear">
        <div class="long-info-container column">

            <div class="long-info-section">

                <div id="prescriptions" class="info-header">
                    <i class="icon-medkit"></i>

                    <h3>Clinical notes</h3>

                    <a href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.patient.uuid])}">
                        Back to Summary
                    </a>
                    <a class="right"
                       href="${ui.pageLink("ebolaexample", "addClinicalNote", [patient: patient.patient.id])}">
                        Add clinical note
                    </a>
                </div>

                <div class="info-body">
                    <% if (clinicalNotes.size() > 0) { %>
                    <table>

                        <% clinicalNotes.each { clinicalNote ->
                            def obs = clinicalNote.obs.toArray()[0]
                            def encounterDatetime = clinicalNote.encounterDatetime
                            def provider = clinicalNote.provider
                            def givenName = provider.personName.givenName
                            def familyName = provider.personName.familyName
                            def middleName = provider.personName.middleName

                            def name = givenName + ", " + " " + familyName
                            if(middleName){
                                name += " " + middleName
                            };

                            def note = obs.valueText.replace('\r\n', "<br/>")
                        %>
                        <tr style="font-size: 0.9em;">
                            <td style="display:inline-block; border:none !important">
                                    <span style="margin-right: 5px">${timeFormat.format(encounterDatetime)} </span>

                                    ${name}
                            </td>

                            <td style="width: 70%">
                                    ${note}
                            </td>
                        </tr>

                        <% } %>
                    </table>

                    <% } else { %>
                    ${ui.message("coreapps.none")}
                    <% } %>
                </div>
            </div>

        </div>
    </div>
</div>