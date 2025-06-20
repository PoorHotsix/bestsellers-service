package com.inkcloud.bestsellers_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inkcloud.bestsellers_service.dto.BestsellerResponseDto;
import com.inkcloud.bestsellers_service.service.BestsellerQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/bestsellers")
@RequiredArgsConstructor
public class BestsellerController {

    private final BestsellerQueryService bestsellerQueryService;

    @GetMapping
    public List<BestsellerResponseDto> getBestsellers(@RequestParam(defaultValue = "daily") String period) {
        log.info("daily request!!!");
        return bestsellerQueryService.getTopBestsellers(period);
    }
}
