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

        // 도서 메타 테이블에서 bookId 목록 조회
        List<Long> soldBookIds = bookRepository.findAllBookIds();
        if (soldBookIds.isEmpty()) {
            log.warn("판매된 도서 ID 목록이 비어있습니다.");
            return List.of();
        }

        // 해당 bookId들에 대한 판매 이력 조회
        List<WeeklyBookSales> allSales = salesRepository.findBetweenDates(startDate, today.plusDays(1), soldBookIds);

        // 수량 집계
        Map<Long, Integer> quantityMap = new HashMap<>();
        for (WeeklyBookSales sale : allSales) {
            quantityMap.merge(sale.getBookId(), sale.getTotalSoldQuantity(), Integer::sum);
            log.info("판매 이력 - bookId: {}, 수량: {}", sale.getBookId(), sale.getTotalSoldQuantity());
        }

        return quantityMap.entrySet().stream()
                .map(entry -> {
                    BestsellerBook book = bookRepository.findByBookId(entry.getKey());
                    if (book == null) {
                        log.warn("도서 정보 없음 - bookId: {}", entry.getKey());
                        return null;
                    }

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
