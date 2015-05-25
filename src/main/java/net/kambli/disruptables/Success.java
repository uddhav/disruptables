package net.kambli.disruptables;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The `Try` was successful
 *
 * @since May 2015
 */
final class Success<T> implements Try<T>
{
    static final Try<?> EMPTY = new Success<>(null);

    private final T m_result;

    Success(final T result)
    {
        m_result = result;
    }
    
    @Override
    public T get() throws Exception
    {
        return m_result;
    }
    
    @Override
    public T orElse(final T other)
    {
        return m_result;
    }
    
    @Override
    public Try<T> orElseGet(final DisruptableSupplier<T> supplier)
    {
        Objects.requireNonNull(supplier);
        return this;
    }
    
    @Override
    public Try<T> orElseRun(final DisruptableRunnable runnable)
    {
        Objects.requireNonNull(runnable);
        return this;
    }
    
    @Override
    public <X extends Throwable> T orElseThrow(final Function<? super Exception, ? extends X> mapper) throws X
    {
        Objects.requireNonNull(mapper);
        return m_result;
    }

    @Override
    public boolean isSuccess()
    {
        return true;
    }

    @Override
    public boolean isFailure()
    {
        return false;
    }

    @Override
    public void ifSuccess(final Consumer<? super T> successHandler)
    {
        Objects.requireNonNull(successHandler);
        successHandler.accept(m_result);
    }

    @Override
    public void ifFailed(final Consumer<Exception> failureHandler)
    {
        Objects.requireNonNull(failureHandler);
    }
    
    @Override
    public Try<T> filter(final DisruptablePredicate<? super T> predicate)
    {
        Objects.requireNonNull(predicate);

        try
        {
            return predicate.test(m_result) ? this : new Failure<>(new NoSuchElementException("Predicate does not hold for " + m_result));
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }
    
    @Override
    public <U> Try<U> map(final DisruptableFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);

        try
        {
            return new Success<>(mapper.apply(m_result));
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }
    
    @Override
    public <U> Try<U> flatMap(final DisruptableFunction<? super T, Try<U>> mapper)
    {
        Objects.requireNonNull(mapper);

        try
        {
            return mapper.apply(m_result);
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }
    
    @Override
    public <X extends Throwable> Try<T> capitulate(final Class<X> exceptionClass) throws X
    {
        Objects.requireNonNull(exceptionClass);
        return this;
    }
    
    @Override
    public Try<T> recover(final DisruptableFunction<? super Exception, ? extends T> mapper)
    {
        Objects.requireNonNull(mapper);
        return this;
    }
    
    @Override
    public Try<T> recoverWith(final DisruptableFunction<? super Exception, Try<T>> mapper)
    {
        Objects.requireNonNull(mapper);
        return this;
    }
    
    @Override
    public Optional<T> toOptional()
    {
        return Optional.ofNullable(m_result);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Success))
        {
            return false;
        }

        Success<?> that = (Success<?>)obj;
        return Objects.equals(m_result, that.m_result);
    }

    @Override
    public int hashCode()
    {
        return m_result == null ? 0 : m_result.hashCode();
    }

    @Override
    public String toString()
    {
        return String.format("Success[%s]", m_result);
    }
}
