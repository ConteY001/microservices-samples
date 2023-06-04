package com.mounahtech.inventoryservice.service;

import com.mounahtech.inventoryservice.dto.InventoryResponse;
import com.mounahtech.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows // Do not do this in the production code
    public List<InventoryResponse> isInstock(List<String> skuCode) {
//        log.info("Wait Started");
//        Thread.sleep(10000);
//        log.info("Wait  Ended");
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();

    }
}
