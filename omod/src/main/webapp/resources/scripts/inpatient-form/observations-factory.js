var module = angular.module('inpatientForm')

module.factory("observationsFactory", function(conceptMappingFactory) {
  var that = this;

  this.viewModel = {
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

  angular.forEach(that.viewModel, function(modelQuestionValue, modelQuestionKey) {

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


 return postList;


});
