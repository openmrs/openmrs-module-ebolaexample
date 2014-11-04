var module = angular.module('inpatientForm')

module.service("clientServerService", function() {


  function createQuestion(formId, conceptType, conceptUuid) {
    var question = {
      id: formId,
      type: conceptType,
      conceptId: conceptUuid
    }
    return question;
  }


  function createAnswer(formId, conceptUuid) {
    var question = {
      id: formId,
      conceptId: conceptUuid
    }
    return question;
  }

});
