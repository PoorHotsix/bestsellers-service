package com.inkcloud.bestsellers_service.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@DynamoDbBean
public class WeeklyBookSales {

    private Long bookId;

    private LocalDate saleDate;

    private int totalSoldQuantity;

    @DynamoDbPartitionKey
    public Long getBookId() {
        return bookId;
    }

    @DynamoDbSortKey
    public LocalDate getSaleDate() {
        return saleDate;
    }

}
