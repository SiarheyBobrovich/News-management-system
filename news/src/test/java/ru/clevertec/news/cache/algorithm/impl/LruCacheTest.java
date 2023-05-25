package ru.clevertec.news.cache.algorithm.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.news.cache.algoritm.impl.LruCache;

import static org.assertj.core.api.Assertions.assertThat;

class LruCacheTest {

    private static final LruCache<Long, String> lruCache = new LruCache<>(3);

    @BeforeEach
    void cleat() {
        lruCache.clear();
    }

    @Test
    void checkPutIgnoreNullNull() {
        lruCache.put(null, null);
        lruCache.put(1L, "actual");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreNullNullTwoElements() {
        lruCache.put(null, null);
        lruCache.put(3L, "Three");
        lruCache.put(1L, "actual");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreNullNullThreeElements() {
        lruCache.put(null, null);
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");
        lruCache.put(1L, "actual");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreIdNullOneElement() {
        lruCache.put(null, "Not null");
        lruCache.put(1L, "actual");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreIdNullTwoElements() {
        lruCache.put(null, "Not null");
        lruCache.put(3L, "Three");
        lruCache.put(1L, "actual");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreIdNullThreeElements() {
        lruCache.put(null, "Not null");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");
        lruCache.put(1L, "actual");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreObjectNullOneElements() {
        lruCache.put(2L, null);
        lruCache.put(1L, "actual");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreObjectNullTwoElements() {
        lruCache.put(2L, null);
        lruCache.put(3L, "Three");
        lruCache.put(1L, "actual");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkPutIgnoreObjectNullThreeElements() {
        lruCache.put(2L, null);
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");
        lruCache.put(1L, "actual");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkGetMaxSize3() {
        lruCache.put(1L, "actual");
        lruCache.put(2L, "two");
        lruCache.put(3L, "Three");
        lruCache.put(4L, "Four");

        String actual = lruCache.get(1L).orElse("null");

        assertThat(actual)
                .isEqualTo("null");
    }

    @Test
    void checkGetActual() {
        lruCache.put(1L, "actual");
        lruCache.put(2L, "two");
        lruCache.put(3L, "Three");
        lruCache.get(1L);
        lruCache.get(2L);
        lruCache.put(4L, "Four");
        lruCache.get(1L);
        lruCache.put(3L, "Three");
        lruCache.get(4L);
        lruCache.get(1L);
        lruCache.put(2L, "two");
        lruCache.put(3L, "Three");

        String actual = lruCache.get(1L).orElseThrow();

        assertThat(actual)
                .isEqualTo("actual");
    }

    @Test
    void checkDelete() {
        lruCache.put(2L, "Two");
        lruCache.delete(2L);

        String two = lruCache.get(2L).orElse("null");

        assertThat(two)
                .isEqualTo("null");
    }
}
