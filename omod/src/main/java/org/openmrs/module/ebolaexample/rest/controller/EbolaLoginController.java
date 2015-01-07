package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ContextDAO;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/login")
public class EbolaLoginController {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestParam(value="username", required=true) String teamUsername,
                       @RequestParam(value="provider", required=true) String provider) throws Exception {
        SimpleObject response = new SimpleObject();
        User authenticatedUser = Context.getUserContext().authenticate(teamUsername, "", new PasswordlessContextDao());

        UserAndPassword1_8 authenticatedUserResource = new UserResource1_8().getByUniqueId(authenticatedUser.getUuid());
        response.add("user", new UserResource1_8().asDefaultRep(authenticatedUserResource));
        Provider providerByIdentifier = Context.getProviderService().getProviderByIdentifier(provider);
        if(providerByIdentifier == null) {
            providerByIdentifier = Context.getProviderService().getUnknownProvider();
        }
        response.add("provider", new ProviderResource1_9().asDefaultRep(providerByIdentifier));
        return response;
    }
}