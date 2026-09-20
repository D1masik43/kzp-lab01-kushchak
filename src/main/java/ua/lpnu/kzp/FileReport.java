package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Ізолює кросплатформне читання та запис текстових файлів. */
public final class FileReport {

    private FileReport() {
    }

    /**
     * Читає рядки у фіксованому кодуванні UTF-8.
     *
     * @param input шлях до вхідного файла
     * @return рядки файла
     * @throws IOException якщо файл неможливо прочитати
     */
    public static List<String> readLines(Path input) throws IOException {
        return Files.readAllLines(input, StandardCharsets.UTF_8);
    }

    /**
     * Створює каталог призначення і записує звіт.
     *
     * @param output шлях до файла звіту
     * @param report готовий текст звіту
     * @throws IOException якщо каталог або файл неможливо створити
     */
    public static void writeReport(Path output, String report) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(output, report, StandardCharsets.UTF_8);
    }
}
