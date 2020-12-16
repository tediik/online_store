package com.jm.online_store.service.interf;

/**
 * Интерфейс для записи в шедулер метода
 * который переодически ходит по базе и удаляет
 * Кастомеров у которых 30 дней на восстановление профиля истекло.
 */
public interface DeleteExpiredProfileTask extends Runnable{
    void run();
}
