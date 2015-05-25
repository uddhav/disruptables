package net.kambli.disruptables;

/**
 * A {@code Predicate<T>} representation of a predicate that can be disrupted
 *
 * @since May 2015
 */
@FunctionalInterface
public interface DisruptablePredicate<T>
{
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param result the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T result) throws Exception;
}
