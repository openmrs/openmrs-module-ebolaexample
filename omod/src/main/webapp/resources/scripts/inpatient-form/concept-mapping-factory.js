var module = angular.module('tabletForm')

module.factory("conceptMappingFactory", function() {

  function createQuestion(formId, conceptType, conceptUuid) {
    var question = {
      id: formId,
      type: conceptType,
      conceptId: conceptUuid
    }
    return question;
  }

  function createAnswer(formId, conceptUuid) {
    var answer = {
      id: formId,
      conceptId: conceptUuid
    }
    return answer;
  }

  var answerYes = createAnswer("yes", "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
  var answerNo = createAnswer("no", "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
  var answerTrue = createAnswer(true, "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
  var answerFalse = createAnswer(false, "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
  var answerUnknown = createAnswer("unknown", "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

  var booleanAnswers = [answerYes, answerNo, answerTrue, answerFalse, answerUnknown];


  function createFluidManagement(){
    var question = createQuestion("fluidManagement", "coded", "162653AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerORS = createAnswer("ORS", "351AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerIVMaintenance = createAnswer("IVMaintenance", "162651AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerIVIORessucitate = createAnswer("IVIOResuscitate", "162650AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerJelly = createAnswer("jelly", "162649AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerBloodTransfusion = createAnswer("bloodTransfusion", "1063AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerFluidRestrict = createAnswer("fluidRestrict", "162652AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerORS, answerIVMaintenance, answerIVIORessucitate, answerJelly, answerBloodTransfusion, answerFluidRestrict];
    return question;

  }

  function createAntiMalarials(){
    var question = createQuestion("antiMalarials", "coded", "162686AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerALACT = createAnswer("alAct", "162674AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerArtesunate = createAnswer("artesunate", "71561AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerQuinine = createAnswer("quinine", "83023AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerALACT, answerArtesunate, answerQuinine];
    return question;
  }

  function createAntibiotics(){
    var question = createQuestion("antibiotics", "coded", "162687AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerCeftriaxone = createAnswer("ceftriaxone", "73041AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerCefixime = createAnswer("cefixime", "73006AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerMetronidazole = createAnswer("metronidazole", "79782AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerCeftriaxone, answerCefixime, answerMetronidazole];
    return question;
  }

  function createAnalgesicsAntipyretics(){
    var question = createQuestion("analgesicsAntipyretics", "coded", "162688AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerParacetamol = createAnswer("paracetamol", "70116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerTramadol = createAnswer("tramadol", "85275AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerMorphine = createAnswer("morphine", "80106AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerParacetamol, answerTramadol, answerMorphine];
    return question;
  }

  function loadMapping() {
    var conceptMapping = [];
    conceptMapping.push(createFluidManagement());
    conceptMapping.push(createAntiMalarials());
    conceptMapping.push(createAntibiotics());
    conceptMapping.push(createAnalgesicsAntipyretics());

    return conceptMapping;
  }

  return loadMapping();
});
