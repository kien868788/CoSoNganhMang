package com.kien.webserver.response;

public enum Status {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal kien.webserver.server error"),
    METHOD_NOT_ALLOW(405, "Message Not Allowed");

    int code;
    String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
