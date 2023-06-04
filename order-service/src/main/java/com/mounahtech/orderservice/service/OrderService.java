package com.mounahtech.orderservice.service;

import com.mounahtech.orderservice.dto.OrderLineItemsDto;
import com.mounahtech.orderservice.dto.OrderRequest;
import com.mounahtech.orderservice.event.OrderPlacedEvent;
import com.mounahtech.orderservice.model.InventoryResponse;
import com.mounahtech.orderservice.model.Order;
import com.mounahtech.orderservice.model.OrderLineItems;
import com.mounahtech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .build();

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtos()
                .stream()
                .map(this::mapDtoToOrderLine)
                .toList();

        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        log.info("Colling Inventory Service");

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start());
        try (spanInScope) {
            // Call Inventory Service and Place Order if Product is in stock.
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build()
                    .get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInStock);

            if (Boolean.TRUE.equals(allProductsInStock)) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                log.info("Sended Order Number - {}", order.getOrderNumber());
                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in Stock, please try again later.");
            }
        } finally {
            inventoryServiceLookup.end();
        }
    }

    private OrderLineItems mapDtoToOrderLine(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .skuCode(orderLineItemsDto.getSkuCode())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }
}
