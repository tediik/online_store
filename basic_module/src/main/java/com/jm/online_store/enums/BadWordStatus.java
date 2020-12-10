package com.jm.online_store.enums;

/**
 * ENUM статус для Стоп-слов
 * DEACTIVATED не активное (по нему не фильтруется, даже если слово есть в базе)
 * ACTIVE активно, фильтруется
 */
public enum BadWordStatus {
    DEACTIVATED,
    ACTIVE
}