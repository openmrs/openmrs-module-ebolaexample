var module = angular.module('inpatientForm')

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

  function createCurrentConsciousness() {
    var question = createQuestion("currentConsciousness", "coded", "162643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerA = createAnswer("A", "160282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerV = createAnswer("V", "162645AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerP = createAnswer("P", "162644AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerU = createAnswer("U", "159508AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerA, answerV, answerP, answerU];
    return question;
  }

  function createTemperature() {
    var question = createQuestion("temperature", "non-coded", "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createOxigenSaturation() {
    var question = createQuestion("oxigenSaturation", "non-coded", "5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createRespiratoryRate() {
    var question = createQuestion("respiratoryRate", "non-coded", "5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createHeartRate() {
    var question = createQuestion("heartRate", "non-coded", "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createSystolicBP() {
    var question = createQuestion("systolicBP", "non-coded", "5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createDiastolicBP() {
    var question = createQuestion("diastolicBP", "non-coded", "5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createRaisedJVP() {
    var question = createQuestion("raisedJVP", "non-coded", "162646AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createCapillaryRefilTime() {
    var question = createQuestion("capillaryRefilTime", "non-coded", "162513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createAbdomenTender() {
    var question = createQuestion("abdomenTender", "coded", "5105AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerYes = createAnswer("yes", "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNo = createAnswer("no", "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerYes, answerNo];
    return question;
  }

  function createPaleAnaemia() {
    var question = createQuestion("paleAnaemia", "coded", "131004AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNone = createAnswer("none", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerMild = createAnswer("mild", "1498AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerModerate = createAnswer("moderate", "1499AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerSevere = createAnswer("severe", "1500AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerNone, answerMild, answerModerate, answerSevere];
    return question;
  }

  function createOralFluids() {
    var question = createQuestion("oralFluids", "non-coded", "162658AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createDehydration() {
    var question = createQuestion("dehydration", "coded", "142630AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNone = createAnswer("none", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerMild = createAnswer("mild", "1498AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerModerate = createAnswer("moderate", "1499AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerSevere = createAnswer("severe", "1500AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerNone, answerMild, answerModerate, answerSevere];
    return question;
  }

  function createUrineOutput() {
    var question = createQuestion("urineOutput", "coded", "162647AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNormal = createAnswer("normal", "1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNone = createAnswer("none", "162648AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerReduced = createAnswer("reduced", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerUnknown = createAnswer("unknown", "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerNormal, answerNone, answerReduced, answerUnknown];
    return question;
  }

  function createVomiting() {
    var question = createQuestion("vomiting", "coded", "122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNone = createAnswer("none", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerMild = createAnswer("mild", "1498AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerModerate = createAnswer("moderate", "1499AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerSevere = createAnswer("severe", "1500AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerNone, answerMild, answerModerate, answerSevere];
    return question;
  }

  function createStoolType() {
    var question = createQuestion("mainStoolType", "coded", "162654AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerFormed = createAnswer("formed", "162655AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerLiquid = createAnswer("liquid", "162656AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerSemiFormed = createAnswer("semiFormed", "162657AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerNone = createAnswer("none", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerFormed, answerLiquid, answerSemiFormed, answerNone];
    return question;
  }

  function createStoolFrequency() {
    var question = createQuestion("stoolFreq", "non-coded", "1837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;
  }

  function createFluidManagement(){ 
    var question = createQuestion("fluidManagement", "coded", "162653AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerORS = createAnswer("ORS", "351AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerIVMaintenance = createAnswer("IVMaintenance", "162649AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerIVIORessucitate = createAnswer("IVIORessucitate", "162651AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerJelly = createAnswer("jelly", "162650AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerBloodTransfusion = createAnswer("bloodTransfusion", "1063AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerFluidRestrict = createAnswer("fluidRestrict", "162652AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [answerORS, answerIVMaintenance, answerIVIORessucitate, answerJelly, answerBloodTransfusion, answerFluidRestrict];
    return question;  

  }

  function createTargetVolume(){
    var question = createQuestion("targetVolumeInNext24h", "non-coded", "162675AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    question.answers = [];
    return question;  
  }

  function createAntiMalarials(){
    var question = createQuestion("antiMalarials", "coded", "162686AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    var answerALACT = createAnswer("ALACT", "162674AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
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
    conceptMapping.push(createCurrentConsciousness());
    conceptMapping.push(createTemperature());
    conceptMapping.push(createOxigenSaturation());
    conceptMapping.push(createRespiratoryRate());
    conceptMapping.push(createHeartRate());
    conceptMapping.push(createSystolicBP());
    conceptMapping.push(createDiastolicBP());
    conceptMapping.push(createRaisedJVP());
    conceptMapping.push(createCapillaryRefilTime());
    conceptMapping.push(createAbdomenTender());
    conceptMapping.push(createPaleAnaemia());
    conceptMapping.push(createOralFluids());
    conceptMapping.push(createDehydration());
    conceptMapping.push(createUrineOutput());
    conceptMapping.push(createVomiting());
    conceptMapping.push(createStoolType());
    conceptMapping.push(createStoolFrequency());
    conceptMapping.push(createFluidManagement());
    conceptMapping.push(createTargetVolume());
    conceptMapping.push(createAntiMalarials());
    conceptMapping.push(createAntibiotics());
    conceptMapping.push(createAnalgesicsAntipyretics());

    return conceptMapping;
  }

  return loadMapping();
});
