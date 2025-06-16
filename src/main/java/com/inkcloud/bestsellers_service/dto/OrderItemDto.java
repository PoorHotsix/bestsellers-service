package com.inkcloud.bestsellers_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemDto {

    private Long itemId;

    private String name;

    private int price;

    private int quantity;

    private String author;

    private String publisher;

    private String thumbnailUrl;

}
