package com.inkcloud.bestsellers_service.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.inkcloud.bestsellers_service.domain.BestsellerBook;
import com.inkcloud.bestsellers_service.domain.WeeklyBookSales;
import com.inkcloud.bestsellers_service.dto.OrderItemDto;
import com.inkcloud.bestsellers_service.repository.BestsellerBookRepository;
import com.inkcloud.bestsellers_service.repository.WeeklyBookSalesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestsellerService {

    private final WeeklyBookSalesRepository salesRepository;
    private final BestsellerBookRepository bookRepository;

    public void processOrderItem(OrderItemDto item, LocalDate saleDate) {
        Long bookId = item.getItemId();

        // 1. 판매량 누적
        WeeklyBookSales existing = salesRepository.findByBookIdAndDate(bookId, saleDate);
        if (existing != null) {
            existing.setTotalSoldQuantity(existing.getTotalSoldQuantity() + item.getQuantity());
        } else {
            existing = WeeklyBookSales.builder()
                    .bookId(bookId)
                    .saleDate(saleDate)
                    .totalSoldQuantity(item.getQuantity())
                    .build();
        }
        salesRepository.upsert(existing);
        log.info("판매량 저장 완료 - 도서ID: {}, 수량: {}", bookId, existing.getTotalSoldQuantity());

        // 2. 책 메타 정보 저장 (없으면)
        BestsellerBook book = BestsellerBook.builder()
                .bookId(bookId)
                .name(item.getName())
                .author(item.getAuthor())
                .publisher(item.getPublisher())
                .imageUrl(item.getThumbnailUrl())
                .price(item.getPrice())
                .build();

        bookRepository.insertIfAbsent(book);
    }
}
