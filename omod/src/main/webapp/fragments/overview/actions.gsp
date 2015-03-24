<%
config.require("currentAssignment")
config.require("patient")
%>
<% if (config.currentAssignment && config.currentAssignment.ward) { %>
<a class="right"
   href="/${ui.contextPath()}/ms/uiframework/resource/ebolaexample/html/tabletapp/index.html#/patients/${
           config.patient.patient.uuid}/overview2/${config.patient.patient.uuid}/${config.currentAssignment.ward.uuid}">
    <i class="icon-pencil"></i>
</a>
<% } %>