package com.mounahtech.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mounahtech.productservice.dto.ProductRequest;
import com.mounahtech.productservice.model.Product;
import com.mounahtech.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();

        String productRequestContent = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestContent))
                .andExpect(status().isCreated());

        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product product = Product.builder()
                .id("643e603f60b551323509092a")
                .name("Iphone 14")
                .description("Iphone 14")
                .price(BigDecimal.valueOf(1800))
                .build();

        List<Product> products = new ArrayList<>(Collections.singletonList(product));

        String contentOfAllProducts = objectMapper.writeValueAsString(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentOfAllProducts))
                .andExpect(status().isOk());

        assertEquals(1, products.size());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("Iphone 13")
                .description("Iphone 13")
                .price(BigDecimal.valueOf(1200))
                .build();
    }
}
