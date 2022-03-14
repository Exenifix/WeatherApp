package com.exenifix.weatherapp;

public class APIException extends Exception {
    public int errorCode;

    public APIException(int errorCode) {
        super("Could not fetch the data for the given city");
        this.errorCode = errorCode;
    }
}
