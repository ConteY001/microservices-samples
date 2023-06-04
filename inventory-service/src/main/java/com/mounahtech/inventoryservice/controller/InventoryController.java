package com.mounahtech.inventoryservice.controller;

import com.mounahtech.inventoryservice.dto.InventoryResponse;
import com.mounahtech.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8082/inventory/iphone-13,iphone-13-red
    // Request Parameter: http://localhost:8082/inventory?skuCode=iphone-13&skuCode=iphone-13-red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInstock(skuCode);
    }
}
