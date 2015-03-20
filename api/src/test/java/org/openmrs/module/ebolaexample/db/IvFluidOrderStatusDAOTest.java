package org.openmrs.module.ebolaexample.db;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.domain.AdministrationType;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class IvFluidOrderStatusDAOTest extends BaseModuleContextSensitiveTest {
    @Autowired
    IvFluidOrderStatusDAO dao;

    @Autowired
    OrderService orderService;


    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    private OrderType ivFluidOrderType;

    @Before
    public void setup() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        ivFluidOrderType = Context.getOrderService().getOrderTypeByUuid(EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID);
    }

    @Test
    public void shouldSetIvFluidStatusAsStarted(){
        User user = Context.getAuthenticatedUser();
        IvFluidOrder ivFluidOrder = prepareIvFluidOrder();
        IvFluidOrderStatus startedStatus = new IvFluidOrderStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.STARTED);
        startedStatus.setCreator(user);
        dao.saveOrUpdate(startedStatus);

        IvFluidOrderStatus latestIvFluidOrderStatus = dao.getLatestIvFluidOrderStatus(ivFluidOrder);

        Assert.assertEquals(latestIvFluidOrderStatus.getCreator().getId(), startedStatus.getCreator().getId());
        Assert.assertEquals(latestIvFluidOrderStatus.getStatus(), startedStatus.getStatus());
        Assert.assertEquals(latestIvFluidOrderStatus.getUuid(), startedStatus.getUuid());
    }

    @Test
    public void shouldGetAllIvFluidOrderStatuses(){
        User user = Context.getAuthenticatedUser();
        IvFluidOrder ivFluidOrder = prepareIvFluidOrder();
        IvFluidOrderStatus startedStatus = new IvFluidOrderStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.STARTED);
        startedStatus.setCreator(user);
        Date dateCreated = DateUtils.addDays(new Date(), -1);
        startedStatus.setDateCreated(dateCreated);
        dao.saveOrUpdate(startedStatus);

        IvFluidOrderStatus heldStatus = new IvFluidOrderStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.HELD);
        heldStatus.setCreator(user);
        dao.saveOrUpdate(heldStatus);

        List<IvFluidOrderStatus> ivFluidOrderStatuses = dao.getIvFluidOrderStatuses(ivFluidOrder);
        Assert.assertEquals(2, ivFluidOrderStatuses.size());
        Assert.assertEquals(heldStatus.getStatus(), ivFluidOrderStatuses.get(0).getStatus());
    }

    @Test
    public void shouldGetLatestIvFluidOrderStatus(){
        User user = Context.getAuthenticatedUser();
        IvFluidOrder ivFluidOrder = prepareIvFluidOrder();
        IvFluidOrderStatus startedStatus = new IvFluidOrderStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.STARTED);
        startedStatus.setCreator(user);
        Date dateCreated = DateUtils.addDays(new Date(), -1);
        startedStatus.setDateCreated(dateCreated);
        dao.saveOrUpdate(startedStatus);

        IvFluidOrderStatus heldStatus = new IvFluidOrderStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.HELD);
        heldStatus.setCreator(user);
        dao.saveOrUpdate(heldStatus);

        IvFluidOrderStatus latestIvFluidOrderStatus = dao.getLatestIvFluidOrderStatus(ivFluidOrder);
        Assert.assertEquals(latestIvFluidOrderStatus.getCreator().getId(), heldStatus.getCreator().getId());
        Assert.assertEquals(latestIvFluidOrderStatus.getStatus(), heldStatus.getStatus());
        Assert.assertEquals(latestIvFluidOrderStatus.getUuid(), heldStatus.getUuid());
    }

    private IvFluidOrder prepareIvFluidOrder(){
        Patient patient = Context.getPatientService().getAllPatients().get(2);

        IvFluidOrder ivFluidOrder = new IvFluidOrder();
        ivFluidOrder.setOrderType(ivFluidOrderType);
        ivFluidOrder.setConcept(getConceptByUuid("15f83cd6-64e9-4e06-a5f9-364d3b14a43d"));
        ivFluidOrder.setRoute(getConceptByUuid("160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        ivFluidOrder.setAdministrationType(AdministrationType.BOLUS);
        ivFluidOrder.setBolusQuantity(50.0);
        ivFluidOrder.setBolusUnits(getConceptByUuid("162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        ivFluidOrder.setBolusRate(15);
        ivFluidOrder.setBolusRateUnits(getConceptByUuid("1733AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        ivFluidOrder.setPatient(patient);
        ivFluidOrder.setCareSetting(Context.getOrderService().getCareSetting(2));
        ivFluidOrder.setEncounter(Context.getEncounterService().getEncountersByPatient(patient).get(0));
        ivFluidOrder.setOrderer(Context.getProviderService().getAllProviders().get(0));

        orderService.saveOrder(ivFluidOrder, null);
        return ivFluidOrder;
    }
    private Concept getConceptByUuid(String uuid) {
        return Context.getConceptService().getConceptByUuid(uuid);
    }

}
