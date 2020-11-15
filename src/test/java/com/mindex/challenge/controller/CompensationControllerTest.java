package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CompensationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompensationService compensationService;

    private static final String url = "/compensation";

    private Employee employee;
    private Compensation compensation;

    @Before
    public void setup() {
        employee = new Employee(
                "03aa1462-ffa9-4978-901b-7c001562cf6f",
                "Ringo",
                "Starr",
                "Developer V",
                "Engineering",
                null);
        compensation = new Compensation(employee, 2L, LocalDate.now());
    }

    @Test
    public void testPostCompensationSuccess() throws Exception {
        final String _url = url;
        String reqBodyStr = "[{\"employee\": {\n" +
                "      \"employeeId\" : \"b7839309-3348-463b-a7e3-5de1c168beb3\",\n" +
                "      \"firstName\" : \"Paul\",\n" +
                "      \"lastName\" : \"McCartney\",\n" +
                "      \"position\" : \"Developer I\",\n" +
                "      \"department\" : \"Engineering\"\n" +
                "    },\n" +
                "    \"salary\": 3000,\n" +
                "    \"effectiveDate\": \"01/01/2020\"}]";

        when(compensationService.read(anyString())).thenReturn(compensation);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.post(_url)
                        .content(reqBodyStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testPostCompensationInvalidReqBody() throws Exception {
        final String _url = url;
        when(compensationService.read(anyString())).thenReturn(compensation);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.post(_url)
                        .content("bad reqBodyStr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testGetCompensationSuccess() throws Exception {
        final String _url = url + "/03aa1462-ffa9-4978-901b-7c001562cf6f";

        when(compensationService.read(anyString())).thenReturn(compensation);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get(_url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testGetCompensationInvalidPath() throws Exception {
        final String _url = url + "/invalid-path/03aa1462-ffa9-4978-901b-7c001562cf6f";

        when(compensationService.read(anyString())).thenReturn(compensation);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get(_url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testGetCompensationInvalidEmployeeId() throws Exception {
        final String _url = url + "/invalid-id-1234";

        when(compensationService.read(anyString())).thenReturn(null);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders.get(_url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
