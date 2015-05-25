package net.kambli.disruptables

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

/**
 * Testing Try<T>
 *
 * @since May 2015
 */
class TrySpec extends Specification
{
    def "Try is a success"()
    {
        given:
        def test = Try.run { -> "test" }

        expect:
        test.isSuccess()
        !test.isFailure()
    }

    def "Try is a failure"()
    {
        given:
        def test = Try.run { "test".charAt -1 }

        expect:
        !test.isSuccess()
        test.isFailure()
    }

    def "Try to get an expected result on success"()
    {
        given:
        def test = Try.get { "substring".indexOf "string" }

        when:
        def index = test.get()

        then:
        index != null
        index == 3
    }

    def "Try to get an alternate result on failure"()
    {
        given:
        def test = Try.get { "substring".indexOf "test" }

        when:
        def index = test.filter { _ >= 0 } orElse 10

        then:
        index != null
        index == 10
    }

    @SuppressWarnings("GroovyDivideByZero")
    def "Try to calculate an alternate result on failure"()
    {
        given:
        def test = Try.get { -> 1 / 0 }

        when:
        def result = test.orElseGet { -> 10 } get()

        then:
        result != null
        result.intValue() == 10
    }

    def "Throw an exception from a disrupted block of code"()
    {
        given:
        def test = Try.run { -> Collections.emptyList() get 0 }

        when:
        test.orElseRun { -> Optional.empty().get() } get()

        then:
        thrown(NoSuchElementException)
    }

    def "Execute an action on success"()
    {
        given:
        def test = Try.run { "test".charAt(0) }
        def value = new AtomicInteger(0)

        when:
        test.ifSuccess { _ -> value.set 1 }

        then:
        value.intValue() == 1
    }

    def "Execute an action on failure"()
    {
        given:
        def test = Try.run { "test".charAt(-1) }
        def value = new AtomicInteger(0)

        when:
        test.ifFailed { _ -> value.set 1 }

        then:
        value.intValue() == 1
    }

    def "Filter calculated result"()
    {
        given:
        def test = Try.get { -> 20.0 }
        def filtered = test.filter { _ -> _.compareTo(21.0) < 0  }

        expect:
        test == filtered
    }

    def "Transform calculated result with flatmap"()
    {
        given:
        def index = Try.get { -> "substring".substring(3) }.flatMap { t -> Try.get { -> t.length() } }.get()

        expect:
        index != null
        index == 6
    }

    def "Transform calculated result with map"()
    {
        given:
        def index = Try.get { -> "substring".substring(3) }.map { t -> t.length() }.get()

        expect:
        index != null
        index == 6
    }

    def "Capitulate on failure"()
    {
        when:
        Try.get { -> "string" }.map { _ -> _.substring(-1) }.capitulate IndexOutOfBoundsException

        then:
        thrown(StringIndexOutOfBoundsException)
    }

    @SuppressWarnings("GroovyDivideByZero")
    def "Recover from an exceptional situation"()
    {
        given:
        def test = Try.get { -> 5 / 0 }

        when:
        def recovered = test.recover { e -> 10 } get()

        then:
        recovered != null
        recovered.intValue() == 10
    }

    @SuppressWarnings("GroovyDivideByZero")
    def "Recover from an exceptional situation with mapper"()
    {
        given:
        def test = Try.get { -> 5 / 0 }

        when:
        def recovered = test.recoverWith { e -> Try.get { -> 10 } } get()

        then:
        recovered != null
        recovered.intValue() == 10
    }

    @SuppressWarnings("GroovyDivideByZero")
    def "Convert Try result to an Optional"()
    {
        expect:
        result.toOptional().isPresent() == expectedFlag

        where:
        result                  |   expectedFlag
        Try.get { -> 5 / 0 }    |   false
        Try.get { -> 5 }        |   true
    }

    def "Similar results from different calculations should be equal"()
    {
        given:
        def left = Try.get { -> 10 / 2 }
        def right = Try.get { -> 25 / 5 }

        expect:
        left == right
        left.hashCode() == right.hashCode()
    }
}
