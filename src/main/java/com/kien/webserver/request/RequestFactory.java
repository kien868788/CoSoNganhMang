package com.kien.webserver.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class RequestFactory {
    public static Request parseRequest(BufferedInputStream input) throws IOException {
        Request request = new Request();
        String header = readHeader(input, request);
        String[] lines = header.split("\r\n");

        // Parse each header line
        for (int i=0; i<lines.length; i++) {
            parseHeaderLine(lines[i], request);
        }


        // Read content (if any)
        int contentLengthLocal = request.getContentLength();
        int chunk = 5000;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        boolean firstBoundary = true;
        while (contentLengthLocal > 0) {
            byte[] contentBuffer = new byte[chunk];
            int result = input.read(contentBuffer, 0, chunk);

            // check boundary if it's multi-part data
            if (request.getBoundary() != null) {
                String contentStr = new String(contentBuffer, StandardCharsets.ISO_8859_1);
                int boundaryIndex = contentStr.indexOf(request.getBoundary());
                if (boundaryIndex >= 0 && firstBoundary) {
                    int newlineIndex = contentStr.indexOf("\r\n\r\n", boundaryIndex);
                    int skip = newlineIndex + 4;
                    byteStream.write(contentBuffer, skip, result - skip);
                    firstBoundary = false;
                }
                else if (boundaryIndex >= 0 && !firstBoundary) {
                    byteStream.write(contentBuffer, 0, boundaryIndex);
                }
                else {
                    byteStream.write(contentBuffer, 0, result);
                }
            }
            else {
                byteStream.write(contentBuffer, 0, result);
            }
            contentLengthLocal -= result;
        }
        request.setContent(byteStream.toByteArray());
        return request;
    }





    private static void parseHeaderLine(String line, Request request) {
        // Method line
        // Extract method and path from it
        for (Method method : Method.values()) {
            if (line.startsWith(method.name())) {
                request.setMethod(method);
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken().trim();
                    if (Method.of(token) == method) {
                        String path = tokenizer.nextToken().trim();
                        request.setPath(path);
                        if (path.equals("/")) {
                            request.setPath("index.html");
                        }
                        System.out.println(path);
                    }
                }
                break;
            }
        }
        if (line.startsWith("Connection") && line.contains("keep-alive")) {
            request.setKeepAlive(true);
        }
        if (line.startsWith("Content-Length")) {
            StringTokenizer tokenizer = new StringTokenizer(line, ":");
            tokenizer.nextToken();
            String contentLengthStr = tokenizer.nextToken().trim();
            int contentLength = Integer.parseInt(contentLengthStr);
            request.setContentLength(contentLength);
        }
        if (line.contains("boundary")) {
            String boundaryToken = line.substring(line.indexOf("boundary="));
            request.setBoundary("--" + boundaryToken.replace("boundary=", ""));
        }
    }

    private static String readHeader(BufferedInputStream input, Request request) throws IOException {
        String s = "";
        int i;
        while((i = input.read()) != -1) {
            char c = (char) i;
            s += c;

            if (s.endsWith("\r\n\r\n")) {
                break;
            }
        }
        return s.trim();
    }
}
