package com.kien.webserver.response;


import com.kien.webserver.request.Method;
import com.kien.webserver.request.Request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class ResponseFactory {
    public static Response create(Request request, String root) {
        Method requestMethod = request.getMethod();
        if (requestMethod != null) {
            switch (requestMethod) {
                case GET: return createGETResponse(request, root);
                case PUT: return createPUTResponse(request, root);
                case POST: return createPOSTResponse(request, root);
                case DELETE: return createDELETEResponse(request, root);
                default: return createOPTIONResponse(request, root);
            }
        } else {
            return createNotSupportResponse(request, root);
        }
    }

    private static Response createOPTIONResponse(Request request, String root) {
        Response response = new Response();
        Status status = Status.OK;
        int responseLength = 0;
        String mimeType = "text/html; charset=iso-8859-1";
        String header = generateHeader(status, responseLength, mimeType);
        response.setContent(header.getBytes(StandardCharsets.ISO_8859_1));
        return response;
    }

    private static Response createNotSupportResponse(Request request, String root) {
        Response response = new Response();
        Status status = Status.METHOD_NOT_ALLOW;
        int responseLength = 0;
        String mimeType = "text/html; charset=iso-8859-1";
        String header = generateHeader(status, responseLength, mimeType);
        response.setContent(header.getBytes(StandardCharsets.ISO_8859_1));
        return response;
    }

    private static Response createDELETEResponse(Request request, String root) {
        Response response = new Response();
        Status status = Status.OK;
        int responseLength = 0;
        String mimeType = "text/html; charset=iso-8859-1";

        if (!request.getPath().isEmpty()) {
            String path = request.getPath();
            String completePath = root + File.separator + path;
            Path pathVal = Paths.get(completePath);
            if (!Files.exists(pathVal)) {
                status = Status.NOT_FOUND;
            }
            else {
                try { Files.delete(pathVal);
                } catch (IOException e) {
                    System.err.println("Error while deleting file. " + e.getMessage());
                    status = Status.INTERNAL_SERVER_ERROR;
                }
            }
        }
        else {
            // Internal kien.webserver.server error
            status = Status.INTERNAL_SERVER_ERROR;
        }

        String header = generateHeader(status, responseLength, mimeType);

        response.setContent(header.getBytes(StandardCharsets.ISO_8859_1));

        return response;

    }

    private static Response createPOSTResponse(Request request, String root) {
        Response response = new Response();
        Status status = Status.OK;
        int responseLength = 0;
        String mimeType = "text/html; charset=iso-8859-1";

        if (!request.getPath().isEmpty()) {
            String path = request.getPath();
            String completePath = root + File.separator + path;
            Path pathVal = Paths.get(completePath);
            try {
                // When creating the file with name conflict just simply delete and create new one
                Files.deleteIfExists(pathVal);
                Files.createFile(pathVal);
                Files.write(pathVal, request.getContent());
            } catch (IOException e) {
                System.err.println("Error while writing file. " + e.getMessage());
                status = Status.INTERNAL_SERVER_ERROR;
            }
        }
        else {
        // Internal server error
            status = Status.INTERNAL_SERVER_ERROR;
        }


        String header = generateHeader(status, responseLength, mimeType);

        response.setContent(header.getBytes(StandardCharsets.ISO_8859_1));

        return response;
    }


    private static Response createPUTResponse(Request request, String root) {
        // Because we are actually working with file,
        // changing the file is just simply creating new one and removing existing file.
        return createPOSTResponse(request, root);
    }

    private static Response createGETResponse(Request request, String root) {
        Response response = new Response();

        byte[] content = new byte[0];
        Status status = Status.OK;
        int responseLength = 0;
        String mimeType = "text/html; charset=iso-8859-1";

        if (!request.getPath().isEmpty()) {
            String path = request.getPath();
            String completePath = root + File.separator + path;
            Path pathVal = Paths.get(completePath);
            if (!Files.exists(pathVal)) {
                status = Status.NOT_FOUND;
            }
            else {
                try {
                    content = Files.readAllBytes(pathVal);
                    mimeType = Files.probeContentType(pathVal);
                    responseLength = content.length;
                } catch (IOException e) {
                    System.err.println("Error while reading file. " + e.getMessage());
                    status = Status.INTERNAL_SERVER_ERROR;
                }
            }
        }
        else {
            // Internal server error
            status = Status.INTERNAL_SERVER_ERROR;
        }

        String header = generateHeader(status, responseLength, mimeType);

        if (responseLength > 0) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream( );

            try {
                byteStream.write(header.getBytes(StandardCharsets.ISO_8859_1));
                byteStream.write(content);
                response.setContent(byteStream.toByteArray());
            } catch (IOException e) {
                System.err.println("Error while concatenating header and message. " + e.getMessage());
            }


        } else {
            response.setContent(header.getBytes(StandardCharsets.ISO_8859_1));
        }

        return response;
    }

    private static String generateHeader(Status status, int responseLength, String mimeType) {
        Date date = new Date();
        String header = "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n" +
                "Date: " + date.toString() + "\r\n" +
                "Client: Client" + "\r\n" +
                "Content-Length: " + responseLength + "\r\n" +
                "Connection: Closed \r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "\r\n";
        return header;
    }
}
