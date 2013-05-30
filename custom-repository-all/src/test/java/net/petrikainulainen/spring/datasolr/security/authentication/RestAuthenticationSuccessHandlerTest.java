package net.petrikainulainen.spring.datasolr.security.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

/**
 * @author Petri Kainulainen
 */
public class RestAuthenticationSuccessHandlerTest {

    private RestAuthenticationSuccessHandler successHandler;

    @Before
    public void setUp() {
        successHandler = new RestAuthenticationSuccessHandler();
    }

    @Test
    public void onAuthenticationSuccess_ShouldSetResponseStatusToOk() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new TestingAuthenticationToken(null, null);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
    }
}
