package de.helmholtz.marketplace.cerebrum.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CerebrumExceptionHandlerTest
{
    private static final String API_URI_PREFIX = "/api/v0";
    private static final String ORG_API_URI = API_URI_PREFIX + "/organizations";
    @Value("${cerebrum.test.oauth2-token}") private String TOKEN;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    public void whenTry_thenOK() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(API_URI_PREFIX))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(response.getResponse().getContentAsString());
    }

    // handleMethodArgumentTypeMismatch
    @Test
    public void whenMethodArgumentMismatch_thenBadRequest() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(ORG_API_URI + "?page=ccc "))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("should be of type java.lang.Integer"));
    }

    // handleEntityNotFound
    @Test
    public void whenEntityNotFound_thenNotFound() throws Exception
    {
        String nonExistingUuid = "org-5189a7bc-d630-11ea-87d0-0242ac130003";
        final MvcResult response = mockMvc.perform(
                get(ORG_API_URI + "/" + nonExistingUuid))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getMessage()
                .contains("Could not find organization " + nonExistingUuid));
    }

    // handleMethodArgumentNotValid
    @Test
    public void whenValidInput_thenBadRequest() throws Exception
    {
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Forschungszentrum Jülich");
        organisation.put("abbr", "FZJ");

        final MvcResult response = mockMvc.perform(post(ORG_API_URI)
                .contentType("application/json")
                .header("Authorization", "Bearer " + TOKEN)
                .content(objectMapper.writeValueAsString(organisation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertTrue(error.getErrors().get(0).contains("must not be null"));
        assertTrue(error.getMessage().contains("Validation failed"));
    }

    //handleHttpRequestMethodNotSupported
    @Test
    public void whenHttpRequestMethodNotSupported_thenMethodNotAllowed() throws Exception
    {
        final MvcResult response = mockMvc.perform(delete(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0)
                .contains("DELETE method is not supported for this request"));
    }

    // handleNoHandlerFoundException
    @Test
    public void whenNoHandlerForHttpRequest_thenNotFound() throws Exception
    {
        final MvcResult response = mockMvc.perform(delete(API_URI_PREFIX + "/xx")
                .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("No handler found"));
        System.out.println(response.getResponse().getContentAsString());
    }

    // handleHttpMediaTypeNotSupported
    @Test
    public void whenSendInvalidHttpMediaType_thenUnsupportedMediaType() throws Exception
    {
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Forschungszentrum Jülich");
        organisation.put("abbr", "FZJ");

        final MvcResult response = mockMvc.perform(post(ORG_API_URI)
                .contentType("application/xml")
                .header("Authorization", "Bearer " + TOKEN)
                .content(objectMapper.writeValueAsString(organisation)))
                .andExpect(status().isUnsupportedMediaType())
                .andDo(print())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("media type is not supported"));
    }

    // handleHttpMediaTypeNotAcceptable
    @Test
    public void whenSendInvalidHttpMediaType_thenNotAcceptableMediaType() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(ORG_API_URI)
                .accept("application/xml"))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andReturn();
        assertEquals(406, response.getResponse().getStatus());
    }
}
