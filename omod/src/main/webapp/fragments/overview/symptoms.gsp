<%
    ui.includeJavascript("ebolaexample", "overview/symptomsSummary.js")
%>

<div id="symptoms" ng-show="isFeatureEnabled()" class="long-info-section"  ng-controller="SymptomsSummaryController" >

    <div class="info-header" >
        <i class="icon-hospital"></i>

        <h3>Symptoms</h3>
    </div>

    <div style="clear: both;"></div>
</div>


<script>
    angular.bootstrap("#symptoms", ['symptomsSummary']);
</script>
