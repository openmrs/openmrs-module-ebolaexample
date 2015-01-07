package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
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
    public Object get() throws Exception {
        User currentUser = Context.getAuthenticatedUser();
        SimpleObject response = new SimpleObject();
        response.add("user", userRef(currentUser));
        if(currentUser.getPerson() != null) {
            response.add("person", personRef(currentUser));
            response.add("providers", buildProviderList(currentUser));
        }
        return response;
    }

    private SimpleObject personRef(User currentUser) throws Exception {
        PersonResource1_8 personResource = new PersonResource1_8();
        return personResource.asDefaultRep(currentUser.getPerson());
    }

    private SimpleObject userRef(User currentUser) throws Exception {
        UserResource1_8 userResource = new UserResource1_8();
        return userResource.asDefaultRep(userResource.getByUniqueId(currentUser.getUuid()));
    }

    private List<SimpleObject> buildProviderList(User currentUser) throws Exception {
        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(currentUser.getPerson());
        List<SimpleObject> providerList = new ArrayList<SimpleObject>();
        for (Provider provider : providers) {
            providerList.add(providerRef(provider));
        }
        return providerList;
    }

    private SimpleObject providerRef(Provider provider) throws Exception {
        ProviderResource1_9 providerResource = new ProviderResource1_9();
        return providerResource.asDefaultRep(provider);
    }

}
