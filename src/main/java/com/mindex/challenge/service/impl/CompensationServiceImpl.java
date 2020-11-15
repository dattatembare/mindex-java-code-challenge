/*
 * ### Task 2
 * Create a new type, Compensation. A Compensation has the following fields: employee, salary, and effectiveDate. Create
 * two new Compensation REST endpoints. One to create and one to read by employeeId. These should persist and query the
 * Compensation from the persistence layer.
 *
 * Alternate Solution -
 * Create a new type, Compensation. A Compensation will have the following fields: employeeId, salary, effectiveDate and employee(transient)
 * Compensation will be mapped with Employee using employeeId. Here employee(transient) will be used in response to send complete response.
 */
package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);
    private CompensationRepository compensationRepository;

    @Autowired
    public CompensationServiceImpl(final CompensationRepository compensationRepository) {
        this.compensationRepository = compensationRepository;
    }

    @Override
    public List<Compensation> create(List<Compensation> compensations) {
        LOG.debug("Creating compensation [{}]", compensations);
        compensationRepository.insert(compensations);
        return compensations;
    }

    @Override
    public Compensation read(String id) {
        LOG.debug("Find compensation with employee id [{}]", id);
        Compensation compensation = compensationRepository.findByEmployee_EmployeeId(id);
        if (compensation == null) {
            //RuntimeException(status=500) replaced with ResponseStatusException (status=404)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid employeeId");
        }
        return compensation;
    }
}
