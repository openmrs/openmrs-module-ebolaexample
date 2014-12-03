package org.openmrs.module.ebolaexample.db;

import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.db.hibernate.SingleClassHibernateDAO;

public class HibernateScheduledDoseDAO extends SingleClassHibernateDAO<ScheduledDose> implements ScheduledDoseDAO {

    public HibernateScheduledDoseDAO() {
        super(ScheduledDose.class);
    }

}
