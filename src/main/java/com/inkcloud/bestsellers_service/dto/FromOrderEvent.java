package com.inkcloud.bestsellers_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FromOrderEvent {

    private List<OrderItemDto> dtos;
    
    private LocalDateTime orderedAt;

}
