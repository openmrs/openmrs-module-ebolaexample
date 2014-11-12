<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.6.0.min.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeJavascript("uicommons", "filters/display.js")

    ui.includeJavascript("ebolaexample", "formentry/formentry.js")
    ui.includeCss("ebolaexample", "formentry/formentry.css")
%>

<div id="formentry-app" ng-controller="FormEntryCtrl">

    <script id="single-concept" type="text/ng-template">
        <div class="display-value">
            {{ screen | format }}
        </div>
        <br/>
        <button ng-repeat="option in screen.config.options" ng-click="setValue(option)" ng-class="{ selected: option.value == screen.value.value }">
            {{ option.label }}
        </button>
    </script>

    <script id="multiple-concepts" type="text/ng-template">
        <div class="display-value">
            {{ screen | format }}
        </div>
        <br/>
        <button ng-repeat="option in screen.config.options" ng-click="toggleValue(option)" ng-class="{ selected: hasOption(option) }">
            {{ option.label }}
        </button>
    </script>

    <script id="single-numeric" type="text/ng-template">
        <div class="display-value">
            {{ screen | format }}
            {{ screen.config.suffix }}
        </div>
        <br/>
        <button ng-click="appendToValue('1')">1</button>
        <button ng-click="appendToValue('2')">2</button>
        <button ng-click="appendToValue('3')">3</button>
        <br/>
        <button ng-click="appendToValue('4')">4</button>
        <button ng-click="appendToValue('5')">5</button>
        <button ng-click="appendToValue('6')">6</button>
        <br/>
        <button ng-click="appendToValue('7')">7</button>
        <button ng-click="appendToValue('8')">8</button>
        <button ng-click="appendToValue('9')">9</button>
        <br/>
        <button ng-click="resetValue()">X</button>
        <button ng-click="appendToValue('0')">0</button>
        <button ng-click="appendToValue('.')">.</button>
    </script>

    <h3>{{ form.title }}</h3>
    <h4>{{ currentSection.title }}</h4>
    <h5>{{ currentScreen.title }} = {{ formatValue(currentScreen) }}</h5>

    <div class="left">
        <button id="prev-screen" ng-click="prevScreen()">&lArr;</button>
    </div>

    <div class="right">
        <button id="next-screen" ng-click="nextScreen()">&rArr;</button>
    </div>

    <div id="main-content" class="center">
        <include-screen screen="currentScreen"></include-screen>
    </div>
</div>

<script type="text/javascript">
    angular.bootstrap('#formentry-app', ['formentry']);
</script>