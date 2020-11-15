package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
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

import java.util.List;

@RestController
@Api("compensation")
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);
    private CompensationService compensationService;

    @Autowired
    public CompensationController(final CompensationService compensationService) {
        this.compensationService = compensationService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee compensation details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee compensation details not found")
    })
    @PostMapping(value = "/compensation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Compensation>> create(@RequestBody final List<Compensation> compensations) {
        LOG.debug("Received compensation create request for [{}]", compensations);

        return new ResponseEntity<>(compensationService.create(compensations), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found the Employee compensation details"),
            @ApiResponse(code = 400, message = "Invalid id supplied"),
            @ApiResponse(code = 404, message = "Employee compensation details not found")
    })
    @GetMapping(value = "/compensation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Compensation> read(@PathVariable final String id) {
        LOG.debug("Received compensation read request for id [{}]", id);
        Compensation compensation = compensationService.read(id);
        if (compensation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId:");
        }

        return new ResponseEntity<>(compensation, HttpStatus.OK);
    }

}
