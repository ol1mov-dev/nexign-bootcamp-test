package com.projects.nexigntest.services;

import com.projects.nexigntest.commons.CallType;
import com.projects.nexigntest.controllers.responses.UdrResponse;
import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.entities.Subscriber;
import com.projects.nexigntest.repositories.CdrRepository;
import com.projects.nexigntest.repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

/**
 * Сервис для работы с отчетами пользователей об использовании данных
 */
@Service
public class UdrService {
    @Autowired
    private CdrRepository cdrRepository;
    @Autowired
    private SubscriberRepository subscriberRepository;

    /**
     * Получаем UDR пользователя
     * @param msisdn номер пользователя
     * @param month месяц за который нужно предоставить отчет
     * @param year год за который нужно предоставить отчет
     * @return возвращает отчет
     */
    public UdrResponse getUdr(
            String msisdn,
            Integer year,
            Integer month
    ){
        Subscriber subscriber = subscriberRepository.findByPhoneNumber(msisdn);

        if (subscriber != null) {
            Map<String, String> calculatedTimesOfCalls = getCalculatedTimeOfCalls(
                    subscriber.getId(), year, month
            );

            return UdrResponse
                    .builder()
                    .msisdn(msisdn)
                    .incomingCall(
                            UdrResponse.Call
                                    .builder()
                                    .total(calculatedTimesOfCalls.get("outgoingCall"))
                                    .build()
                    )
                    .outcomingCall(
                            UdrResponse.Call
                                    .builder()
                                    .total(calculatedTimesOfCalls.get("incomingCall"))
                                    .build()
                    ).build();
        }

        return null;
    }

    public List<Cdr> getUdrsList(Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return cdrRepository.findByStartTimeBetween(startOfMonth, endOfMonth);
    }

    /**
     * Получить расчеты суммарного количества времени потраченного на входящие и исходящие звонки
     * за определенный промежуток времени
     * @param subscriberId id пользователя
     * @param month месяц за который нужно рассчитать время входящих и исходящих звонков
     * @param year год за который нужно рассчитать время входящих и исходящих звонков
     * @return общее количество потраченного времени за все время
     */
    public Map<String, String> getCalculatedTimeOfCalls(
            Long subscriberId,
            Integer year,
            Integer month
    ){

        List<Cdr> cdrs =
                year == null && month == null ?
                        getCdrsByAllTimePeriod(subscriberId):
                        getCdrsAtMonth(subscriberId, year, month);


        HashMap<String, String> totalTimeOfCalls = new HashMap<>();
        totalTimeOfCalls.put(
                "outgoingCall",
                formatCalculationResult(calculateOutcomingCalls(cdrs))
        );

        totalTimeOfCalls.put(
                "incomingCall",
                formatCalculationResult(calculateIncomingCalls(cdrs))
        );

        return  totalTimeOfCalls;
    }

    /**
     * Расчет входящих звонков
     * @param cdrs список звонков пользователя
     * @return возвращает общее время потраченное на входящие звонки
     */
    public Duration calculateIncomingCalls(List<Cdr> cdrs) {
        return cdrs
                .stream()
                .filter(call -> Objects.equals(call.getCallType().callType, CallType.INCOMING.callType))
                .map(call -> Duration.between(call.getStartTime(), call.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    public Duration calculateOutcomingCalls(List<Cdr> cdrs) {
        return cdrs.stream()
                .filter(call -> Objects.equals(call.getCallType().callType, CallType.OUTGOING.callType))
                .map(call -> Duration.between(call.getStartTime(), call.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Форматируемый полученные расчеты в удобный для пользователя формат
     * @param result - результат расчетов
     * @return удобный для пользователя формат расчета времени.
     */
    public String formatCalculationResult(Duration result) {
        return String.format("%02d:%02d:%02d",
                result.toHours(),
                result.toMinutesPart(),
                result.toSecondsPart());
    }

    /**
     * Получить все Cdr за месяц
     * @param subscriberId идентификатор пользователя
     * @param year год за который нужно получить Cdr
     * @param month месяц за который нужно получить Cdr
     * @return возвращает все Cdr за месяц в определенном году
     */
    public List<Cdr> getCdrsAtMonth(
            Long subscriberId,
            Integer year,
            Integer month
    ){
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return cdrRepository
                .findByCallerIdAndStartTimeBetween(
                        subscriberId,
                        startOfMonth,
                        endOfMonth
                );
    }

    /**
     * Получаем все Cdr пользователя за весь тарифицируемый период
     * @param id идентификатор пользователя
     * @return возвращает все Cdr конктретного пользователя
     */
    public List<Cdr> getCdrsByAllTimePeriod(Long id) {
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        return cdrRepository.findByCallerIdAndStartTimeAfter(id, startDate);
    }
}
