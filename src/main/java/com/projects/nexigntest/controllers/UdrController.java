package com.projects.nexigntest.controllers;

import com.projects.nexigntest.controllers.responses.UdrResponse;
import com.projects.nexigntest.services.UdrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с отчетами пользователей об использовании данных
 */
@RestController
@RequestMapping("/udr")
public class UdrController {
    @Autowired
    private UdrService udrService;

    /**
     * Получаем UDR пользователя на основе его номера (MSISDN)
     * @param msisdn номер пользователя
     * @param month месяц за который нужно предоставить отчет
     * @param year год за который нужно предоставить отчет
     * @return Если передаем год и месяц, то возвращает UDR за этот месяц.
     *         В противном случае за весь запрошенный месяц.
     */
    @GetMapping("/{msisdn}")
    public ResponseEntity<UdrResponse> getUdrByMsisdn(
            @PathVariable String msisdn,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ){
        return ResponseEntity.ok(udrService.getUdrByMsisdn(msisdn, year, month));
    }

    /**
     * Возвращает UDR записи по всем нашим абонентам за запрошенный месяц
     * @param month месяц за который нужно предоставить отчет
     * @param year год за который нужно предоставить отчет
     * @return UDR записи по всем нашим абонентам за запрошенный месяц
     */
    @GetMapping("/list")
    public ResponseEntity<List<UdrResponse>> getUdrsList(
            @RequestParam Integer year,
            @RequestParam Integer month
    ){
        return ResponseEntity.ok(udrService.getUdrsList(year, month));
    }
}
