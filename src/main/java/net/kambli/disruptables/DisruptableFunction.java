package net.kambli.disruptables;

/**
 * A {@code Function<T>} representation of transformation that can be disrupted
 *
 * @since May 2015
 */
@FunctionalInterface
public interface DisruptableFunction<T, R>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws Exception;
}
