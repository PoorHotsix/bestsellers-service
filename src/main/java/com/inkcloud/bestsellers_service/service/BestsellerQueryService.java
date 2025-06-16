package com.inkcloud.bestsellers_service.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inkcloud.bestsellers_service.domain.BestsellerBook;
import com.inkcloud.bestsellers_service.domain.WeeklyBookSales;
import com.inkcloud.bestsellers_service.dto.BestsellerResponseDto;
import com.inkcloud.bestsellers_service.repository.BestsellerBookRepository;
import com.inkcloud.bestsellers_service.repository.WeeklyBookSalesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class BestsellerQueryService {

    private final WeeklyBookSalesRepository salesRepository;
    private final BestsellerBookRepository bookRepository;

    public List<BestsellerResponseDto> getTopBestsellers(String period) {
        LocalDate today = LocalDate.now();

        LocalDate startDate;
        if ("daily".equalsIgnoreCase(period)) {
            startDate = today.minusDays(1);
        } else if ("weekly".equalsIgnoreCase(period)) {
            startDate = today.minusDays(6);
        } else {
            throw new IllegalArgumentException("지원하지 않는 기간: " + period);
        }

        // 1. 기간 내 모든 판매 이력 조회
        List<WeeklyBookSales> allSales = salesRepository.findBetweenDates(startDate, today.plusDays(1));

        // 2. bookId 기준으로 수량 합산
        Map<Long, Integer> quantityMap = new HashMap<>();
        for (WeeklyBookSales sale : allSales) {
            quantityMap.merge(sale.getBookId(), sale.getTotalSoldQuantity(), Integer::sum);
        }

        // 3. bookId 기준으로 메타 정보 조회 + DTO 변환
        return quantityMap.entrySet().stream()
                .map(entry -> {
                    BestsellerBook book = bookRepository.findByBookId(entry.getKey());
                    if (book == null) return null;

                    return BestsellerResponseDto.builder()
                            .bookId(book.getBookId())
                            .name(book.getName())
                            .author(book.getAuthor())
                            .publisher(book.getPublisher())
                            .imageUrl(book.getImageUrl())
                            .price(book.getPrice())
                            .totalSoldQuantity(entry.getValue())
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(BestsellerResponseDto::getTotalSoldQuantity).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
