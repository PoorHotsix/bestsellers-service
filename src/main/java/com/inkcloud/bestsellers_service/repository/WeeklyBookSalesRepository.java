package com.inkcloud.bestsellers_service.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.inkcloud.bestsellers_service.domain.WeeklyBookSales;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WeeklyBookSalesRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    public void upsert(WeeklyBookSales sales) {
        DynamoDbTable<WeeklyBookSales> table = enhancedClient.table("weekly_book_sales",
                TableSchema.fromBean(WeeklyBookSales.class));
        table.putItem(sales);  // 존재하면 업데이트, 없으면 삽입
    }

    public WeeklyBookSales findByBookIdAndDate(Long bookId, LocalDate date) {
        DynamoDbTable<WeeklyBookSales> table = enhancedClient.table("weekly_book_sales",
                TableSchema.fromBean(WeeklyBookSales.class));
        return table.getItem(r -> r.key(k -> k
            .partitionValue(bookId)
            .sortValue(date.toString())
        ));
    }

    public List<WeeklyBookSales> findBetweenDates(LocalDate from, LocalDate to, List<Long> bookIds) {
        DynamoDbTable<WeeklyBookSales> table = enhancedClient.table("weekly_book_sales",
                TableSchema.fromBean(WeeklyBookSales.class));

        List<WeeklyBookSales> result = new ArrayList<>();

        for (Long bookId : bookIds) {
            Key keyFrom = Key.builder()
                    .partitionValue(bookId)
                    .sortValue(from.toString())
                    .build();
            Key keyTo = Key.builder()
                    .partitionValue(bookId)
                    .sortValue(to.toString())
                    .build();

            QueryConditional condition = QueryConditional.sortBetween(keyFrom, keyTo);

            PageIterable<WeeklyBookSales> pages = table.query(r -> r.queryConditional(condition));

            List<WeeklyBookSales> sales = pages.stream()
                    .flatMap(page -> page.items().stream())
                    .collect(Collectors.toList());

            if (!sales.isEmpty()) {
                log.info("판매 이력 발견 - bookId: {}, 개수: {}", bookId, sales.size());
                result.addAll(sales);
            }
        }

        return result;
    }
}
