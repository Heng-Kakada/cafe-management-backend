package com.api.utils;

import org.springframework.http.HttpStatus;

public class ConstantUtils {
    public static final String SC_OK = String.valueOf(HttpStatus.OK.value());
    public static final String SC_BD = String.valueOf(HttpStatus.BAD_REQUEST.value());
    public static final String SC_UA = String.valueOf(HttpStatus.UNAUTHORIZED.value());
    public static final String SC_NF = String.valueOf(HttpStatus.NOT_FOUND.value());
    public static final String ACT = "ACT";
    public static final String BLK = "BLK";
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 1000 * 60 * 2; // 5 min // 24 hours 24 * 60 * 60 * 1000
}
