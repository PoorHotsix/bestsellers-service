package com.inkcloud.bestsellers_service.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.inkcloud.bestsellers_service.domain.BestsellerBook;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
@RequiredArgsConstructor
public class BestsellerBookRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    public void insertIfAbsent(BestsellerBook book) {
        DynamoDbTable<BestsellerBook> table = enhancedClient.table("bestsellers",
                TableSchema.fromBean(BestsellerBook.class));

        BestsellerBook existing = table.getItem(Key.builder().partitionValue(book.getBookId()).build());
        if (existing == null) {
            table.putItem(book);
        }
    }

    public BestsellerBook findByBookId(Long bookId) {
        DynamoDbTable<BestsellerBook> table = enhancedClient.table("bestsellers",
                TableSchema.fromBean(BestsellerBook.class));
        return table.getItem(Key.builder()
                .partitionValue(bookId)
                .build());
    }

    public List<Long> findAllBookIds() {
        DynamoDbTable<BestsellerBook> table = enhancedClient.table("bestsellers",
                TableSchema.fromBean(BestsellerBook.class));

        return table.scan().items().stream()
                .map(BestsellerBook::getBookId)
                .collect(Collectors.toList());
    }
}
