package com.pascoal.springrestdocs.service;

import com.pascoal.springrestdocs.domain.Customer;

import java.util.stream.Stream;

public interface CustomerService {
    String create(Customer customer);

    Customer findById(String id);

    Stream<Customer> findAll();

    Customer update(String id, Customer customer);

    void deleteById(String id);
}
