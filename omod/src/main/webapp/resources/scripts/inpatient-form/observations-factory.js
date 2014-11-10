var module = angular.module('inpatientForm')

module.factory("observationsFactory", function(conceptMappingFactory) {
  var that = this;

  this.createObservations = function(modelQuestions) {

    var postList = [];

    var symptom = {
      concept: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
      conceptQuestion: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
      conceptAnswer: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    }

    angular.forEach(modelQuestions, function(modelQuestionValue, modelQuestionKey) {
      angular.forEach(conceptMappingFactory, function(concept) {
        var post = {};


        if (concept.id === modelQuestionKey) {
          post.concept = concept.conceptId;

          if (that.isSymptom(concept.type)) {
            post.concept = symptom.concept;
            post.groupMembers = [{
              concept: symptom.conceptQuestion,
              value: concept.conceptId
            }, {
              concept: symptom.conceptAnswer,
              value: that.findAnswer(concept.answers, modelQuestionValue)
            }];
            postList.push(post);
          } else if (that.isNonCode(concept.type)) {
            post.value = modelQuestionValue;
            postList.push(post);
          } else if (that.isCode(concept.type)) {
            post.value = that.findAnswer(concept.answers, modelQuestionValue);
            postList.push(post);
          }
        }

      });

    });

    return postList;
  }

  // Yeah, I know!
  this.isSymptom = function(type) {
    return type === "symptom";
  };

  this.isCode = function(type) {
    return type === "coded";
  };

  this.isNonCode = function(type) {
    return type === "non-coded";
  };

  this.findAnswer = function(answers, value) {
    var response;
    angular.forEach(answers, function(answer) {
      if (answer.id === value) {
        response = answer.conceptId;
      }
    });
    return response;
  };

  return {
    get: function(modelQuestions) {
      return that.createObservations(modelQuestions);
    }
  }
});
