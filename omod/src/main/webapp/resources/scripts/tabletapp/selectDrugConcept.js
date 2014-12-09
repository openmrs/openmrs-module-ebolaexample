angular.module('select-drug-concept', [ 'drugConceptService', 'ui.bootstrap' ])

    .directive('selectDrugConcept', [ 'DrugConceptService', '$timeout', function (DrugConceptService, $timeout) {

        return {
            restrict: 'E',
            scope: {
                ngModel: '=',
                id: '@',
                placeholder: '@'
            },
            link: function ($scope, element, attrs) {
                $scope.required = attrs.hasOwnProperty('required'); // required attribute has no value
                $scope.inputId = emr.domId($scope.id, 'sel-drug', 'input');
                $scope.size = attrs.size ? attrs.size : 40;

                $scope.search = function (term) {
                    return DrugConceptService.getDrugConcepts(term).then(function (response) {
                        return response.data["drugConcepts"];
                    });
                }

                $scope.verify = function () {
                    if (!$scope.ngModel) {
                        $('#' + $scope.inputId).val('');
                    }
                }

                $scope.onSelect = function ($model) {
                    alert($model);
                }

            },
            template: '<input type="text" id="{{ inputId }}" ng-model="ngModel" ng-blur="verify()" ' +
                'typeahead="drug as drug.display for drug in search($viewValue) | filter:$viewValue" ' +
//                'typeahead-on-select="{{ onselect }}" ' +
                'typeahead-editable="false" ' +
                'autocomplete="off" ' +
                'placeholder="{{ placeholder }}" ' +
                'autocomplete="off" ' +
                'ng-required="{{ required }}" ' +
                'size="{{ size }}" ' +
                'typeahead-wait-ms="20" ' +
                'typeahead-min-length="3" />'
        };
    }]);
