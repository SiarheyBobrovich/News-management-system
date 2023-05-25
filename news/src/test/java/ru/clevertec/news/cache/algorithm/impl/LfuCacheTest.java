package ru.clevertec.news.cache.algorithm.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.news.cache.algoritm.impl.LfuCache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LfuCacheTest {

    private LfuCache<Long, String> algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new LfuCache<>(3);
    }

    @Test
    void checkPut() {
        algorithm.put(1L, "5");
        String integer = algorithm.get(1L).orElseThrow();

        assertEquals("5", integer);
    }

    @Test
    void checkPutAsynch() {
        algorithm = new LfuCache<>(Integer.MAX_VALUE);
        ConcurrentHashMap<Long, String> map = new ConcurrentHashMap<>();
        Random rnd = new Random();
        LongStream.range(1, 100_000)
                .parallel()
                .mapToObj(x -> {
                    String value = String.valueOf(rnd.nextLong(1_000));
                    algorithm.put(x, value);
                    return Map.entry(x, value);
                }).forEach((k) -> map.put(k.getKey(), k.getValue()));

        map.forEach((k, v) -> {
            Optional<String> value = algorithm.get(k);
            assertThat(value).isNotEmpty()
                    .get().isEqualTo(v);
        });
    }

    @Test
    void checkGetAsynch() {
        algorithm = new LfuCache<>(Integer.MAX_VALUE);
        Random rnd = new Random();
        LongStream.range(1, 100_000)
                .parallel()
                .forEach(x -> {
                    String value = String.valueOf(rnd.nextInt(1_000));
                    algorithm.put(x, value);
                    Optional<String> optional = algorithm.get(x);
                    assertThat(optional).isNotEmpty()
                            .get().isEqualTo(value);
                });
    }

    @Test
    void checkRemoveEldestElement() {
        algorithm = new LfuCache<>(10);
        AtomicInteger count = new AtomicInteger();

        AtomicLong i = new AtomicLong(1);

        List<Long> integers = Stream.generate(i::getAndIncrement)
                .limit(10_000)
                .toList();

        integers.stream()
                .parallel()
                .forEach(x -> algorithm.put(x, String.valueOf(x)));

        integers.stream()
                .parallel()
                .forEach(x -> algorithm.get(x)
                        .ifPresent(o -> count.incrementAndGet()));

        assertThat(count.get())
                .isEqualTo(10);
    }

    @Test
    void checkFrequencyAsynch() {
        algorithm = new LfuCache<>(2);
        algorithm.put(1L, "One");
        IntStream.range(1, 999)
                .forEach(x -> algorithm.get(1L));

        algorithm.put(2L, "Two");
        IntStream.range(1, 1000)
                .parallel()
                .forEach(x -> algorithm.get(2L));
        algorithm.put(3L, "Three");
        Optional<String> optional = algorithm.get(1L);

        assertThat(optional).isEmpty();
    }

    @Test
    void checkPut4Elements() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");

        Optional<String> actual = algorithm.get(1L);

        assertThat(actual).isEmpty();
    }

    @Test
    void checkPutFIFO() {
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(2L, "Two");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");

        Optional<String> actual = algorithm.get(1L);

        assertThat(actual).isEmpty();
    }

    @Test
    void checkPutFIFO2() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(1L, "One");
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");

        Optional<String> actual = algorithm.get(3L);
        assertThat(actual).isEmpty();
    }

    @Test
    void checkGetNull() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(1L, "One");
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");

        Optional<String> actual = algorithm.get(null);
        assertThat(actual).isEmpty();
    }

    @Test
    void checkGetExistOne() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(1L, "One");
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");

        String actual = algorithm.get(1L)
                .orElseThrow();

        assertThat(actual)
                .isEqualTo("One");
    }

    @Test
    void checkGetExistTwo() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(1L, "One");
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");


        String actual = algorithm.get(2L)
                .orElseThrow();

        assertThat(actual)
                .isEqualTo("Two");
    }

    @Test
    void checkGetNotExistFour() {
        algorithm.put(2L, "Two");
        algorithm.put(1L, "One");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(2L, "Two");
        algorithm.put(3L, "Three");
        algorithm.put(4L, "Four");
        algorithm.put(1L, "One");
        algorithm.put(1L, "One");
        algorithm.put(2L, "Two");

        String actual = algorithm.get(4L)
                .orElseThrow();

        assertThat(actual)
                .isEqualTo("Four");
    }

    @Test
    void checkDelete() {
        algorithm.put(2L, "Two");
        algorithm.delete(2L);

        assertThat(algorithm.get(2L)).isEmpty();
    }
}
