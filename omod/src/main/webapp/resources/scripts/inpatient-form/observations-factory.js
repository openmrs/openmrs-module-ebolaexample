var module = angular.module('inpatientForm')

module.factory("observationsFactory", function(conceptMappingFactory) {
  var that = this;

  this.createObservations = function(modelQuestions) {

    var postList = [];

    angular.forEach(modelQuestions, function(modelQuestionValue, modelQuestionKey) {
      angular.forEach(conceptMappingFactory, function(concept) {
        var post = {};

        if (concept.type === "symptom") {
          var answerValue;
          angular.forEach(concept.answers, function(answer) {
            if (answer.id === modelQuestionValue) {
              answerValue = answer.conceptId;
            }
          });

          post.concept = "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAA";
          post.groupMembers = [{
            concept: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            value: concept.conceptId
          }, {
            concept: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            value: answerValue
          }];
        }

        if (concept.id === modelQuestionKey) {
          post.concept = concept.id;

          if (concept.type === "non-coded") {
            post.value = modelQuestionValue;
          } else if (concept.type == "coded") {
            angular.forEach(concept.answers, function(answer) {
              if (answer.id === modelQuestionValue) {
                post.value = answer.conceptId;
              }
            });
          }

          postList.push(post);
        }
      });

    });

    return postList;
  }

  return {
    get: function(modelQuestions) {
      return that.createObservations(modelQuestions);
    }
  }
});
