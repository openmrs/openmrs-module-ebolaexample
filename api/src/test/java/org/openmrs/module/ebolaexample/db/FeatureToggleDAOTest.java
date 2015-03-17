package org.openmrs.module.ebolaexample.db;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.ebolaexample.domain.FeatureToggle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FeatureToggleDAOTest  extends BaseModuleContextSensitiveTest {
    @Autowired
    FeatureToggleDAO dao;

    @Before
    public void setup() throws Exception {
        initializeInMemoryDatabase();
    }

    @Test
    public void shouldBeAbleToTurnFeatueToggleOnEvenNoToggleFoundInDB(){
        FeatureToggle toggle = new FeatureToggle();
        String name = "feature toggle 1";
        toggle.setName(name);
        toggle.setEnabled(true);
        dao.saveOrUpdate(toggle);
        toggle = dao.getByName(name);
        Assert.assertTrue(toggle.getEnabled());
    }

    @Test
    public void shouldBeAbleToTurnFeatueToggleOffEvenNoToggleFoundInDB(){
        FeatureToggle toggle = new FeatureToggle();
        String name = "feature toggle 1";
        toggle.setName(name);
        toggle.setEnabled(false);
        dao.saveOrUpdate(toggle);
        toggle = dao.getByName(name);
        Assert.assertFalse(toggle.getEnabled());
    }

    @Test
    public void shouldBeAbleToGetAllFeatureToggles(){
        FeatureToggle featureToggle1 = new FeatureToggle();
        featureToggle1.setName("toggle 1");
        dao.saveOrUpdate(featureToggle1);
        FeatureToggle featureToggle2 = new FeatureToggle();
        featureToggle2.setName("toggle 2");
        dao.saveOrUpdate(featureToggle2);
        List<FeatureToggle> featureToggles = dao.getAll();
        Assert.assertEquals(2, featureToggles.size());
    }
}