package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.patientIdentifierType;

@Component("kerryTownMetadata")
@Requires(EbolaMetadata.class)
public class KerryTownMetadata extends AbstractMetadataBundle {

    public static class _PatientIdentifierType {
        public static final String KERRY_TOWN_IDENTIFIER = "f8bfd104-84b1-11e4-9fc5-eede903351fb";
    }

    @Override
    public void install() throws Exception {
        // TODO set a validator on this identifier type
        install(patientIdentifierType("Kerry Town ID", "ID assigned at Kerry Town ETC", "KT-\\d-\\d{5}", "KT-#-#####", null, PatientIdentifierType.LocationBehavior.NOT_USED, false, _PatientIdentifierType.KERRY_TOWN_IDENTIFIER));
    }

}
