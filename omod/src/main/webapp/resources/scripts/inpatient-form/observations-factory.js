var module = angular.module('inpatientForm')

module.factory("observationsFactory", function(conceptMappingFactory) {
  var that = this;

  this.createObservations = function(modelQuestions) {

    var postList = [];

    var symptom = {
      concept: "1727AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
      conceptQuestion: "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAA",
      conceptAnswer: "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAA"
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
            console.log(that.findAnswer(concept.answers, modelQuestionValue));
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
      var post = {};
      post.obs = that.createObservations(modelQuestions);
      post.patient = "45068f61-ee74-4117-80b0-b2450da58d0e";
      post.encounterType = "e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
      post.location = "7f65d926-57d6-4402-ae10-a5b3bcbf7986";
      post.provider = "9badd80-ab76-11e2-9e96-0800200c9a66";
      post.visit = "42c29d76-62fa-4816-aa51-17d6ce63b122";
      return post;
    }
  }
});
