package com.projects.nexigntest.services;

import com.projects.nexigntest.entities.Subscriber;
import com.projects.nexigntest.repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Сервис для работы с абонентами
 */
@Service
public class SubscriberService {
    @Autowired
    SubscriberRepository subscriberRepository;

    /**
     * Генерируем пользователя и добавляем в БД
     */
    public void generate(){
        long min = 70000000000L;
        long max = 79999999999L;

        for(int i = 0; i<10; i++){
            while (true) {
                String number = String.valueOf(new Random().nextLong(min, max));
                if (subscriberRepository.findByMsisdn(number) == null) {
                    subscriberRepository.save(
                            Subscriber
                                    .builder()
                                    .msisdn(number)
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
}
