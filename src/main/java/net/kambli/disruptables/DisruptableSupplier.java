package net.kambli.disruptables;

/**
 * A {@code Supplier<T>} representation of deferred execution that can be disrupted
 *
 * @since May 2015
 */
@FunctionalInterface
public interface DisruptableSupplier<T>
{
    /**
     * Gets a result.
     *
     * @return a result of type {@code T}
     * @throws an {@code Exception} that disrupts the supplier
     */
    T get() throws Exception;
}
