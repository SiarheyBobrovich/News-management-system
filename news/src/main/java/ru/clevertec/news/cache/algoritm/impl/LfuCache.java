package ru.clevertec.news.cache.algoritm.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.clevertec.news.cache.algoritm.CacheAlgorithm;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Scope("prototype")
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm", havingValue = "lfu")
public class LfuCache<ID, T> implements CacheAlgorithm<ID, T> {

    private final Map<ID, Node> idNodeMap; //cache id and Node (object and counters)
    private final Map<Integer, LinkedHashSet<ID>> countIdMap;   //Counter and id set
    private final int cap;  //max elements
    private final AtomicInteger min = new AtomicInteger(); //min count

    private final Lock lock = new ReentrantLock();

    public LfuCache(@Value("${spring.cache.size}") int capacity) {
        cap = capacity;
        idNodeMap = new HashMap<>();
        countIdMap = new HashMap<>();
        countIdMap.put(1, new LinkedHashSet<>());
    }

    @Override
    public Optional<T> get(ID id) {
        lock.lock();
        try {
            if (Objects.isNull(id) || !idNodeMap.containsKey(id)) {
                return Optional.empty();
            }

            Node node = idNodeMap.get(id);
            increaseCount(id, node);

            return Optional.of(node.object);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Увеличивает колличество обращения к объекту
     *
     * @param id   идентификатор объекта
     * @param node объект {@link Node} с объектом
     */
    private void increaseCount(ID id, Node node) {
        int count = node.count.getAndIncrement();
        countIdMap.get(count).remove(id);

        if (count == min.get() && countIdMap.get(count).size() == 0) {
            min.incrementAndGet();
        }

        int nodeCount = node.count.get();
        if (!countIdMap.containsKey(nodeCount)) {
            countIdMap.put(nodeCount, new LinkedHashSet<>());
        }
        countIdMap.get(nodeCount).add(id);
    }

    @Override
    public void delete(ID id) {
        lock.lock();
        try {
            Node remove = idNodeMap.remove(id);
            if (Objects.nonNull(remove)) {
                countIdMap.get(remove.count.get()).remove(id);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(ID id, T obj) {
        if (cap <= 0 || Objects.isNull(id) || Objects.isNull(obj))
            return;

        lock.lock();
        try {
            if (idNodeMap.containsKey(id)) {
                Node node = idNodeMap.get(id);
                node.object = obj;
                increaseCount(id, node);
                return;
            }

            if (idNodeMap.size() >= cap) {
                ID evit = countIdMap.get(min.get()).iterator().next();
                countIdMap.get(min.get()).remove(evit);
                idNodeMap.remove(evit);
            }

            idNodeMap.put(id, new Node(obj, new AtomicInteger(1)));
            min.set(1);
            countIdMap.get(min.get())
                    .add(id);
        } finally {
            lock.unlock();
        }
    }

    @AllArgsConstructor
    private class Node {
        private T object;
        private AtomicInteger count;
    }

    @Override
    public void clear() {
        idNodeMap.clear();
        countIdMap.clear();
        countIdMap.put(1, new LinkedHashSet<>());
    }
}
