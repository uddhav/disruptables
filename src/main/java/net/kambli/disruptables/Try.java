package net.kambli.disruptables;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Like Scala Try[+T] type but I've tried to keep interface similar to Optional
 *
 * @see Success<T>
 * @see Failure<T>
 *
 * @since May 2015
 */
public interface Try<T>
{
    /**
     * {{{
     *      Try<Double> quotient = Try.get(() -> {
     *          double dividend = Double.valueOf(request.getParameter("dividend");
     *          double divisor = Double.valueOf(request.getParameter("divisor");
     *
     *          return dividend / divisor;
     *      });
     *
     *      return quotient.orElse(Double.NaN);
     * }}}
     *
     * @param supplier a block of code represented by a {@code DisruptableSupplier}
     * @param <T> the type of result
     * @return a {@code Try<T>} with result or an exception
     */
    static <T> Try<T> get(DisruptableSupplier<T> supplier)
    {
        try
        {
            return new Success<>(supplier.get());
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }

    /**
     * {{{
     *      Try<T> result = Try.run(() -> {
     *          doOperation();
     *      });
     *
     *      result.orElseThrow(e -> new RuntimeException(e));
     * }}}
     *
     * @param runnable a block of code represented by a {@code DisruptableRunnable}
     * @return a {@code Try<T>} with result or an exception
     */
    @SuppressWarnings("unchecked")
    static <T> Try<T> run(DisruptableRunnable runnable)
    {
        try
        {
            runnable.run();
            return (Try<T>)Success.EMPTY;
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }

    /**
     * @return the result from this `Success` or throws the exception if this is a `Failure`.
     * @throws Exception
     */
    T get() throws Exception;

    /**
     * @param other the result to be returned if `Try` is a `Failure`
     * @return the result, if `Try` is a `Success`, otherwise {@code other}
     */
    T orElse(T other);

    /**
     * @param disruptable the block to be tried if `Try` is a `Failure`
     * @return the result, if `Try` is a `Success`, otherwise try {@code other}
     */
    Try<T> orElseGet(DisruptableSupplier<T> disruptable);

    /**
     * @param disruptable the block to be tried if `Try` is a `Failure`
     * @return the result, if `Try` is a `Success`, otherwise try {@code other}
     */
    Try<T> orElseRun(DisruptableRunnable disruptable);

    /**
     * Return the calculated result, if present, otherwise throw an exception to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty argument list can be used as the supplier.
     * For example, {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param mapper The mapper which will transform the exception to be thrown
     * @return the present value
     * @throws X if there is no value present
     */
    <X extends Throwable> T orElseThrow(Function<? super Exception, ? extends X> mapper) throws X;

    /**
     * @return `true` if the `Try` is a `Success`, `false` otherwise.
     */
    boolean isSuccess();

    /**
     * @return `true` if the `Try` is a `Failure`, `false` otherwise
     */
    boolean isFailure();

    /**
     * @param successHandler is applied if this is a `Success`, otherwise does nothing if this is a `Failure`
     */
    void ifSuccess(Consumer<? super T> successHandler);

    /**
     * @param failureHandler is applied if this is a `Failure`, otherwise does nothing if this is a `Success`
     */
    void ifFailed(Consumer<Exception> failureHandler);

    /**
     * @param predicate a predicate to apply to the result, if successful
     * @return a `Failure` if the predicate is not satisfied
     */
    Try<T> filter(DisruptablePredicate<? super T> predicate);

    /**
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the result, if successful
     * @return the {@code Try<U>} from mapping the given function to the result from this `Success` or returns this if this is a `Failure`.
     */
    <U> Try<U> map(DisruptableFunction<? super T, ? extends U> mapper);

    /**
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the result, if successful
     * @return the {@code Try<U>} from mapping the given function to the result from this `Success` or returns this if this is a `Failure`.
     */
    <U> Try<U> flatMap(DisruptableFunction<? super T, Try<U>> mapper);

    /**
     * Throw an exception to be specified by the caller on match otherwise returns itself
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionClass The expected exception type
     * @return the {@code Try<T>} if it is a `Success` or a `Failure` with a different exception
     * @throws X if there is no value present
     */
    <X extends Throwable> Try<T> capitulate(Class<X> exceptionClass) throws X;

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like map for the exception.
     * @param mapper a mapping function to apply to the exception, if failed
     * @return a result to be mapped in case of a failure
     */
    Try<T> recover(DisruptableFunction<? super Exception, ? extends T> mapper);

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like `flatMap` for the exception.
     * @param mapper a mapping function to apply to the exception, if failed
     * @return a result to be mapped in case of a failure
     */
    Try<T> recoverWith(DisruptableFunction<? super Exception, Try<T>> mapper);

    /**
     * @return `empty` if this is a `Failure` or a `optional` containing the result if this is a `Success`
     */
    Optional<T> toOptional();

    /**
     * Indicates whether some other object is "equal to" this Try.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Try} and;
     * <li>both instances have no result present or;
     * <li>the present results are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code value of the present result, if any, or as applicable if no result is present.
     *
     * @return hash code value of the present result or as applicable if no result is present
     */
    @Override
    int hashCode();

    /**
     * Returns a non-empty string representation of this Try suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present Try(s) must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    String toString();
}
