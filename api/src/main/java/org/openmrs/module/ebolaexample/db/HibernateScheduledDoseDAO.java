package org.openmrs.module.ebolaexample.db;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.db.hibernate.SingleClassHibernateDAO;

import java.util.Date;
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

    @Override
    public List<ScheduledDose> getScheduledDosesByPatientAndDateRange(Patient patient, Date onOrAfter, Date onOrBefore) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ScheduledDose.class);
        Query query = sessionFactory.getCurrentSession()
                .createQuery("from ScheduledDose " +
                        "where order.patient = :patient " +
                        "and scheduledDatetime between :onOrAfter and :onOrBefore")
                .setEntity("patient", patient)
                .setTimestamp("onOrAfter", onOrAfter)
                .setTimestamp("onOrBefore", onOrBefore);
        return query.list();
//        criteria.add(Restrictions.eq("order.patient", patient));
//        criteria.add(Restrictions.between("scheduledDatetime", onOrAfter, DateUtil.getEndOfDayIfTimeExcluded(onOrBefore)));
//        return criteria.list();
    }
}
