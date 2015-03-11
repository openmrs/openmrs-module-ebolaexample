package org.openmrs.module.ebolaexample.domain;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class IvFluidOrderTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private OrderService orderService;

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
    public void shouldSaveValidOrders() throws Exception {
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

        assertNotNull(ivFluidOrder.getId());
    }

    private Concept getConceptByUuid(String uuid) {
        return Context.getConceptService().getConceptByUuid(uuid);
    }

}
