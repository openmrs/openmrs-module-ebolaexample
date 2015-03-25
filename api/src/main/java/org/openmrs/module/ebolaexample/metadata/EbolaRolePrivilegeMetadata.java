package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

@Component("ebolaRolePrivilegeMetadata")
public class EbolaRolePrivilegeMetadata extends AbstractMetadataBundle {

    public static class _Privilege {
        public static final String APP_COREAPPS_FIND_PATIENT = "App: coreapps.findPatient";
        public static final String APP_COREAPPS_ACTIVE_VISITS = "App: coreapps.activeVisits";
        public static final String APP_COREAPPS_PATIENT_DASHBOARD = "App: coreapps.patientDashboard";
        public static final String APP_REGISTRATIONAPP_REGISTER_PATIENT = "App: registrationapp.registerPatient";
        public static final String APP_EBOLAEXAMPLE_EBOLA_PHARMACY = "App: ebolaexample.ebolaPharmacy";
        public static final String APP_EBOLAEXAMPLE_ACTIVE_PATIENTS = "App: ebolaexample.activePatients";
        public static final String APP_EBOLAEXAMPLE_DATA_EXPORT = "App: ebolaexample.dataExport";
    }

    public static class _Role {

        public static final String APPLICATION_REGISTERS_PATIENTS = "Application: Registers Patients";
        public static final String APPLICATION_LOOKS_UP_PATIENTS = "Application: Looks up Patients";

        public static final String EBOLA_DOCTOR = "Ebola: Doctor";
        public static final String EBOLA_NURSE = "Ebola: Nurse";
        public static final String EBOLA_PHARMACIST = "Ebola: Pharmacist";
    }

    @Override
    public void install() throws Exception {
        install(privilege(_Privilege.APP_COREAPPS_FIND_PATIENT, "Able to access the find patient app"));
        install(privilege(_Privilege.APP_COREAPPS_ACTIVE_VISITS, "Able to access the active visits app"));
        install(privilege(_Privilege.APP_COREAPPS_PATIENT_DASHBOARD, "Able to access the patient dashboard"));
        install(privilege(_Privilege.APP_REGISTRATIONAPP_REGISTER_PATIENT, "Able to access the register patient app"));
        install(privilege(_Privilege.APP_EBOLAEXAMPLE_EBOLA_PHARMACY, "Able to access the find Ebola pharmacy app"));
        install(privilege(_Privilege.APP_EBOLAEXAMPLE_ACTIVE_PATIENTS, "Able to list active patients app"));
        install(privilege(_Privilege.APP_EBOLAEXAMPLE_DATA_EXPORT, "Able to download patient data as CSV"));

        install(role(_Role.APPLICATION_LOOKS_UP_PATIENTS, "Looks up patients", idSet(), idSet(
                _Privilege.APP_COREAPPS_FIND_PATIENT,
                _Privilege.APP_EBOLAEXAMPLE_DATA_EXPORT)));

        install(role(_Role.APPLICATION_REGISTERS_PATIENTS, "Registers patients", idSet(), idSet(
                _Privilege.APP_COREAPPS_ACTIVE_VISITS,
                _Privilege.APP_REGISTRATIONAPP_REGISTER_PATIENT,
                _Privilege.APP_COREAPPS_PATIENT_DASHBOARD)));

        install(role(_Role.EBOLA_DOCTOR, "Doctor", idSet(
                _Role.APPLICATION_REGISTERS_PATIENTS,
                _Role.APPLICATION_LOOKS_UP_PATIENTS), idSet()));

        install(role(_Role.EBOLA_NURSE, "Nurse", idSet(
                _Role.APPLICATION_REGISTERS_PATIENTS,
                _Role.APPLICATION_LOOKS_UP_PATIENTS), idSet()));

        install(role(_Role.EBOLA_PHARMACIST, "Pharmacist", idSet(
                _Role.APPLICATION_LOOKS_UP_PATIENTS), idSet(
                _Privilege.APP_EBOLAEXAMPLE_EBOLA_PHARMACY)));
    }
}
