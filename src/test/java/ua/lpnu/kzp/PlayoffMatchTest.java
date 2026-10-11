package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayoffMatchTest {

    @Test
    void constructorStoresValidFields() {
        PlayoffMatch match = new PlayoffMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals("Динамо", match.getHomeTeam());
        assertEquals(MatchKind.PLAYOFF, match.getKind());
    }

    @Test
    void constructorRejectsDraw() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new PlayoffMatch("Динамо", "Шахтар", 1, 1, 18500));

        assertTrue(exception.getMessage().contains("нічиєю"));
    }

    @Test
    void constructorRejectsBlankHomeTeam() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayoffMatch(" ", "Шахтар", 2, 1, 18500));
    }

    @Test
    void constructorRejectsNegativeAttendance() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayoffMatch("Динамо", "Шахтар", 2, 1, -5));
    }

    @Test
    void fromCsvCreatesValidMatch() {
        PlayoffMatch match = PlayoffMatch.fromCsv("Динамо;Шахтар;2;1;18500");

        assertEquals(2, match.homePoints());
        assertEquals(0, match.awayPoints());
    }

    @Test
    void fromCsvRejectsDraw() {
        assertThrows(IllegalArgumentException.class,
                () -> PlayoffMatch.fromCsv("Динамо;Шахтар;1;1;18500"));
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        assertThrows(IllegalArgumentException.class,
                () -> PlayoffMatch.fromCsv("Динамо;Шахтар;2;1"));
    }

    @Test
    void fromCsvRejectsNonNumericField() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PlayoffMatch.fromCsv("Карпати;Верес;два;0;7200"));

        assertTrue(exception.getMessage().contains("помилковий формат"));
    }

    @Test
    void awayWinnerGetsPoints() {
        PlayoffMatch match = new PlayoffMatch("Динамо", "Шахтар", 0, 2, 18500);

        assertEquals(0, match.homePoints());
        assertEquals(2, match.awayPoints());
    }
}
