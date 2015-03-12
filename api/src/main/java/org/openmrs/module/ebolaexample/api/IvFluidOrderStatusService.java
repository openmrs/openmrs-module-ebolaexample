package org.openmrs.module.ebolaexample.api;


import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;

public interface IvFluidOrderStatusService extends OpenmrsService {
    public void saveStatus(IvFluidOrder order, IvFluidOrderStatus.IVFluidOrderStatus status);
    public IvFluidOrderStatus getCurrentStatus(IvFluidOrder order);
}
