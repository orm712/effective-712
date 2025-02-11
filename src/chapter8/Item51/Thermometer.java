package chapter8.Item51;

public class Thermometer {

    public static Thermometer newInstance(boolean isCelsius) {
       return new Thermometer();
    };

    public static Thermometer newInstance(TemperatureScale temperatureScale) {
        return new Thermometer();
    }
}

