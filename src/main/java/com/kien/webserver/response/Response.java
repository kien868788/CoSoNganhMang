package com.kien.webserver.response;

import lombok.Data;

@Data
public class Response {
    String root;
    Status status;
    byte[] content;

    public byte[] getContent() {
        return  content == null ? "".getBytes() : content;
    }
}
