<%
    ui.decorateWith("appui", "standardEmrPage")

    def dateAndTimeFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        <% if (wardAndBed && wardAndBed.ward) { %>
        {
            label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "findPatientByWard", [ ward: wardAndBed.ward.uuid ])) }'
        },
        <% } %>
        {
            label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }",
            link: '${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.patient.uuid ]) }'
        },
        {label: "Med Administration"}
    ]
    var patient = {id: ${ patient.id }};

    function formValues() {
        var post = {};
        _.each(jq('#add-edit-form').serializeArray(), function(it) {
            if (it.value) {
                post[it.name] = it.value;
            }
        });
        return post;
    }

    function validate() {
        var valid = true;
        var post = formValues();
        if (!post['status']) {
            valid = false;
        }
        if (!post['scheduledDatetime']) {
            valid = false;
        }
        if (valid) {
            if (post['status'] == 'FULL' && post['reasonNotAdministeredNonCoded']) {
                valid = false;
            }
        }
        if (valid) {
            jq('#add-edit-form button[type=submit]').prop('disabled', false);
        } else {
            jq('#add-edit-form button[type=submit]').prop('disabled', true);
        }
        if (!post['status'] || post['status'] === 'FULL') {
            jq('#add-edit-form select[name=reasonNotAdministeredNonCoded]').prop('disabled', true);
        } else {
            jq('#add-edit-form select[name=reasonNotAdministeredNonCoded]').prop('disabled', false);
        }
    }

    jq(function() {
        jq('#add-edit-form').on('change', 'input, select', function(event) {
            validate();
        });
        validate();
    });
</script>

<h3>
    Add a drug administration for
    <br/>
    <strong>${ui.format(prescription.drug)}</strong>
    <em>
        ${formatter.formatPrescription(prescription, context.locale, ui)}

        from
        ${dateAndTimeFormat.format(prescription.dateActivated)}
        <% if (prescription.dateStopped) { %>
            stopped
            ${dateAndTimeFormat.format(prescription.dateStopped)}
        <% } else if (prescription.autoExpireDate) { %>
            until ${dateAndTimeFormat.format(prescription.autoExpireDate)}
        <% } %>
    </em>
</h3>

<form id="add-edit-form" method="post" action="${ ui.pageLink("ebolaexample", "addEditDose", [patient: patient.patient.uuid, prescription: prescription.uuid]) }">
    <input type="hidden" name="order" value="${ prescription.uuid }">
    <p>
        ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                id: "scheduledDate",
                label: "Date and time",
                formFieldName: "scheduledDatetime",
                startDate: prescription.effectiveStartDate,
                endDate: prescription.effectiveStopDate ?: new Date(),
                useTime: true
        ])}
    </p>
    ${ ui.includeFragment("uicommons", "field/dropDown", [
            formFieldName: "status",
            label: "Status",
            options: [
                    [value: "FULL", label: "Fully Given"],
                    [value: "PARTIAL", label: "Partially Given"],
                    [value: "NOT_GIVEN", label: "Not Given"]
            ]
    ])}
    ${ ui.includeFragment("uicommons", "field/dropDown", [
            formFieldName: "reasonNotAdministeredNonCoded",
            label: "Reason not administered",
            options: [
                    "Patient asleep",
                    "Patient vomiting",
                    "Aggressive mood",
                    "Cannot access vein for IV"
            ].collect{ [value: it, label: it] }
    ])}

    <p>
        <button type="submit" class="confirm right">Save</button>
        <a class="cancel button" href="${ui.pageLink("ebolaexample", "doseManagement", [patient:patient.patient.uuid])}">
            Discard
        </a>
    </p>
</form>