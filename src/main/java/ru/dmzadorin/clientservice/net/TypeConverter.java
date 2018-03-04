package ru.dmzadorin.clientservice.net;

/**
 * Created by Dmitry Zadorin on 05.03.2018
 */
public interface TypeConverter<T> {
    T convertFromString(String input);
}
