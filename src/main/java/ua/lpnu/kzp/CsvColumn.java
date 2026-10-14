package ua.lpnu.kzp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Позначає поле, яке {@link CsvExporter} експортує як стовпець CSV.
 * {@link RetentionPolicy#RUNTIME} залишає анотацію доступною для рефлексії
 * під час виконання (без неї {@code getAnnotation} у {@link CsvExporter}
 * нічого не знайде); {@link ElementType#FIELD} обмежує застосування лише
 * полями. Анотація лише описує контракт — вона не читає й не записує дані
 * сама.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    /** Повертає заголовок CSV-стовпця, який буде записано в перший рядок файла. */
    String value();
}
