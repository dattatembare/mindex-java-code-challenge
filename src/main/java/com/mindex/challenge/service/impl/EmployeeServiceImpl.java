package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Find employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            //RuntimeException(status=500) replaced with ResponseStatusException (status=404)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId");
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructureByEmployeeId(String id) {
        LOG.debug("Get employee reporting structure for id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId");
        }

        Long totalDirectReports = calculateDirectReports(Collections.singletonList(employee), 0);

        return new ReportingStructure(employee, totalDirectReports);
    }

    /**
     * Recursive method to find out total direct reportees to desired employee
     * findAllById- repository method used to find the records for list of employeeIds
     *
     * @param employees          - Employee
     * @param directReportsCount - known direct reportees count
     * @return total reportees count
     */
    private long calculateDirectReports(List<Employee> employees, long directReportsCount) {
        List<Employee> directReports = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getDirectReports() != null) {
                List<String> reportees = employee.getDirectReports().stream()
                        .map(Employee::getEmployeeId)
                        .collect(Collectors.toList());
                //Single call for all 'n' reportees instead of individual call for each employeeId
                List<Employee> edr = new ArrayList<>((List<Employee>) employeeRepository.findAllById(reportees));
                employee.setDirectReports(edr);
                directReports.addAll(edr);
                directReportsCount += edr.size();
            }
        }
        long finalDirectReportsCount = directReportsCount;
        if (directReports.size() > 0) return calculateDirectReports(directReports, finalDirectReportsCount);
        return finalDirectReportsCount;
    }
}
