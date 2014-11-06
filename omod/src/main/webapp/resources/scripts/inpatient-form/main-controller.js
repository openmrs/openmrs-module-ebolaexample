var module = angular.module('inpatientForm');

module.controller('MainController', function ($scope, conceptMappingFactory) {
    var createView = function (name, file, description) {
            return {
                name: name,
                file: file,
                description: description
            }
        }, activeView = undefined,
        initialize = function () {
            activeView = $scope.views[0];
        },
        getActiveViewIndex = function () {
            return _.indexOf($scope.views, activeView);
        };

    $scope.views = [
        createView('vital-signs', 'vital-signs.html', 'VITAL SIGNS'),
        createView('hydration', 'hydration.html', 'HYDRATION'),
        createView('symptoms', 'symptoms.html', 'SYMPTOMS'),
        createView('daily-management', 'daily-management.html', 'DAILY MGMT')
    ];

    $scope.shouldDisplay = function (view) {
        return activeView === view;
    };

    $scope.shouldDisplayBackButton = function () {
        return getActiveViewIndex() !== 0;
    };

    $scope.shouldDisplayNextButton = function () {
        return getActiveViewIndex() !== $scope.views.length-1;
    };

    $scope.shouldDisplayFinishButton = function () {
        return getActiveViewIndex() === $scope.views.length-1;
    };

    $scope.next = function () {
        activeView = $scope.views[getActiveViewIndex() + 1];
    };

    $scope.back = function () {
        activeView = $scope.views[getActiveViewIndex() - 1];
    };

    $scope.finish= function () {
        alert('Not there yet!');
    };

    $scope.display = function (view) {
        activeView = view;
    };

    initialize();

  $scope.viewModel = {
    oralFluids: '',
    dehydration: '',
    urineOutput: '',
    vomiting: '',
    stoolFreq: '',
    mainStool: '',
    respiratoryRate: ''
  };


  var postList = [];
  var conceptList = conceptMappingFactory;

  angular.forEach($scope.viewModel, function(modelQuestionValue, modelQuestionKey) {



    angular.forEach(conceptMappingFactory, function(concept) {
      var post = {};

      if(concept.type === "symptom"){
         var answerValue;
      	 angular.forEach(concept.answers, function(answer) {
          	if(answer.id === modelQuestionValue){
          		answerValue = answer.conceptId;
          	}
          });

      	post.concept = "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAA";
      	post.groupMembers = [ 
      		{
      			concept: "",
      			value: concept.conceptId
      		},
      		{
				concept:  "",
				value: answerValue      			
      		}
      	];
      }

      if (concept.id === modelQuestionKey) {
        post.concept = concept.id;

        if (concept.type === "non-coded") {
          post.value = modelQuestionValue;
        }

        if (concept.type == "coded") {
          angular.forEach(concept.answers, function(answer) {
          	if(answer.id === modelQuestionValue){
          		post.value = answer.conceptId;
          	}
          });
        }

        postList.push(post);
      }
    });


  });


  console.table(postList);

});
