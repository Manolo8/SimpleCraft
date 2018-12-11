package com.github.manolo8.simplecraft.core.commands.line.inf;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;

public interface Supplier<E> {

    default Class getSuppliedClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericInterfaces()[0];
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        return (Class<?>) typeArguments[0];
    }


    interface Convert<E> extends Supplier<E> {

        default E defaultValue() {
            return null;
        }

        default void tabComplete(TabArguments arguments) throws SQLException {

        }

        Result<E> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException;
    }

    interface Basic<E> extends Supplier<E> {

        Result<E> provide(Sender sender, Class<E> clazz);

        default Result<E> provideLocal(Sender sender, Class<E> clazz) {
            return provide(sender, clazz);
        }
    }
}
