package ru.clevertec.news.cache.algoritm;

import java.util.Optional;

public interface CacheAlgorithm<ID, T> {

    /**
     * Сохраняет или обновляет объект типа T с заданным идентификатором ID.
     *
     * @param id идентификатор объекта типа ID
     * @param o  объект типа T, который нужно сохранить или обновить
     */
    void put(ID id, T o);

    /**
     * Возвращает объект типа T по заданному идентификатору.
     *
     * @param id идентификатор объекта типа ID
     * @return объект Optional, содержащий объект типа T или пустой, если такого объекта не существует
     */
    Optional<T> get(ID id);

    /**
     * Удаляет объект типа T с заданным идентификатором.
     *
     * @param id идентификатор объекта типа ID
     */
    void delete(ID id);

    /**
     * Удаляет все объекты типа T из хранилища.
     */
    void clear();
}
