package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Записує анотовані {@link CsvColumn} поля довільного плоского класу в
 * UTF-8 CSV. Не знає жодного домену наперед: не викликає геттери, не містить
 * розгалуження за конкретним класом чи підтипом і визначає стовпці лише
 * через рефлексію над {@code type}.
 */
public final class CsvExporter {

    private CsvExporter() {
    }

    /**
     * Записує анотовані поля {@code records} у CSV-файл за шляхом {@code path}.
     * Порядок стовпців детермінований — поля сортуються за іменем Java-поля
     * (а не за порядком оголошення), тому заголовок і значення завжди
     * узгоджені. Файл перезаписується повністю ({@link StandardOpenOption#TRUNCATE_EXISTING}),
     * щоб після коротшого експорту не лишались хвости попереднього файла.
     *
     * @param <T> тип записів
     * @param path шлях до CSV-файла
     * @param type клас записів (потрібен навіть для порожнього списку, щоб побудувати заголовок)
     * @param records записи для експорту
     * @throws DataStorageException якщо в {@code type} немає жодного анотованого поля,
     *         поле недоступне для читання або файл неможливо записати
     */
    public static <T> void write(Path path, Class<T> type, List<? extends T> records) throws DataStorageException {
        List<Field> fields = annotatedFields(type);

        String header = fields.stream()
                .map(field -> escape(field.getAnnotation(CsvColumn.class).value()))
                .collect(Collectors.joining(","));
        StringBuilder csv = new StringBuilder(header).append(System.lineSeparator());

        for (T record : records) {
            List<String> values = new ArrayList<>();
            for (Field field : fields) {
                try {
                    values.add(escape(field.get(record)));
                } catch (IllegalAccessException exception) {
                    throw new DataStorageException(
                            "Не вдалося прочитати поле %s".formatted(field.getName()), exception);
                }
            }
            csv.append(String.join(",", values)).append(System.lineSeparator());
        }

        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, csv.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new DataStorageException("Не вдалося записати CSV: %s".formatted(path), exception);
        }
    }

    /**
     * Повертає поля {@code type}, позначені {@link CsvColumn}, у
     * детермінованому порядку (сортування за іменем поля), перевіривши
     * доступ до кожного.
     */
    private static List<Field> annotatedFields(Class<?> type) throws DataStorageException {
        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvColumn.class))
                .sorted(Comparator.comparing(Field::getName))
                .toList();
        if (fields.isEmpty()) {
            throw new DataStorageException("Клас %s не має CSV-полів".formatted(type.getName()));
        }
        for (Field field : fields) {
            if (!field.trySetAccessible()) {
                throw new DataStorageException("Немає доступу до поля %s".formatted(field.getName()));
            }
        }
        return fields;
    }

    /** Екранує значення за правилами CSV: кому, лапку й перенесення рядка. */
    private static String escape(Object value) {
        String text = Objects.toString(value, "");
        boolean mustQuote = text.contains(",") || text.contains("\"") || text.contains("\r") || text.contains("\n");
        return mustQuote ? "\"%s\"".formatted(text.replace("\"", "\"\"")) : text;
    }
}
