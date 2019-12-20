package com.kien.webserver.request;

public enum Method {
    GET, POST, PUT, DELETE, OPTION;

    public static Method of(String token) {
        try {
            return Method.valueOf(token);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
