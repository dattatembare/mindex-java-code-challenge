package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private Employee employee;
    private ReportingStructure reportingStructure;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";

        employee = new Employee(
                "03aa1462-ffa9-4978-901b-7c001562cf6f",
                "Ringo",
                "Starr",
                "Developer V",
                "Engineering",
                null);
        reportingStructure = new ReportingStructure(employee, 2L);
    }

    @Test
    public void testCreateReadUpdate() {
        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(employee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testGetReportingStructureByEmployeeIdNoReportee() {
        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(employee, createdEmployee);

        // Read checks
        employeeIdUrl = employeeIdUrl + "/reporting-structure";
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdUrl, ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, reportingStructure.getEmployee());
        assertEquals(0L, (long) reportingStructure.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureByEmployeeIdMultipleDirectReports() {
        List<Employee> directReports = Arrays.asList(
                new Employee("b7839309-3348-463b-a7e3-5de1c168beb3", null, null, null, null, null),
                new Employee("03aa1462-ffa9-4978-901b-7c001562cf6f", null, null, null, null, null)
        );
        employee = new Employee(
                "16a596ae-edd3-4847-99fe-c4518e82c86f",
                "John",
                "Lennon",
                "Development Manager",
                "Engineering",
                directReports);
        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(employee, createdEmployee);

        // Read checks
        employeeIdUrl = employeeIdUrl + "/reporting-structure";
        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeIdUrl, ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, reportingStructure.getEmployee());
        assertNotNull(reportingStructure.getEmployee().getFirstName());
        assertNotNull(reportingStructure.getEmployee().getLastName());
        assertEquals(2, reportingStructure.getEmployee().getDirectReports().size());
        assertEquals(4L, (long) reportingStructure.getNumberOfReports());
    }

    @Test(expected = ResponseStatusException.class)
    public void testGetReportingStructureByEmployeeIdInvalidEmployeeId() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(null);
        employeeService.getReportingStructureByEmployeeId("invalid-id");
    }
}
