package com.jm.online_store.exception.constants;

/**
 * Набор констант для обработки исключений.
 * Многократно используемые строковые пояснения причин
 * возникновения исключений. Передаются в конструктор
 * исключений и используются в комбинации с ExceptionEnums
 */
public class ExceptionConstants {
    public final static String  NOT_FOUND = "NOT FOUND";
    public final static String  ALREADY_EXISTS = "%s ALREADY EXISTS";
    public final static String  WITH_SUCH_ID_NOT_FOUND = "WITH SUCH ID: %s IS NOT FOUND";
    public final static String  ALREADY_UNARCHIVED = "IS ALREADY UNARCHIVED";
    public final static String  ALREADY_ARCHIVED = "IS ALREADY ARCHIVED";
    public final static String  NOT_VALID = "%s NOT VALID";
    public final static String  NOT_AUTHENTICATED = "NOT AUTHENTICATED, PLEASE LOGG IN";
    public final static String  IS_EMPTY = "IS EMPTY";

}
