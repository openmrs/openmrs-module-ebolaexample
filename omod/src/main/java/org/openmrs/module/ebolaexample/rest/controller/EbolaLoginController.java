package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/login")
public class EbolaLoginController {

    @Autowired
    ProviderService providerService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get() throws Exception {
        if (!Context.isAuthenticated()) {
            SimpleObject response = new SimpleObject();
            response.put("authenticated", false);
            return response;
        }
        else {
            try {
                Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
                Context.addProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
                User authenticatedUser = Context.getAuthenticatedUser();
                return buildResponse(authenticatedUser, null);
            }
            finally {
                Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
                Context.removeProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody Map<String, Object> map) throws Exception {
        SimpleObject response = new SimpleObject();
        try {
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
            User authenticatedUser = Context.getUserContext().authenticate((String) map.get("username"), "", new PasswordlessContextDao());
            Provider provider = buildProvider(map);
            return buildResponse(authenticatedUser, provider);
        }
        catch (ContextAuthenticationException ex) {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
            response.add("error", ex);
            throw ex;
        } catch (Exception e) {
            response.add("error", e);
            // pass
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_PROVIDERS);
        }
        return response;
    }

    private SimpleObject buildResponse(User user, Provider provider) throws Exception {
        SimpleObject response = new SimpleObject();
        UserAndPassword1_8 authenticatedUserResource = new UserResource1_8().getByUniqueId(user.getUuid());
        response.add("user", new UserResource1_8().asDefaultRep(authenticatedUserResource));

        // if provider wasn't specified, we get the one associated with the user account
        if (provider == null) {
            for (Provider candidate : providerService.getProvidersByPerson(user.getPerson(), false)) {
                provider = candidate;
            }
        }
        if (provider == null) {
            provider = getUnknownProvider();
        }

        response.add("provider", new ProviderResource1_9().asDefaultRep(provider));
        response.add("person", new PersonResource1_8().asDefaultRep(user.getPerson()));
        response.add("authenticated", true);
        return response;

    }

    private Provider buildProvider(Map<String, Object> map) {
        String providerId = (String) map.get("provider");
        Provider providerByIdentifier = providerService.getProviderByIdentifier(providerId);
        if(providerByIdentifier == null) {
            providerByIdentifier = getUnknownProvider();
        }
        return providerByIdentifier;
    }

    private Provider getUnknownProvider() {
        Provider providerByIdentifier;
        providerByIdentifier = providerService.getUnknownProvider();

        // Remove the following once EMR API https://issues.openmrs.org/browse/EA-56.
        // We should just be able to use Context.getProviderService().getUnknownProvider() above.
        if(providerByIdentifier == null) {
            String unknownProviderUuid = Context.getAdministrationService().getGlobalProperty(EmrApiConstants.GP_UNKNOWN_PROVIDER);
            providerByIdentifier = providerService.getProviderByUuid(unknownProviderUuid);
        }
        return providerByIdentifier;
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

