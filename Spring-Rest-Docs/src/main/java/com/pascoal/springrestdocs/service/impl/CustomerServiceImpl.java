package com.pascoal.springrestdocs.service.impl;

import com.pascoal.springrestdocs.domain.Customer;
import com.pascoal.springrestdocs.repository.CustomerRepository;
import com.pascoal.springrestdocs.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public String create(Customer customer) {
        customer.setId(UUID.randomUUID().toString());
        customerRepository.save(customer);
        return customer.getId();
    }

    @Override
    public Customer findById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Customer with id %s does not exists", id)));
    }

    @Override
    public Stream<Customer> findAll() {
        return customerRepository.findAll()
                .stream();
    }

    @Override
    public Customer update(final String id, final Customer customer) {
        Customer existingCustomer = findById(id);
        customer.setId(existingCustomer.getId());
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public void deleteById(String id) {
        customerRepository.deleteById(findById(id).getId());
    }
}
