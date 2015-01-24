package org.openmrs.module.ebolaexample.pharmacy;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DoseHistoryTest {

    @Test
    public void testDosesByOrder() throws Exception {
        DrugOrder o1 = new DrugOrder();
        DrugOrder o2 = new DrugOrder();
        DrugOrder o3 = new DrugOrder();

        List<ScheduledDose> doses = doses(o1, o2, o3, o2, o3, o3, o3);
        DoseHistory doseHistory = new DoseHistory(null, doses);

        Map<DrugOrder, List<ScheduledDose>> map = doseHistory.getDosesByOrder();
        assertThat(map.size(), is(3));
        assertThat(map.get(o1).size(), is(1));
        assertThat(map.get(o2).size(), is(2));
        assertThat(map.get(o3).size(), is(4));
    }

    private List<ScheduledDose> doses(DrugOrder... drugOrders) {
        List<ScheduledDose> list = new ArrayList<ScheduledDose>();
        for (DrugOrder order : drugOrders) {
            ScheduledDose dose = new ScheduledDose();
            dose.setOrder(order);
            list.add(dose);
        }
        return list;
    }

    @Test
    public void testGroupingOrdersByConcept() throws Exception {
        Concept cipro = new Concept();
        Concept tylenol = new Concept();

        DrugOrder c1 = buildDrugOrder(cipro, "2015-01-02 09:00", "2015-01-04 12:00");
        DrugOrder c2 = buildDrugOrder(cipro, "2015-01-04 12:00", null);
        DrugOrder t1 = buildDrugOrder(tylenol, "2015-01-01 09:00", "2015-01-01 17:00");
        DrugOrder t2 = buildDrugOrder(tylenol, "2015-01-02 09:00", "2015-01-02 17:00");
        DrugOrder t3 = buildDrugOrder(tylenol, "2015-01-03 09:00", "2015-01-03 17:00");

        DoseHistory history = new DoseHistory(Arrays.asList(t2, c1, t3, c2, t1), null);
        List<Map.Entry<Concept, List<DrugOrder>>> groups = history.getOrdersGroupedByConcept();

        assertThat(groups.get(0).getKey(), is(cipro));
        assertThat(groups.get(0).getValue().get(0), is(c2));
        assertThat(groups.get(0).getValue().get(1), is(c1));

        assertThat(groups.get(1).getKey(), is(tylenol));
        assertThat(groups.get(1).getValue().get(0), is(t3));
        assertThat(groups.get(1).getValue().get(1), is(t2));
        assertThat(groups.get(1).getValue().get(2), is(t1));
    }

    @Test
    public void testGroupingOrdersByDrug() throws Exception {
        Drug cipro250 = new Drug();
        Drug cipro500 = new Drug();

        DrugOrder a1 = buildDrugOrder(cipro250, "2015-01-02 09:00", "2015-01-04 12:00");
        DrugOrder a2 = buildDrugOrder(cipro250, "2015-01-04 12:00", null);
        DrugOrder b1 = buildDrugOrder(cipro500, "2015-01-01 09:00", "2015-01-01 17:00");
        DrugOrder b2 = buildDrugOrder(cipro500, "2015-01-02 09:00", "2015-01-02 17:00");
        DrugOrder b3 = buildDrugOrder(cipro500, "2015-01-03 09:00", "2015-01-03 17:00");

        DoseHistory history = new DoseHistory(Arrays.asList(b2, a1, b3, a2, b1), null);
        List<Map.Entry<Drug, List<DrugOrder>>> groups = history.getOrdersGroupedByDrug();

        assertThat(groups.get(0).getKey(), is(cipro250));
        assertThat(groups.get(0).getValue().get(0), is(a2));
        assertThat(groups.get(0).getValue().get(1), is(a1));

        assertThat(groups.get(1).getKey(), is(cipro500));
        assertThat(groups.get(1).getValue().get(0), is(b3));
        assertThat(groups.get(1).getValue().get(1), is(b2));
        assertThat(groups.get(1).getValue().get(2), is(b1));
    }

    private DrugOrder buildDrugOrder(Object conceptOrDrug, String ymdhmStart, String ymdhmStop) throws ParseException {
        DrugOrder order = new DrugOrder();
        if (conceptOrDrug instanceof Concept) {
            order.setConcept((Concept) conceptOrDrug);
        }
        else {
            order.setDrug((Drug) conceptOrDrug);
        }

        order.setDateActivated(date(ymdhmStart));
        if (ymdhmStop != null) {
            order.setAutoExpireDate(date(ymdhmStop));
        }
        return order;
    }

    private Date date(String ymdhm) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(ymdhm);
    }

}