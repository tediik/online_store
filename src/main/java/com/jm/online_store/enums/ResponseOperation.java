package com.jm.online_store.enums;

/**
 * Набор перечислений для использования в работе со строковыми, наиболее частыми ответами
 * эндпоинтов.
 */

public enum ResponseOperation {

    HAS_BEEN_DELETED("%s - has successfully deleted"),
    HAS_BEEN_SAVED("%s - has successfully saved"),
    HAS_BEEN_UPDATED("%s - has successfully updated"),
    // используется как заглушка в ResponseDto для поля error, в случае если тело передает строковый тип
    NO_ERROR(""),
    HAS_NOT_BEEN_DELETED("%s - has not deleted"),
    SUCCESS, FAILED;

    private String message;

    ResponseOperation() {
    }

    ResponseOperation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
