package com.inkcloud.bestsellers_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@DynamoDbBean
public class BestsellerBook {

    private Long bookId;

    private String name;

    private String author;

    private String publisher;

    private String imageUrl;
    
    private int price;

    @DynamoDbPartitionKey
    public Long getBookId() {

        return bookId;
    }

}
