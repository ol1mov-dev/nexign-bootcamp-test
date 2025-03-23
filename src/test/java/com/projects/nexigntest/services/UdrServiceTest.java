package com.projects.nexigntest.services;

import com.projects.nexigntest.commons.CallType;
import com.projects.nexigntest.controllers.responses.UdrResponse;
import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.entities.Subscriber;
import com.projects.nexigntest.repositories.CdrRepository;
import com.projects.nexigntest.repositories.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UdrServiceTest {

    @InjectMocks
    private UdrService udrService;

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private CdrRepository cdrRepository;

    @Mock
    private CdrRepository udrRepository;

    @Mock
    private CdrService cdr;
    @Test
    void testGetUdr_ReturnsValidUdrResponse() {
        var caller = Subscriber.builder()
                .id(1L)
                .msisdn("+79999999999")
                .build();

        var receiver = Subscriber.builder()
                .id(2L)
                .msisdn("+79999999998")
                .build();

        var cdrList = List.of(
                Cdr.builder()
                        .id(1L)
                        .caller(caller)
                        .receiver(receiver)
                        .callType(CallType.OUTGOING.value())
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusMinutes(10))
                        .build()
        );

        // Мокаем вызов репозитория для поиска абонента
        doReturn(caller).when(subscriberRepository).findByMsisdn("+79999999999");

        // Мокаем вызов репозитория для поиска звонков
        doReturn(cdrList)
                .when(cdrRepository)
                .findByCallerIdAndStartTimeBetween(
                        eq(caller.getId()),       // Используем eq для id
                        any(LocalDateTime.class), // Используем any для времени
                        any(LocalDateTime.class)  // Используем any для времени
                );

        // Вызов метода
        UdrResponse response = udrService.getUdrByMsisdn("+79999999999", 2024, 3);

        // Проверка результата
        assertNotNull(response);
        assertEquals("+79999999999", response.msisdn());
        assertEquals("00:10:00", response.incomingCall().total());
    }


    @Test
    void testGetUdrsList_ReturnsValidUdrListResponse() {

        // Arrange
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("1234567890");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("0987654321");

        when(subscriberRepository.findAll()).thenReturn(Arrays.asList(subscriber1, subscriber2));
        when(udrService.getUdrByMsisdn(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(UdrResponse.builder().msisdn(subscriber1.getMsisdn()).build());

        // Act
        List<UdrResponse> result = udrService.getUdrsList(2025, 3);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}