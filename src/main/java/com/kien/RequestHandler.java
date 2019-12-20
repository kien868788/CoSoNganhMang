package com.kien;

import java.io.BufferedInputStream;

/**
 * Every subclass of this interface will handler a request from client as an input stream
 * then send back response as byte data
 */
public interface RequestHandler {
    byte[] response(BufferedInputStream input, String root);
}
