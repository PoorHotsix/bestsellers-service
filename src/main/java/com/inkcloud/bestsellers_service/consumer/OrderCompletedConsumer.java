package com.inkcloud.bestsellers_service.consumer;

import java.time.LocalDate;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkcloud.bestsellers_service.dto.FromOrderEvent;
import com.inkcloud.bestsellers_service.dto.OrderItemDto;
import com.inkcloud.bestsellers_service.service.BestsellerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCompletedConsumer {

    private final ObjectMapper objectMapper;
    private final BestsellerService bestsellerService;

    @KafkaListener(topics = "order-complete-bestseller", groupId = "bestseller-group")
    public void consume(String eventJson) {
        try {
            FromOrderEvent event = objectMapper.readValue(eventJson, FromOrderEvent.class);
            LocalDate saleDate = LocalDate.now();

            log.info("[Kafka] 주문 이벤트 수신 - 도서 수: {}, 주문일자: {}", 
                event.getDtos().size(), saleDate);

            for (OrderItemDto item : event.getDtos()) {
                bestsellerService.processOrderItem(item, saleDate);
            }

        } catch (Exception e) {
            log.error("Kafka 이벤트 처리 실패", e);
        }
    }
}