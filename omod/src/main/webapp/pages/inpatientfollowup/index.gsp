<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
        <link rel="stylesheet" type="text/css" href="${ ui.resourceLink("ebolaexample", "styles/style.css") }">
        <script src="${ ui.resourceLink("ebolaexample", "scripts/lib/angular/angular.min.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/lib/jquery/jquery-2.1.1.min.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/lib/lodash/dist/lodash.min.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/lib/angular-route/angular-route.min.js") }"></script>
        <script src="${ ui.resourceLink("uicommons", "scripts/emr.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/inpatient-form-module.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/main-controller.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/simple-form-controller.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/concept-mapping-factory.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/observations-factory.js") }"></script>
        <script src="${ ui.resourceLink("ebolaexample", "scripts/inpatient-form/listeners.js") }"></script>
    </head>

    <script type="text/javascript">
        var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
        window.inpatientFormConfig = {
            patientUuid: '${ patient.patient.uuid }',
            visitUuid: '${ visit.visit.uuid }',
            locationUuid: '${ assignedLocation.uuid }',
            providerUuid: '${ provider.person.uuid }'
        }
    </script>

    <body ng-app="inpatientForm" ng-controller="MainController" ng-init="init()">
        <div class="page-wrap" ng-show="patient">
            <header>
        	    <div class="patient-header">
        			<span class="patient-id">
        				<small>PATIENT ID NUMBER</small>
        				{{ patient.identifier }}
        			</span>
        			<span class="patient-info">
        				<small>WARD</small>
        				12
        			</span>
        			<span class="patient-info">
        				<small>BED</small>
        				27
        			</span>
        	    </div>
        	    <div class="navigation" style="background-size: {{ getProgress() }}% 100%;">
        			<span ng-if="view.shouldShow" class="category" ng-repeat="view in views">
        				<h3>{{view.description}}</h3>
        			</span>
        	    </div>
            </header>
            <div class="ebola-form">
                <div class="section">
                    <div ng-repeat="view in views">
                        <ng-include ng-show="shouldDisplay(view)" src="view.file" ng-controller="SimpleFormController"></ng-include>
                    </div>
                </div>
            </div>
        </div>
        <div class="actions">
            <button class="left" ng-show="shouldDisplayBackButton()" ng-click="back()">BACK</button>
            <button class="right" ng-show="shouldDisplayNextButton()" ng-click="next()">NEXT</button>
            <button class="right" ng-show="shouldDisplayFinishButton()" ng-click="finish()">FINISH</button>
        </div>
    </body>
</html>
