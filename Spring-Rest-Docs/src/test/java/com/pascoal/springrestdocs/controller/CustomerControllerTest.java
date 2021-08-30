package com.pascoal.springrestdocs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pascoal.springrestdocs.domain.Customer;
import com.pascoal.springrestdocs.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;


@ExtendWith(SpringExtension.class)
@WebMvcTest //telling Spring to only load the web context
@AutoConfigureRestDocs
@Slf4j
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Stream<Customer> customers;


    @BeforeEach
    void setUp() {
        customers = Stream.of(new Customer("xpto", "Pascal", "Bayonne", "eddybayonne@gmail.com", "+351-915-123-321", LocalDate.of(1992, 12, 31)),
                new Customer(null, "Alina", "Yerusalimets", "alyye@gmail.com", "+351-915-123-321", LocalDate.of(1996, 1, 22)),
                new Customer(null, "Henrique", "Armando", "goldberg@outlook.com", "+351-915-123-321", LocalDate.of(2001, 5, 22)));

        objectMapper.canSerialize(LocalDate.class);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Given Customer when POST create then customer created")
    void createOK() throws Exception {
        //GIVEN
        Customer customer = customers.findFirst().orElseThrow();
        //WHEN
        Mockito.when(customerService.create(ArgumentMatchers.any())).thenReturn(customer.getId());
        String jsonRequestBody = objectMapper.writeValueAsString(customer);
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customer")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("X-Customer-ID", customer.getId()))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        responseHeaders(headerWithName("X-Customer-ID").description("The new created customer Id"))));
    }

    @Test
    void findAllOK() throws Exception {
        Mockito.when(customerService.findAll()).thenReturn(customers);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customer")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("{method-name}"))
                //.andExpect(MockMvcResultMatchers.content().json(jsonResponse))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        Assertions.assertFalse(response.getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Given Id when findById then return Customer OK")
    void findByIdOK() throws Exception {
        //GIVEN
        Customer customer = customers.findFirst().orElseThrow();
        //WHEN
        Mockito.when(customerService.findById(ArgumentMatchers.any())).thenReturn(customer);
        String jsonResponse = objectMapper.writeValueAsString(customer);
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customer/xpto")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("xpto")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", Matchers.is("Pascal")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname", Matchers.is("Bayonne")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("eddybayonne@gmail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber", Matchers.is("+351-915-123-321")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate", Matchers.is("1992-12-31")))
                .andExpect(MockMvcResultMatchers.content().json(jsonResponse, true))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").description("The customer unique identifier"),
                                PayloadDocumentation.fieldWithPath("firstname").description("The customer's first name"),
                                PayloadDocumentation.fieldWithPath("lastname").description("The customer's surname"),
                                PayloadDocumentation.fieldWithPath("email").description("The customer email address"),
                                PayloadDocumentation.fieldWithPath("phoneNumber").description("The customer's phone number which must be a PT"),
                                PayloadDocumentation.fieldWithPath("birthDate").description("The customer's birth date"))));
    }

    @Test
    @DisplayName("Given Id and Customer when PUT update then Customer updated Accepted")
    void updateAccepted() throws Exception {
        //GIVEN
        var newCustomer = new Customer();
        newCustomer.setId("xpto");
        newCustomer.setFirstname("Pascoal");
        newCustomer.setLastname("Bayona");
        newCustomer.setEmail("pascoal.bayona@xpance.pt");
        newCustomer.setPhoneNumber("931098345");
        newCustomer.setBirthDate(LocalDate.of(1992,12,31));
        String jsonRequestBody = objectMapper.writeValueAsString(customers.findFirst().orElse(new Customer()));
        //WHEN
        Mockito.when(customerService.update(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(newCustomer);

        //THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customer/xpto")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.header().string("X-Customer-ID", newCustomer.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("xpto")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", Matchers.is("Pascoal")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname", Matchers.is("Bayona")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("pascoal.bayona@xpance.pt")))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").description("The customer unique identifier"),
                                PayloadDocumentation.fieldWithPath("firstname").description("The customer's first name"),
                                PayloadDocumentation.fieldWithPath("lastname").description("The customer's surname"),
                                PayloadDocumentation.fieldWithPath("email").description("The customer email address"),
                                PayloadDocumentation.fieldWithPath("phoneNumber").description("The customer's phone number which must be a PT"),
                                PayloadDocumentation.fieldWithPath("birthDate").description("The customer's birth date"))));
    }

    @Test
    @DisplayName("Given Id when deleteById then delete and NoContent")
    void deleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customer/xpto"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcRestDocumentation.document("{method-name}"));
    }
}