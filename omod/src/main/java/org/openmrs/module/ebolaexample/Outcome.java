package org.openmrs.module.ebolaexample;

public class Outcome {
    private String uuid;
    private String name;

    public Outcome(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public static Outcome getOutcomeByConceptId(Integer conceptId) {
        // ui.i18n.Concept.name.162684AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Suspected negative and discharged 162684
        // ui.i18n.Concept.name.159791AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Cured and discharged 159791
        // ui.i18n.Concept.name.1694AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Discharged against medical advice 1694
        // ui.i18n.Concept.name.142177AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Suspect referred to other facility 142177
        // ui.i18n.Concept.name.159392AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Confirmed case transferred to other facility 159392
        // ui.i18n.Concept.name.160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Died on ward 160034
        // ui.i18n.Concept.name.142934AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=Dead on arrival 142934
        switch (conceptId) {
            case 162684:
                return new Outcome("162684AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Suspected negative and discharged");
            case 159791:
                return new Outcome("159791AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Cured and discharged");
            case 1694:
                return new Outcome("1694AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Discharged against medical advice");
            case 142177:
                return new Outcome("142177AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Suspect referred to other facility");
            case 159392:
                return new Outcome("159392AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Confirmed case transferred to other facility");
            case 160034:
                return new Outcome("160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Died on ward");
            case 142934:
                return new Outcome("142934AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Dead on arrival");
        }
        return null;
    }
}