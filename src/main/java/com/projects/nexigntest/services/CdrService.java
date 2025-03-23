package com.projects.nexigntest.services;

import com.projects.nexigntest.commons.CallType;
import com.projects.nexigntest.controllers.responses.GenerateCdrReportResponse;
import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.entities.Subscriber;
import com.projects.nexigntest.repositories.CdrRepository;
import com.projects.nexigntest.repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

/**
 * Сервис для работы с CDR
 */
@Service
public class CdrService {
    @Autowired
    private CdrRepository cdrRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Value(value = "${cdr-report.path}")
    private String REPORTS_DIR;

    /**
     * Генерируем информацию о действиях, совершенных абонентом за тарифицируемый период.
     */
    public void generate() {
        for (int i = 0; i < 100; i++) {  // Генерируем 100 звонков
            while (true){

                Subscriber caller = subscriberService.getRandomSubscriber();
                Subscriber receiver = subscriberService.getRandomSubscriber();

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
     * Получаем случайный тип звонка (Incoming - входящий, Outcoming - исходящий)
     * @return случайный тип звонка
     */
    public String getRandomCallType() {
        return new Random().nextBoolean() ?
                CallType.INCOMING.value() :
                CallType.OUTGOING.value();
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

    /**
     *
     * @param msisdn
     * @param startDate
     * @param endDate
     * @return
     * @throws IOException
     */
    public GenerateCdrReportResponse generateReport(
            String msisdn,
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException {
        Subscriber subscriber = subscriberRepository.findByMsisdn(msisdn);
        List<Cdr> cdrList = cdrRepository.findByCallerIdAndStartTimeBetween(subscriber.getId(), startDate.atStartOfDay(), endDate.atStartOfDay());

        if(cdrList.isEmpty()){
            return GenerateCdrReportResponse
                    .builder()
                    .msisdn(msisdn)
                    .message("Cdr отсутствует!")
                    .build();
        }


        File directory = new File(REPORTS_DIR);
        if (!directory.exists()) {
            directory.mkdirs();  // Создаем директорию, если она не существует
        }

        String fileName = msisdn + "_" + UUID.randomUUID() + ".txt";
        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Cdr cdr : cdrList) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        cdr.getCallType(),
                        cdr.getCaller().getMsisdn(),
                        cdr.getReceiver().getMsisdn(),
                        cdr.getStartTime(),
                        cdr.getEndTime()
                ));
            }
        }
        return GenerateCdrReportResponse
                .builder()
                .msisdn(msisdn)
                .message("Отчет %s создан!".formatted(fileName))
                .build();
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
        Map<String, LocalDateTime> dateInterval = generateMonthInterval(year, month);

        return cdrRepository
                .findByCallerIdAndStartTimeBetween(
                        subscriberId,
                        dateInterval.get("startOfMonth"),
                        dateInterval.get("endOfMonth")
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

    /**
     * Создает интервал времени для начала и конца определенного месяца.
     * @param year год месяца
     * @param month число месяца
     * @return интервал времени для начала и конца определенного месяца.
     */
    public Map<String, LocalDateTime> generateMonthInterval(
            Integer year,
            Integer month
    ){
        Map<String, LocalDateTime> dateInterval = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        dateInterval.put("startOfMonth", startOfMonth);
        dateInterval.put("endOfMonth", endOfMonth);

        return dateInterval;
    }
}
