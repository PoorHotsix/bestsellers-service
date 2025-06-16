package com.inkcloud.bestsellers_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BestsellerResponseDto {

    private Long bookId;

    private String name;

    private String author;

    private String publisher;

    private String imageUrl;

    private int price;

    private int totalSoldQuantity;

}
