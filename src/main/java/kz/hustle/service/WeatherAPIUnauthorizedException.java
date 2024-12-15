package kz.hustle.service;

import java.io.IOException;

public class WeatherAPIUnauthorizedException extends IOException {
    public WeatherAPIUnauthorizedException(String message) {
        super(message);
    }
}
