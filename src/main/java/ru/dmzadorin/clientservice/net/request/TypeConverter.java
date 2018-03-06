package ru.dmzadorin.clientservice.net.request;

/**
 * Created by Dmitry Zadorin on 05.03.2018
 */
public interface TypeConverter<T> {
    /**
     * Transforms input string value in desired type, for example Integer, Boolean, Double
     * If it fails to convert string - will throw runtime exception
     *
     * @param input string to convert
     * @return converted value
     */
    T convertFromString(String input);
}
