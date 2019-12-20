package com.kien.webserver.response;

import com.kien.RequestHandler;
import com.kien.webserver.request.Request;
import com.kien.webserver.request.RequestFactory;

import java.io.BufferedInputStream;

public class HttpRequestHandler implements RequestHandler {
    @Override
    public byte[] response(BufferedInputStream input, String root) {
        try {
            // parse byte data to request object
            Request request = RequestFactory.parseRequest(input);

            // parse request object to byte data to response to client
            Response response = ResponseFactory.create(request, root);
            return response.getContent();
        } catch (Exception e) {
            System.err.println(": Error while reading http request: ");
            return "".getBytes();
        }
    }
}
