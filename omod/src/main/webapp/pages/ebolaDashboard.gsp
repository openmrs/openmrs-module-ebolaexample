<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaDashboard.title")) }" }
    ]
</script>

<p>
    <label>Total Ebola Patients and Suspects</label>
    ${ inProgram.size() }
</p>

<p>
    <label>Enrolled Today</label>
    ${ enrolledToday.size() }
</p>