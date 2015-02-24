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
                .createQuery("from ScheduledDose s where s.order = :order and s.voided = false")
                .setEntity("order", order)
                .list();
    }

    @Override
    public List<ScheduledDose> getScheduledDosesByPatientAndDateRange(Date onOrAfter, Date onOrBefore, Patient patient, boolean includeVoided) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ScheduledDose.class);
        Query query = (includeVoided) ? sessionFactory.getCurrentSession()
                .createQuery("from ScheduledDose " +
                        "where order.patient = :patient " +
                        "and scheduledDatetime between :onOrAfter and :onOrBefore")
                : sessionFactory.getCurrentSession()
                .createQuery("from ScheduledDose " +
                        "where order.patient = :patient " +
                        "and voided = false and scheduledDatetime between :onOrAfter and :onOrBefore");

        query.setEntity("patient", patient)
                .setTimestamp("onOrAfter", onOrAfter)
                .setTimestamp("onOrBefore", onOrBefore);

        return query.list();
//        criteria.add(Restrictions.eq("order.patient", patient));
//        criteria.add(Restrictions.between("scheduledDatetime", onOrAfter, DateUtil.getEndOfDayIfTimeExcluded(onOrBefore)));
//        return criteria.list();
    }
}
