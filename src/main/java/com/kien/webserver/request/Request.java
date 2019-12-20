package com.kien.webserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    Method method;
    String path;
    boolean keepAlive;
    int contentLength;

    // boundary to differentiate multi-part form data
    String boundary;
    byte[] content;
}
