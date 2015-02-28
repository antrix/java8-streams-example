/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.antrix;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Runner {

    public static final int LIMIT = 4_000_000;

    @Benchmark
    public Integer usingIterator() {
        FibonacciSequence fibonacciSequence = new FibonacciSequence(LIMIT);

        Integer sum = 0;

        while (fibonacciSequence.hasNext()) {
            Integer value = fibonacciSequence.next();
            if (value % 2 == 0) {
                sum += value;
            }
        }

        return sum;
    }

    @Benchmark
    public Integer usingIterable() {
        FibonacciSequence fibonacciSequence = new FibonacciSequence(LIMIT);

        Integer sum = 0;

        for (Integer value : fibonacciSequence) {
            if (value % 2 == 0) {
                sum += value;
            }
        }

        return sum;
    }

    @Benchmark
    public int usingStream() {
        FibonacciSequence fibonacciSequence = new FibonacciSequence(LIMIT);

        Stream<Integer> fibStream = StreamSupport.stream(fibonacciSequence.spliterator(), false);

        int sum = fibStream
                .filter(i -> i % 2 == 0)
                .mapToInt(i -> i)
                .sum();

        return sum;
    }

    @Benchmark
    public int usingRecursiveStream() {
        int sum = recursiveFibStream(0, 1, LIMIT)
                .filter(i -> i % 2 == 0)
                .mapToInt(i -> i)
                .sum();

        return sum;
    }

    private static Stream<Integer> recursiveFibStream(int a, int b, int max) {
        Integer current = a + b, prev = b;
        if (current <= max) {
            return Stream.concat(Stream.of(current), recursiveFibStream(prev, current, max));
        } else {
            return Stream.empty();
        }
    }


    public static void main(String[] args) {
        Runner runner = new Runner();

        System.out.println("Sum using Iterator = " + runner.usingIterator());
        System.out.println("Sum using Iterable = " + runner.usingIterable());
        System.out.println("Sum using Stream = " + runner.usingStream());
        System.out.println("Sum using RecursiveStream = " + runner.usingRecursiveStream());
    }
}
