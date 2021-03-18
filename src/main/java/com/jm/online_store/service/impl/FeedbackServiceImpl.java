package com.jm.online_store.service.impl;

import com.jm.online_store.exception.FeedbackNotFoundException;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.repository.FeedbackRepository;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    /**
     * Method saves feedback from current authenticated user
     * with current time {@link LocalDateTime} trimmed to seconds and default status
     *  for new feedbacks TO_DO
     * @param feedback - {@link Feedback} to save
     * @return saved feedback {@link Feedback}
     */
    @Override
    public Feedback addFeedbackFromDto(Feedback feedback) {
        feedback.setUser(userService.getCurrentLoggedInUser());
        feedback.setFeedbackPostDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        feedback.setStatus(Feedback.Status.IN_PROGRESS);
        return feedbackRepository.save(feedback);
    }

    /**
     * Метод добавления ответа на обращения,плюс еще мы сохраняем юзера-менеджера, задает статус RESOLVED
     * @param feedback - {@link Feedback} обращение
     * @return feedback - {@link Feedback} добавленное обращение
     */
    @Override
    public Feedback addAnswerFeedback(Feedback feedback) {
        Feedback feedbackDB = feedbackRepository.findById(feedback.getId()).orElseThrow(FeedbackNotFoundException::new);
        feedbackDB.setManagerId(feedback.getManagerId());
        feedbackDB.setAnswer(feedback.getAnswer());
        feedbackDB.setStatus(Feedback.Status.RESOLVED);
        feedbackDB.setResponseExpected(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return feedbackRepository.save(feedbackDB);
    }

    /**
     * Метод устанавливает дату и время обращения, для поля responseExpected и переводит в статус LATER
     * @param feedback - {@link Feedback} обращение
     * @return feedback - {@link Feedback} обновленное обращение
     */
    @Override
    public Feedback updateTimeAnswerFeedback(Feedback feedback) {
        Feedback feedbackDB = feedbackRepository.findById(feedback.getId()).orElseThrow(FeedbackNotFoundException::new);
        feedbackDB.setResponseExpected(feedback.getResponseExpected());
        feedbackDB.setStatus(Feedback.Status.LATER);
        return feedbackRepository.save(feedbackDB);
    }

    /**
     * Метод возвращает обращение в работу
     * @param id - идентификатор обращения
     */
    @Override
    public Feedback returnInWork(Long id) {
        Feedback feedbackDB = feedbackRepository.findById(id).orElseThrow(FeedbackNotFoundException::new);
        feedbackDB.setStatus(Feedback.Status.IN_PROGRESS);
        feedbackDB.setResponseExpected(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return feedbackRepository.save(feedbackDB);
    }

    /**
     * Метод возвращает обращение по {@param id}
     * @param id - идентификатор обращения
     * @return - {@link Feedback} обращение
     */
    @Override
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElseThrow(FeedbackNotFoundException::new);
    }

    /**
     * Метод возвращает обращения текущего пользователя
     * @return - {@link Set<Feedback>} сет обращений
     */
    @Override
    public Set<Feedback> getAllFeedbackCurrentCustomer() {
        return userService.getCurrentLoggedInUser().getFeedbacks();
    }

    /**
     * Метод возвращает все обращения, сортирует их по дате
     * @return - {@link List<Feedback>} лист обращений
     */
    @Override
    public List<Feedback> getAllFeedback() {
       List<Feedback> feedbackList = feedbackRepository.findAll();
       feedbackList.sort((x,y) -> y.getFeedbackPostDate().compareTo(x.getFeedbackPostDate()));
       return feedbackList;
    }

    /**
     * Метод возвращает все обращения в статуте IN_PROGRESS, сортирует их по дате
     * @return - {@link List<Feedback>} лист обращений
     */
    @Override
    public List<Feedback> getInProgressFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findByStatus(Feedback.Status.IN_PROGRESS);
        feedbackList.sort((x,y) -> y.getFeedbackPostDate().compareTo(x.getFeedbackPostDate()));
        return feedbackList;
    }

    /**
     * Метод возвращает все обращения в статуте LATER, сортирует их по дате.
     * Проверяет время и дату до которого обращение было отложено, и если время и дата
     * меньше текущей, отправляет обращение в работу
     * @return - {@link List<Feedback>} лист обращений
     */
    @Override
    public List<Feedback> getLaterFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findByStatus(Feedback.Status.LATER);
        feedbackList.sort((x,y) -> y.getFeedbackPostDate().compareTo(x.getFeedbackPostDate()));
        for (Feedback feedback : feedbackList) {
            if (feedback.getResponseExpected().isBefore(LocalDateTime.now())) {
                feedback.setStatus(Feedback.Status.IN_PROGRESS);
            }
        }
        return feedbackList;
    }

    /**
     * Метод возвращает все обращения в статуте RESOLVED, сортирует их по дате
     * @return - {@link List<Feedback>} лист обращений
     */
    @Override
    public List<Feedback> getResolvedFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findByStatus(Feedback.Status.RESOLVED);
        feedbackList.sort((x,y) -> y.getFeedbackPostDate().compareTo(x.getFeedbackPostDate()));
        return feedbackList;
    }

    /**
     * Метод по идентификатору находит обращение, смотрит значение поля responseExpected,
     * если оно null, возвращаем текущее время, обрезанное до минут, иначе возвращаем
     * значение поля.
     * @param id - идентификатор обращения
     * @return - {@link LocalDateTime} Время до которого обращение переводится в статус LATER
     */
    @Override
    public LocalDateTime getDateTimeFeedback(Long id) {
        Feedback feedback = getFeedbackById(id);
        LocalDateTime localDateTime = feedback.getResponseExpected();
        if (localDateTime == null) {
            return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        }
        return localDateTime;
    }

    /**
     * Метод удаляет обращение
     * @param id - идентификатор обращения
     */
    @Override
    public void deleteFeedbackById(Long id) {
        feedbackRepository.deleteById(id);
    }
}