package ua.lpnu.kzp;

/**
 * Повідомляє про помилку зберігання, CSV-формату або рефлексії: хибну
 * кількість полів, хибне число чи дату, брак доступу до анотованого поля,
 * помилку файлової системи тощо. Завжди або передається з причиною
 * ({@link #DataStorageException(String, Throwable)}), або формулює власну,
 * самодостатню причину — порожній {@code catch} не приховує її мовчки.
 */
public class DataStorageException extends Exception {

    /**
     * Створює виняток із повідомленням без першопричини (наприклад, коли
     * формат рядка сам по собі порушений, а не викликав іншу помилку).
     *
     * @param message опис операції й причини відмови
     */
    public DataStorageException(String message) {
        super(message);
    }

    /**
     * Створює виняток із повідомленням і технічною першопричиною
     * ({@link java.io.IOException}, {@link NumberFormatException},
     * {@link ReflectiveOperationException} тощо), яку повертає {@link #getCause()}.
     *
     * @param message опис операції, що не вдалася
     * @param cause технічна причина відмови
     */
    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
