package io.zaaim.arindexer.provider;

/**
 * A generic provider interface that defines a method to provide an iterable collection of items of type T based on an input of type K.
 *
 * @param <T> the type of items to be provided
 * @param <K> the type of the input parameter used to provide the items
 */
public interface Provider <V, K> {
    V provide(K yey);
}
