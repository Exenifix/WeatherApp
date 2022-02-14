package com.exenifix.weatherapp;

public class APIException extends Exception {
    public APIException() {
        super("Could not fetch the data for the given city");
    }
}
