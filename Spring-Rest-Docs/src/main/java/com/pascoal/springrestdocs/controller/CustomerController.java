package com.pascoal.springrestdocs.controller;

import com.pascoal.springrestdocs.domain.Customer;
import com.pascoal.springrestdocs.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> create(@RequestBody @Valid Customer customer) {
        String customerID = customerService.create(customer);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Customer-ID", customerID);
        httpHeaders.setLocation(URI.create("/api/v1/customer/" + customerID));

        return ResponseEntity
                .created(httpHeaders.getLocation())
                .headers(httpHeaders)
                .build();
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stream<Customer>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Customer> update(@PathVariable String id, @RequestBody Customer customer) {
        final Customer customerUpdated = customerService.update(id, customer);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Customer-Id", customerUpdated.getId());
        httpHeaders.setLocation(URI.create("/api/v1/customer/" + customerUpdated.getId()));

        return ResponseEntity.accepted()
                .headers(httpHeaders)
                .body(customerUpdated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
