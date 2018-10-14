package com.me4502.cab432;

import com.me4502.cab432.app.TwitterApp;

import java.io.IOException;

public class Assignment2 {

    public static void main(String[] args) {
        try {
            TwitterApp.getInstance().load();
        } catch (IOException e) {
            // If an exception makes it here, runtime.
            throw new RuntimeException(e);
        }
    }
}
