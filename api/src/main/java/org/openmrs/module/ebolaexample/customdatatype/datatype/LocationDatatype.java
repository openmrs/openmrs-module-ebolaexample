package org.openmrs.module.ebolaexample.customdatatype.datatype;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;

public class LocationDatatype extends SerializingCustomDatatype<Location> {

	@Override
	public String serialize(Location typedValue) {
		if (typedValue == null || typedValue.getUuid() == null) {
			return null;
		}
		return typedValue.getUuid();
	}

	@Override
	public Location deserialize(String serializedValue) {
		if (StringUtils.isEmpty(serializedValue)) {
			return null;
		}
		return Context.getLocationService().getLocationByUuid(serializedValue);
	}
}
