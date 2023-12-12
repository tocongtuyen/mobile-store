package com.r2s.mobilestore.exception;

import java.util.Map;

public class InvalidExcelVersionException extends ExceptionCustom {
    public InvalidExcelVersionException(Map<String, Object> errors) {
        super("INVALID VERSION OF EXCEL FILE", errors);
    }
}
