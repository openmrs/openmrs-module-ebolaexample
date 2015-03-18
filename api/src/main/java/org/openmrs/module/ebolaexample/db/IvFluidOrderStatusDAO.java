package org.openmrs.module.ebolaexample.db;

import org.hibernate.SessionFactory;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("ivFluidOrderStatusDAO")
public class IvFluidOrderStatusDAO{
    @Autowired
    SessionFactory sessionFactory;
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public IvFluidOrderStatus getLatestIvFluidOrderStatus(IvFluidOrder order){
        IvFluidOrderStatus orderStatus = (IvFluidOrderStatus) sessionFactory.getCurrentSession().createQuery(
                "from IvFluidOrderStatus s where s.ivFluidOrder = :order and s.voided = false order by dateCreated desc")
                .setEntity("order", order).setMaxResults(1)
                .uniqueResult();
        return orderStatus;
    }


    @Transactional(readOnly = true)
    public List<IvFluidOrderStatus> getIvFluidOrderStatuses(IvFluidOrder order){
        List<IvFluidOrderStatus> orderStatus = sessionFactory.getCurrentSession().createQuery(
                "from IvFluidOrderStatus s where s.ivFluidOrder = :order and s.voided = false order by dateCreated desc")
                .setEntity("order", order).list();
        return orderStatus;
    }

    @Transactional
    public IvFluidOrderStatus saveOrUpdate(IvFluidOrderStatus status){
        sessionFactory.getCurrentSession().saveOrUpdate(status);
        return status;
    }

}

