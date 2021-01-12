package com.jm.online_store.enums;

/**
 * Обозначает статус согласия юзера на получение рассылки (об изменении цены).
 * NO_ACTIONS - нет согласия, REQUESTED - согласие запрошено (в письме), CONFIRMED - согласие получено.
 */
public enum ConfirmReceiveEmail {
    NO_ACTIONS,
    REQUESTED,
    CONFIRMED
}