package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CompensationServiceImplTest {
    @Autowired
    private CompensationService compensationService;

    @Mock
    private CompensationRepository compensationRepository;

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

        compensation = new Compensation(employee, 2000L, LocalDate.now());
    }

    @Test
    public void testCreate() {
        List<Compensation> compensations = compensationService.create(Collections.singletonList(compensation));
        assertNotNull(compensations.get(0).getEmployee().getEmployeeId());
        assertEmployeeEquivalence(employee, compensations.get(0).getEmployee());
    }

    @Test
    public void testRead() {
        when(compensationRepository.findByEmployee_EmployeeId(anyString())).thenReturn(compensation);
        Compensation compensationRes = compensationService.read("03aa1462-ffa9-4978-901b-7c001562cf6f");
        assertNotNull(compensationRes.getEmployee().getEmployeeId());
        assertEmployeeEquivalence(employee, compensationRes.getEmployee());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test(expected = ResponseStatusException.class)
    public void testGetReportingStructureByEmployeeIdInvalidEmployeeId() {
        when(compensationRepository.findByEmployee_EmployeeId(anyString())).thenReturn(null);
        compensationService.read("invalid-id");
    }
}
