package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/session-info")
class SessionInfoController extends BaseRestController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get() {
        User currentUser = Context.getAuthenticatedUser();
        SimpleObject response = new SimpleObject();
        response.add("userId", currentUser.getUuid());
        if(currentUser.getPerson() != null) {
            response.add("personId", currentUser.getPerson().getUuid());
            response.add("providers", buildProviderList(currentUser));
        }
        return response;
    }

    private List<SimpleObject> buildProviderList(User currentUser) {
        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(currentUser.getPerson());
        List<SimpleObject> providerList = new ArrayList<SimpleObject>();
        for (Provider provider : providers) {
            SimpleObject providerInfo = new SimpleObject();
            providerInfo.add("uuid", provider.getUuid());
            providerList.add(providerInfo);
        }
        return providerList;
    }

}
