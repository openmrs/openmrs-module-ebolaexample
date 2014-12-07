package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WardResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SessionInfoControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Autowired
    private List<DefaultAnnotationHandlerMapping> handlerMappings;

    private WardResource resource;
    private SessionInfoController controller;
    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        controller = new SessionInfoController();
        response = new MockHttpServletResponse();
        requestURI = "ebola/session-info";
    }

    @Test
    public void shouldReturnUUIDsForSession() throws Exception {
        User user = Context.getAuthenticatedUser();
        Provider provider = Context.getProviderService().getProvidersByPerson(user.getPerson()).iterator().next();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");

        response = handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        assertEquals(responseObject.get("userId"), user.getUuid());
        assertEquals(responseObject.get("personId"), user.getPerson().getUuid());

        List<Map> providers = (List<Map>) responseObject.get("providers");
        assertEquals(providers.size(), 1);
        assertEquals(providers.get(0).get("uuid"), provider.getUuid());
    }

    public MockHttpServletResponse handle(HttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HandlerExecutionChain handlerExecutionChain = null;
        for (DefaultAnnotationHandlerMapping handlerMapping : handlerMappings) {
            handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain != null) {
                break;
            }
        }
        Assert.assertNotNull("The request URI does not exist", handlerExecutionChain);

        handlerAdapter.handle(request, response, handlerExecutionChain.getHandler());

        return response;
    }
}

