package org.openmrs.module.ebolaexample

import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class DateUtilTest {

    @Test
    public void shouldReturnTrueWhenDateIsInLast24Hours() throws Exception {
        Calendar calendar = Calendar.getInstance();
        assertTrue(DateUtil.isInLast24Hours(calendar.getTime()))

        calendar.add(Calendar.HOUR, -4);
        assertTrue(DateUtil.isInLast24Hours(calendar.getTime()))

        calendar.add(Calendar.HOUR, -34);
        assertFalse(DateUtil.isInLast24Hours(calendar.getTime()))
    }
}
