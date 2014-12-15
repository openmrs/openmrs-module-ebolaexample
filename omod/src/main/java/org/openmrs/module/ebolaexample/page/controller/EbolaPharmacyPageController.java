package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.*;

public class EbolaPharmacyPageController {

    public void get(@SpringBean("patientService") PatientService patientService, PageModel model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        model.addAttribute("today", getDateToday());
    }

    private Date getDateToday(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);
        return calendar.getTime();
    }
}
