package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchRecordTest {

    private static final LocalDate DATE = LocalDate.of(2026, 4, 1);

    @Test
    void constructorStoresValidFields() {
        MatchRecord match = new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 3);

        assertEquals("Динамо", match.homeTeam());
        assertEquals("Шахтар", match.awayTeam());
        assertEquals(DATE, match.date());
        assertEquals(2, match.homeScore());
        assertEquals(1, match.awayScore());
        assertEquals(3, match.round());
    }

    @Test
    void constructorRejectsNullDate() {
        assertThrows(NullPointerException.class, () -> new MatchRecord("Динамо", "Шахтар", null, 2, 1, 3));
    }

    @Test
    void constructorRejectsBlankAwayTeam() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MatchRecord("Динамо", " ", DATE, 2, 1, 3));

        assertTrue(exception.getMessage().contains("порожня назва"));
    }

    @Test
    void constructorRejectsNegativeScore() {
        assertThrows(IllegalArgumentException.class, () -> new MatchRecord("Динамо", "Шахтар", DATE, -1, 1, 3));
    }

    @Test
    void constructorRejectsRoundLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 0));
    }

    @Test
    void fromFieldsParsesSortedFieldOrder() throws DataStorageException {
        // Порядок полів відповідає експорту CsvExporter: awayScore, awayTeam, date, homeScore, homeTeam, round.
        MatchRecord match = MatchRecord.fromFields(List.of("1", "Шахтар", "2026-04-01", "2", "Динамо", "3"));

        assertEquals(new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 3), match);
    }

    @Test
    void fromCsvParsesLineInExportFormat() throws DataStorageException {
        MatchRecord match = MatchRecord.fromCsv("1,Шахтар,2026-04-01,2,Динамо,3");

        assertEquals(2, match.homeScore());
        assertEquals(1, match.awayScore());
    }

    @Test
    void fromFieldsRejectsWrongFieldCount() {
        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> MatchRecord.fromFields(List.of("1", "Шахтар", "2026-04-01", "2", "Динамо")));

        assertTrue(exception.getMessage().contains("полів"));
    }

    @Test
    void fromFieldsRejectsNonNumericScore() {
        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> MatchRecord.fromFields(List.of("один", "Шахтар", "2026-04-01", "2", "Динамо", "3")));

        assertTrue(exception.getMessage().contains("помилковий формат"));
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    @Test
    void fromFieldsRejectsInvalidDate() {
        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> MatchRecord.fromFields(List.of("1", "Шахтар", "2026-13-99", "2", "Динамо", "3")));

        assertTrue(exception.getMessage().contains("дата"));
    }

    @Test
    void equalMatchesHaveSameHashCode() {
        MatchRecord first = new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 3);
        MatchRecord second = new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 3);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentRoundsAreNotEqual() {
        MatchRecord first = new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 3);
        MatchRecord second = new MatchRecord("Динамо", "Шахтар", DATE, 2, 1, 4);

        assertNotEquals(first, second);
    }
}
