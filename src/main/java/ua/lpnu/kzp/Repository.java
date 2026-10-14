package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Типобезпечне параметризоване сховище записів у пам'яті. Параметр типу
 * {@code T} виключає {@code raw type} і приведення типів ззовні: сховище,
 * створене як {@code Repository<MatchRecord>}, приймає лише {@code MatchRecord}.
 *
 * @param <T> тип записів, які зберігає сховище
 */
public final class Repository<T> {

    private final List<T> items = new ArrayList<>();

    /**
     * Додає непорожній запис до сховища.
     *
     * @param item запис, який додається
     * @throws NullPointerException якщо {@code item} дорівнює {@code null}
     */
    public void add(T item) {
        items.add(Objects.requireNonNull(item, "Запис не може бути null"));
    }

    /**
     * Повертає незмінний знімок усіх записів у порядку додавання. Зовнішній
     * код не може через цей знімок змінити внутрішній стан сховища.
     *
     * @return незмінний список усіх записів
     */
    public List<T> all() {
        return List.copyOf(items);
    }

    /**
     * Повертає записи, що задовольняють умову. {@code Predicate<? super T>}
     * дозволяє передати умову, написану для супертипу {@code T}.
     *
     * @param condition умова відбору
     * @return незмінний список записів, що задовольняють {@code condition}
     */
    public List<T> find(Predicate<? super T> condition) {
        Objects.requireNonNull(condition, "Умова не може бути null");
        return items.stream().filter(condition).toList();
    }
}
