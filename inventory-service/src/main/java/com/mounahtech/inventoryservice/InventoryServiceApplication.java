package com.mounahtech.inventoryservice;

import com.mounahtech.inventoryservice.model.Inventory;
import com.mounahtech.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory = Inventory.builder()
                    .skuCode("Iphone_13")
                    .quantity(100)
                    .build();
            inventoryRepository.save(inventory);

            Inventory inventory1 = Inventory.builder()
                    .skuCode("Iphone_13_red")
                    .quantity(0)
                    .build();
            inventoryRepository.save(inventory1);
        };
    }
}
