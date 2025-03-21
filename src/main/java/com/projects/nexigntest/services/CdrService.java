package com.projects.nexigntest.services;

import com.projects.nexigntest.commons.CallType;
import com.projects.nexigntest.commons.DatePatterns;
import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.entities.Subscriber;
import com.projects.nexigntest.exceptions.RecordNotFoundException;
import com.projects.nexigntest.repositories.CdrRepository;
import com.projects.nexigntest.repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Сервис для работы с CDR
 */
@Service
public class CdrService {
    @Autowired
    private CdrRepository cdrRepository;
    @Autowired
    private SubscriberRepository subscriberRepository;

    /**
     * Генерируем информацию о действиях, совершенных абонентом за тарифицируемый период.
     */
    public void generate() {
        for (int i = 0; i < 100; i++) {  // Генерируем 100 звонков
            while (true){

                Subscriber caller = getRandomSubscriber();
                Subscriber receiver = getRandomSubscriber();

                if(!Objects.equals(caller.getId(), receiver.getId())){
                    Map<String, LocalDateTime> dates = getRandomDates();
                    cdrRepository.save(
                            Cdr.builder()
                                    .callType(getRandomCallType())
                                    .caller(caller)
                                    .receiver(receiver)
                                    .startTime(dates.get("start"))
                                    .endTime(dates.get("end"))
                                    .build()
                    );
                    break;
                }
            }
        }
    }

    /**
     * Получаем случайного пользователя
     * @return случайный пользователь
     */
    public Subscriber getRandomSubscriber() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        if (subscribers.isEmpty()) {
            throw new RuntimeException("CDR-записи не найдены!");
        }

        return subscribers.get(
                new Random().nextInt(subscribers.size())
        );
    }

    /**
     * Получаем случайный тип звонка (Incoming - входящий, Outcoming - исходящий)
     * @return случайный тип звонка
     */
    public CallType getRandomCallType() {
        return new Random().nextBoolean() ?
                CallType.INCOMING :
                CallType.OUTGOING;
    }

    /**
     * Получаем случайную дату операции в течении одного года
     * @return случайная дата операции в течение года
     */
    public Map<String, LocalDateTime> getRandomDates() {
        Map<String, LocalDateTime> dateTimes = new HashMap<>();

        Random random = new Random();
        LocalDateTime startTime = LocalDateTime.now()
                                .minusDays(random.nextInt(365));

        LocalDateTime endTime = startTime
                                .plusMinutes(random.nextInt(60))
                                .plusSeconds(random.nextInt(60));

        dateTimes.put("start", startTime);
        dateTimes.put("end", endTime);
        return dateTimes;
    }
}
