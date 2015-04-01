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

    function formValues() {
        var post = {};
        _.each(jq('#add-edit-form').serializeArray(), function(it) {
            console.log(it);
            if (it.value) {
                post[it.name] = it.value;
            }
        });
        return post;
    }

    function validate() {
        var valid = true;
        var post = formValues();

        if (!post['note']) {
            valid = false;
        }
        if (valid) {
            jq('#add-edit-form button[type=submit]').prop('disabled', false);
        } else {
            jq('#add-edit-form button[type=submit]').prop('disabled', true);
        }
    }

    jq(function() {
        jq('#add-edit-form').on('keyup', 'textarea', function(event) {
            validate();
        });
        validate();
    });
</script>

<div class="dashboard clear">
    <div class="long-info-container column">
<div class="long-info-section">
<div  id = "prescriptions" class="info-header">
    <i class="icon-medkit"></i>
    <h3>Clinical notes</h3>
</div>
</div>
</div>
</div>

<form id="add-edit-form" method="post" action="${ ui.pageLink("ebolaexample", "addClinicalNote", [patient: patient.patient.uuid]) }">
    <p>
        ${ ui.includeFragment("uicommons", "field/textarea", [
                formFieldName: "note",
                label: "note",
                initialValue: null
        ])}
    </p>
    <p>
        <button type="submit" class="confirm right">Save</button>
        <a class="cancel button" href="${ui.pageLink("ebolaexample", "clinicalNotes", [patient:patient.patient.uuid])}">
            Cancel
        </a>
    </p>
</form>