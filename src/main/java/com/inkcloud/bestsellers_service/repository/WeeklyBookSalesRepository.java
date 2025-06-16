package com.inkcloud.bestsellers_service.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.inkcloud.bestsellers_service.domain.WeeklyBookSales;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

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

    public List<WeeklyBookSales> findBetweenDates(LocalDate from, LocalDate to) {

        DynamoDbTable<WeeklyBookSales> table = enhancedClient.table("weekly_book_sales",
                TableSchema.fromBean(WeeklyBookSales.class));

        List<WeeklyBookSales> result = new ArrayList<>();

        for (long bookId = 1; bookId <= 99999L; bookId++) {
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

            pages.stream()
                    .flatMap(page -> page.items().stream())  // 명시적으로 작성
                    .forEach(result::add);
        }

        return result;
    }
}
