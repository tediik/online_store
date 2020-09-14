package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.SchedulingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

/**
 * Класс планировщик заданий. В соответствии с заданным планом выполняет определенные действия(задания).
 */
@AllArgsConstructor
@Slf4j
@Service
public class SchedulingServiceImpl implements SchedulingService {

    private final MailSenderService mailSenderService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    private final String EMAIL_TYPE = "Stock sender";

    private final TaskScheduler scheduler;

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public void addTaskToScheduler(long id, Runnable task){
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, new CronTrigger("0 0 0 * * ?", TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(id, scheduledTask);
    }

    public void removeTaskFromScheduler(Long id){
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if(scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(id, null);
        }
    }

    @EventListener({ ContextRefreshedEvent.class })
    public void contextRefreshedEvent() {
        // Get all tasks from DB and reschedule them in case of context restarted
    }
    /**
     * Метод выполняется с регулярностью заданным параметром ${emailStockSending.delay} в файле application.yml
     * берем текущий день, делаем выборку среди всех {@link User} у кого в поле DayOfWeekForStockSend
     * текущий день недели отправляем заранее сформированный email из текущих и будущих акций.
     * Акции отбирабтся следующим условием:
     * - Задается период +- 7 дней от текущего.
     * - день старта акции должен попадать в этот промежуток и день окончания акции должен быть после текущего дня.
     */
    @Override
    @Scheduled(cron = "${emailStockSending.delay}")
    public void sendStocksToCustomers() {
        User.DayOfWeekForStockSend dayOfWeek = User.DayOfWeekForStockSend.valueOf(LocalDate.now().getDayOfWeek().toString());
        List<User> usersToSendStock = userRepository.findByDayOfWeekForStockSend(dayOfWeek);
        List<Stock> currentAndFutureStocks = stockRepository
                .findAllByStartDateBetweenAndEndDateIsAfter(LocalDate.now().minusDays(7L), LocalDate.now().plusDays(7L), LocalDate.now());

        String messageBody = prepareMessageBody(currentAndFutureStocks);
        String messageSubject = "Внимание Акции!!!";

        if (usersToSendStock.size() != 0) {
            for (User user : usersToSendStock) {

                mailSenderService.send(user.getEmail(), messageSubject, messageBody, EMAIL_TYPE);
                log.debug("Stock message was sent to {} with email {}", user, user.getEmail());
            }
            log.debug("{} stock emails were sent", usersToSendStock.size());
        } else {
            log.debug("There are no users in db with DayOfWeekForStockSend field equals {}", dayOfWeek);
        }
    }

    /**
     * Метод создает messageBody для рассылки акций
     *
     * @param currentAndFutureStocks лист актуальных акций
     * @return Строка с текстом.
     */
    private String prepareMessageBody(List<Stock> currentAndFutureStocks) {
        StringBuilder messageForEmail = new StringBuilder();
        for (Stock stock : currentAndFutureStocks) {
            messageForEmail
                    .append(stock.getStockTitle())
                    .append("\n")
                    .append(stock.getStockText())
                    .append("\n")
                    .append("\n")
                    .append("Акция проходит с: ")
                    .append(stock.getStartDate())
                    .append(" по: ")
                    .append(stock.getEndDate())
                    .append("\n")
                    .append("\n");
        }
        return messageForEmail.toString();
    }
}
