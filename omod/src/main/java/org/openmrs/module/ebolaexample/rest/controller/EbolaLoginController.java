package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.api.db.ContextDAO;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/login")
public class EbolaLoginController {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody Map<String, Object> map) throws Exception {
        SimpleObject response = new SimpleObject();
        try {
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
            User authenticatedUser = Context.getUserContext().authenticate((String) map.get("username"), "", new PasswordlessContextDao());

            UserAndPassword1_8 authenticatedUserResource = new UserResource1_8().getByUniqueId(authenticatedUser.getUuid());
            response.add("user", new UserResource1_8().asDefaultRep(authenticatedUserResource));
            Provider providerByIdentifier = Context.getProviderService().getProviderByIdentifier((String) map.get("provider"));
            if(providerByIdentifier == null) {
                providerByIdentifier = Context.getProviderService().getUnknownProvider();
            }
            response.add("provider", new ProviderResource1_9().asDefaultRep(providerByIdentifier));
        }
        catch (ContextAuthenticationException ex) {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
            throw ex;
        } catch (Exception e) {
            // pass
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
        }
        return response;
    }

    public class LoginInfo {
        private String username;
        private String provider;

        public String getProvider() { return provider; }

        public void setProvider(String provider) { this.provider = provider; }


        public String getUsername() { return username; }

        public void setUsername(String username) { this.username = username; }
    }
}

