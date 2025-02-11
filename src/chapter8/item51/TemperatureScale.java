package chapter8.item51;

public enum TemperatureScale {
    FAHRENHEIT {
        double toCelsius(double fahrenheit) {
            return (fahrenheit - 32) * (double) 5 / (double) 9;
        };
    },
    CELSIUS {
        double toFahrenheit(double celsius) {
            return (double) 5 / (double) 9 * celsius + 32;
        };
    }
}
