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
    @Autowired
    private CdrService cdrService;

    /**
     * Получаем UDR пользователя
     * @param msisdn номер пользователя
     * @param month месяц за который нужно предоставить отчет
     * @param year год за который нужно предоставить отчет
     * @return возвращает отчет
     */
    public UdrResponse getUdrByMsisdn(
            String msisdn,
            Integer year,
            Integer month
    ){
        Subscriber subscriber = subscriberRepository.findByMsisdn(msisdn);

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

    /**
     * Получить отчеты всех пользователей за запрошенный месяц
     * @param year год запрошенного месяца
     * @param month месяц за который нужно получить отчет
     * @return список отчетов за месяц
     */
    public List<UdrResponse> getUdrsList(Integer year, Integer month) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        List<UdrResponse> udrsList = new ArrayList<>();

        for (Subscriber subscriber : subscribers) {
            udrsList.add(
                    getUdrByMsisdn(subscriber.getMsisdn(), year, month)
            );
        }

        return udrsList;
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
                        cdrService.getCdrsByAllTimePeriod(subscriberId):
                        cdrService.getCdrsAtMonth(subscriberId, year, month);


        HashMap<String, String> totalTimeOfCalls = new HashMap<>();
        totalTimeOfCalls.put(
                "outgoingCall",
                formatCalculationResult(calculateOutgoingCalls(cdrs))
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
                .filter(call -> Objects.equals(call.getCallType(), CallType.INCOMING.value()))
                .map(call -> Duration.between(call.getStartTime(), call.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Расчет исходящих звонков
     * @param cdrs список звонков пользователя
     * @return возвращает общее время потраченное на исходящие звонки
     */
    public Duration calculateOutgoingCalls(List<Cdr> cdrs) {
        return cdrs.stream()
                .filter(call -> Objects.equals(call.getCallType(), CallType.OUTGOING.value()))
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
}
