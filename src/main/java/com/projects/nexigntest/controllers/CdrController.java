package com.projects.nexigntest.controllers;

import com.projects.nexigntest.controllers.responses.GenerateCdrReportResponse;
import com.projects.nexigntest.services.CdrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/cdr")
public class CdrController {

    @Autowired
    private CdrService cdrService;

    /**
     * Создает отчет на основе полученного номера и интервала времени
     * @param msisdn номер пользователя
     * @param startDate начало даты за который нужно показать отчет
     * @param endDate конец даты за который нужно показать отчет
     * @return сообщение о созданном отчете
     * @throws IOException
     */
    @GetMapping("/generate-report/{msisdn}")
    public ResponseEntity<GenerateCdrReportResponse> generateReport(
            @PathVariable String msisdn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) throws IOException {
        return ResponseEntity.ok(cdrService.generateReport(msisdn, startDate, endDate));
    }
}
