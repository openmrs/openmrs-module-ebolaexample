package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.api.db.ContextDAO;

import java.util.Properties;

public class PasswordlessContextDao implements ContextDAO {
    @Override
    public User authenticate(String username, String password) throws ContextAuthenticationException {
        User user = Context.getUserService().getUserByUsername(username);
        if(user == null) {
            throw new ContextAuthenticationException("Username does not exist");
        }
        return user;
    }

    @Override
    public User getUserByUuid(String uuid) throws ContextAuthenticationException {
        return null;
    }

    @Override
    public void openSession() { }

    @Override
    public void closeSession() { }

    @Override
    public void clearSession() { }

    @Override
    public void flushSession() { }

    @Override
    public void evictFromSession(Object obj) { }

    @Override
    public void startup(Properties props) { }

    @Override
    public void shutdown() { }

    @Override
    public void mergeDefaultRuntimeProperties(Properties runtimeProperties) { }
}
