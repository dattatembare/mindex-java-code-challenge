package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Api("employee")
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee details not found")
    })
    @PostMapping("/employee")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return new ResponseEntity<>(employeeService.create(employee), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee details not found")
    })
    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> read(@PathVariable String id) {
        LOG.debug("Received employee create request for id [{}]", id);
        Employee employee = employeeService.read(id);
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId:");
        }

        //return response with HTTP status 200
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee details not found")
    })
    @PutMapping(value = "/employee/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return new ResponseEntity<>(employeeService.update(employee), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee reporting structure details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee reporting structure details not found")
    })
    @GetMapping(value = "/employee/{id}/reporting-structure", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportingStructure> getReportingStructure(@PathVariable String id) {
        LOG.debug("Received employee create request for id [{}]", id);
        ReportingStructure reportingStructure = employeeService.getReportingStructureByEmployeeId(id);
        if (reportingStructure == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId:");
        }

        return new ResponseEntity<>(reportingStructure, HttpStatus.OK);
    }
}
