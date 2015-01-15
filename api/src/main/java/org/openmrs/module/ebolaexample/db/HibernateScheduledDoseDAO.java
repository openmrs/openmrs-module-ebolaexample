package org.openmrs.module.ebolaexample.db;

import org.openmrs.Order;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.db.hibernate.SingleClassHibernateDAO;

import java.util.Iterator;
import java.util.List;

public class HibernateScheduledDoseDAO extends SingleClassHibernateDAO<ScheduledDose> implements ScheduledDoseDAO {

    public HibernateScheduledDoseDAO() {
        super(ScheduledDose.class);
    }

    @Override
    public ScheduledDose getScheduledDoseByUuid(String uuid) {
        return (ScheduledDose) sessionFactory.getCurrentSession()
                .createQuery("from ScheduledDose s where s.uuid = :uuid")
                .setString("uuid", uuid)
                .uniqueResult();
    }

    @Override
    public List<ScheduledDose> getScheduledDoseByOrderId(Order order) {
        return (List<ScheduledDose>) sessionFactory.getCurrentSession()
                .createQuery("from ScheduledDose s where s.order = :order")
                .setEntity("order", order)
                .list();
    }
}
