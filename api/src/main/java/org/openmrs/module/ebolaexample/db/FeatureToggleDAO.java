package org.openmrs.module.ebolaexample.db;

import org.hibernate.SessionFactory;
import org.openmrs.module.ebolaexample.domain.FeatureToggle;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("featureToggleDAO")
public class FeatureToggleDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public FeatureToggle getByName(String toggleName){
        FeatureToggle toggle = (FeatureToggle) sessionFactory.getCurrentSession()
                .createQuery("from FeatureToggle f where f.name=:name")
                .setString("name", toggleName)
                .setMaxResults(1).uniqueResult();
        return toggle;
    }

    @Transactional
    public void saveOrUpdate(FeatureToggle toggle){
        sessionFactory.getCurrentSession().saveOrUpdate(toggle);
    }

    @Transactional(readOnly = true)
    public List<FeatureToggle> getAll() {
        List<FeatureToggle> toggles = (List<FeatureToggle>) sessionFactory.getCurrentSession()
                .createQuery("from FeatureToggle").list();
        return toggles;
    }
}
