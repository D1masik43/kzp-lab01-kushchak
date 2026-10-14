package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvExporterTest {

    private static final LocalDate DATE_1 = LocalDate.of(2026, 4, 1);
    private static final LocalDate DATE_2 = LocalDate.of(2026, 4, 8);

    @Test
    void headerColumnsAreOrderedByJavaFieldName(@TempDir Path tempDir) throws DataStorageException, IOException {
        Path path = tempDir.resolve("matches.csv");
        CsvExporter.write(path, MatchRecord.class, List.of());

        String header = Files.readAllLines(path, StandardCharsets.UTF_8).get(0);

        // Порядок стовпців = сортування імен Java-полів: awayScore, awayTeam, date, homeScore, homeTeam, round.
        assertEquals("Рахунок гостей,Гості,Дата,Рахунок господарів,Господарі,Тур", header);
    }

    @Test
    void writeEmptyListProducesHeaderOnly(@TempDir Path tempDir) throws DataStorageException, IOException {
        Path path = tempDir.resolve("matches.csv");
        CsvExporter.write(path, MatchRecord.class, List.of());

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
    }

    @Test
    void roundTripRestoresEqualObjectsIncludingEscapedField(@TempDir Path tempDir)
            throws DataStorageException, IOException {
        List<MatchRecord> original = List.of(
                new MatchRecord("Динамо", "Шахтар", DATE_1, 2, 1, 1),
                new MatchRecord("Карпати, Львів", "Верес \"Б\"", DATE_2, 0, 0, 1),
                new MatchRecord("Зоря", "Металіст", DATE_1, 3, 2, 2));

        Path path = tempDir.resolve("matches.csv");
        CsvExporter.write(path, MatchRecord.class, original);

        String csv = Files.readString(path, StandardCharsets.UTF_8);
        List<List<String>> rows = CsvParser.parseDocument(csv);
        List<MatchRecord> restored = rows.stream()
                .skip(1)
                .map(fields -> {
                    try {
                        return MatchRecord.fromFields(fields);
                    } catch (DataStorageException exception) {
                        throw new IllegalStateException(exception);
                    }
                })
                .toList();

        assertEquals(4, rows.size());
        assertEquals(original, restored);
        assertTrue(csv.contains("\"Карпати, Львів\""));
    }

    @Test
    void writeThrowsWhenClassHasNoCsvColumns(@TempDir Path tempDir) {
        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> CsvExporter.write(tempDir.resolve("plain.csv"), PlainClass.class, List.of(new PlainClass())));

        assertTrue(exception.getMessage().contains("CSV-полів"));
    }

    @Test
    void writeWrapsFileSystemErrorWithCause(@TempDir Path tempDir) throws IOException {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "не директорія", StandardCharsets.UTF_8);
        Path blockedTarget = blocker.resolve("matches.csv");

        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> CsvExporter.write(blockedTarget, MatchRecord.class, List.of()));

        assertTrue(exception.getCause() instanceof IOException);
    }

    /** Клас без анотованих полів — лише для перевірки помилки експортера. */
    static final class PlainClass {
    }
}
