package chapter8.item51;

public class Thermometer {

    public static Thermometer newInstance(boolean isCelsius) {
       return new Thermometer();
    };

    public static Thermometer newInstance(TemperatureScale temperatureScale) {
        return new Thermometer();
    }
}

