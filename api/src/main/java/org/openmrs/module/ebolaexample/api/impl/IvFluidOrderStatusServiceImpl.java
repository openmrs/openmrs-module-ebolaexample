package org.openmrs.module.ebolaexample.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ebolaexample.api.IvFluidOrderStatusService;
import org.openmrs.module.ebolaexample.db.IvFluidOrderStatusDAO;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;

import java.util.List;

public class IvFluidOrderStatusServiceImpl extends BaseOpenmrsService implements IvFluidOrderStatusService {

    IvFluidOrderStatusDAO ivFluidOrderStatusDAO;

    public void setIvFluidOrderStatusDAO(IvFluidOrderStatusDAO dao){
        this.ivFluidOrderStatusDAO = dao;
    }

    @Override
    public void saveStatus(IvFluidOrder order, IvFluidOrderStatus.IVFluidOrderStatus status) {
        IvFluidOrderStatus ivFluidOrderStatus = new IvFluidOrderStatus(order, status);
        ivFluidOrderStatusDAO.save(ivFluidOrderStatus);
    }

    @Override
    public IvFluidOrderStatus getCurrentStatus(IvFluidOrder order) {
        return ivFluidOrderStatusDAO.getLatestIvFluidOrderStatus(order);
    }
}
