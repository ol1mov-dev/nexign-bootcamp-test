package com.projects.nexigntest.controllers;

import com.projects.nexigntest.controllers.responses.UdrResponse;
import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.services.UdrService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @return возвращает отчет
     */
    @GetMapping("/{msisdn}")
    public UdrResponse getUdr(
            @PathVariable String msisdn,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ){
        return udrService.getUdr(msisdn, year, month);
    }

    @GetMapping("/list")
    public List<Cdr> getUdrList(
            @RequestParam Integer year,
            @RequestParam Integer month
    ){
        return udrService.getUdrsList(year, month);
    }
}
