<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeJavascript("uicommons", "services/locationService.js")
    ui.includeJavascript("ebolaexample", "admin/manageWards.js")

    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.includeCss("ebolaexample", "admin/manageWards.css")
%>

${ ui.includeFragment("appui", "messages", [ codes: [
        'ebolaexample.manageWards.addDialogTitle',
        'ebolaexample.manageWards.editDialogTitle'
]]) }

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("ebolaexample.manageWards.title")) }" }
    ]
</script>

<div id="manage-wards" ng-controller="ManageWardsCtrl" ng-init='init("${ parentLocation.uuid }", ${ tagsForAllJson })'>
    <script type="text/ng-template" id="addEditTemplate">
    <div class="dialog-header">
        <h3>{{ ngDialogData.title }}</h3>
    </div>
        <div class="dialog-content">
            <form>
                <p>
                    <label>${ ui.message("ebolaexample.manageWards.name") }</label>
                    <input type="text" ng-model="name"/>
                </p>
                <p>
                    <label>${ ui.message("ebolaexample.manageWards.description") }</label>
                    <textarea ng-model="description"/>
                </p>
                <p ng-show="editing">
                    <label>${ ui.message("ebolaexample.manageWards.type") }</label>
                    {{ displayTags(editing) }}
                </p>
                <p ng-hide="editing">
                    <label>${ ui.message("ebolaexample.manageWards.type") }</label>
                    <select ng-model="tagUuid">
                        <option value=""></option>
                        <% tags.each { %>
                            <option value="${ it.uuid }">${ ui.escapeHtml(ui.format(it)) }</option>
                        <% } %>
                    </select>
                </p>
            </form>
            <div>
                <button class="confirm right" ng-click="save()">${ ui.message("uicommons.save") }</button>
                <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
            </div>
        </div>
    </script>

    <a ng-click="showAddDialog()">
        <i class="icon-plus"></i>
        ${ ui.message("ebolaexample.manageWards.addAnother") }
    </a>

    <% tags.each { tag ->%>

        <h3>${ ui.format(tag) }</h3>
        <table>
            <thead>
                <th>${ ui.message("ebolaexample.manageWards.name") }</th>
                <th>${ ui.message("ebolaexample.manageWards.description") }</th>
                <th>${ ui.message("coreapps.actions") }</th>
            </thead>
            <tbody>
                <tr ng-repeat="location in locationsWithTag('${ tag.name }')">
                    <td ng-class="{ retired: location.retired }">
                        {{ location.name }}
                    </td>
                    <td ng-class="{ retired: location.retired }">
                        {{ location.description }}
                    </td>
                    <td>
                        <a ng-click="showEditDialog(location)"><i class="icon-pencil edit-action"></i></a>
                        <a ng-show="!location.retired" ng-click="retire(location)"><i class="icon-remove delete-action"></i></a>
                        <a ng-show="location.retired" ng-click="unretire(location)"><i class="icon-plus edit-action"></i></a>
                    </td>
                </tr>
            </tbody>
        </table>
    <% } %>
</div>

<script>
    var locations = ${ locationsJson };

    angular.bootstrap('#manage-wards', ['manageWards']);
</script>