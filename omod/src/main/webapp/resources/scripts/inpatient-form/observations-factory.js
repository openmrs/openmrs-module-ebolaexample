var module = angular.module('inpatientForm')

module.factory("observationsFactory", function(conceptMappingFactory) {
  var that = this;

  this.createObservations = function(modelQuestions) {

    var postList = [];

    var symptomGrpup = {
     concept: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
     conceptQuestion: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
     conceptAnswer: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    }

    angular.forEach(modelQuestions, function(modelQuestionValue, modelQuestionKey) {
      angular.forEach(conceptMappingFactory, function(concept) {
        var post = {};


        if (concept.id === modelQuestionKey) {
          post.concept = concept.id;

          if (that.isSymptom(concept.type)) {
            post.concept = symptom.concept;
            post.groupMembers = [{
              concept: symptom.conceptQuestion,
              value: concept.conceptId
            }, {
              concept: symptom.conceptAnswer,
              value: that.findAnswer(answers, modelQuestionValue)
            }];

          } else if (that.isNonCode(concept.type)) {
            post.value = modelQuestionValue;
          } else if (that.isCode(concept.type)) {
            post.value = that.findAnswer(answers, modelQuestionValue);
          }
        }

        postList.push(post);
      });

    });

    return postList;
  }

  // Yeah, I know!
  this.isSymptom = function(type) {
    return type === "symptom";
  };

  this.isCode = function(type) {
    return type === "non-coded";
  };

  this.isNonCode = function(type) {
    return type === "non-coded";
  };

  this.findAnswer = function(answers, value) {
    angular.forEach(answers, function(answer) {
      if (answer.id === value) {
        return answer.conceptId;
      }
    });

  };

  return {
    get: function(modelQuestions) {
      return that.createObservations(modelQuestions);
    }
  }
});
