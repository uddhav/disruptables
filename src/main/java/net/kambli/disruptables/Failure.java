package net.kambli.disruptables;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The `Try` was a failure
 *
 * @since May 2015
 */
final class Failure<T> extends Try<T>
{
    private final Exception m_exception;

    Failure(final Exception exception)
    {
        m_exception = exception;
    }
    
    @Override
    public T get() throws Exception
    {
        throw m_exception;
    }
    
    @Override
    public T orElse(final T other)
    {
        return other;
    }
    
    @Override
    public Try<T> orElseGet(final DisruptableSupplier<T> supplier)
    {
        Objects.requireNonNull(supplier);
        return Try.get(supplier);
    }
    
    @Override
    public Try<T> orElseRun(final DisruptableRunnable runnable)
    {
        Objects.requireNonNull(runnable);
        return Try.run(runnable);
    }
    
    @Override
    public <X extends Throwable> T orElseThrow(final Function<? super Exception, ? extends X> mapper) throws X
    {
        Objects.requireNonNull(mapper);
        throw mapper.apply(m_exception);
    }

    @Override
    public boolean isSuccess()
    {
        return false;
    }

    @Override
    public boolean isFailure()
    {
        return true;
    }

    @Override
    public void ifSuccess(final Consumer<? super T> successHandler)
    {
        Objects.requireNonNull(successHandler);
    }

    @Override
    public void ifFailed(final Consumer<Exception> failureHandler)
    {
        Objects.requireNonNull(failureHandler);
        failureHandler.accept(m_exception);
    }
    
    @Override
    public Try<T> filter(final DisruptablePredicate<? super T> predicate)
    {
        Objects.requireNonNull(predicate);
        return this;
    }
    
    @Override
    public <U> Try<U> map(final DisruptableFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return new Failure<>(m_exception);
    }
    
    @Override
    public <U> Try<U> flatMap(final DisruptableFunction<? super T, Try<U>> mapper)
    {
        Objects.requireNonNull(mapper);
        return new Failure<>(m_exception);
    }
    
    @Override
    public <X extends Throwable> Try<T> capitulate(final Class<X> exceptionClass) throws X
    {
        Objects.requireNonNull(exceptionClass);

        if (exceptionClass.isInstance(m_exception))
        {
            throw exceptionClass.cast(m_exception);
        }

        return this;
    }
    
    @Override
    public Try<T> recover(final DisruptableFunction<? super Exception, ? extends T> mapper)
    {
        Objects.requireNonNull(mapper);

        try
        {
            return new Success<>(mapper.apply(m_exception));
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }
    
    @Override
    public Try<T> recoverWith(final DisruptableFunction<? super Exception, Try<T>> mapper)
    {
        Objects.requireNonNull(mapper);

        try
        {
            return mapper.apply(m_exception);
        }
        catch (Exception exception)
        {
            return new Failure<>(exception);
        }
    }
    
    @Override
    public Optional<T> toOptional()
    {
        return Optional.empty();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Failure))
        {
            return false;
        }

        Failure<?> that = (Failure<?>)obj;
        return Objects.equals(m_exception, that.m_exception);
    }

    @Override
    public int hashCode()
    {
        return m_exception.hashCode();
    }

    @Override
    public String toString()
    {
        return String.format("Failure[%s]", m_exception);
    }
}
