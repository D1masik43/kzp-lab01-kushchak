package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchTest {

    @Test
    void constructorStoresValidFields() {
        Match match = new Match("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals("Динамо", match.getHomeTeam());
        assertEquals("Шахтар", match.getAwayTeam());
        assertEquals(2, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
        assertEquals(18500, match.getAttendance());
        assertEquals(3, match.totalGoals());
    }

    @Test
    void constructorRejectsNullHomeTeam() {
        assertThrows(NullPointerException.class,
                () -> new Match(null, "Шахтар", 2, 1, 18500));
    }

    @Test
    void constructorRejectsBlankAwayTeam() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Match("Динамо", " ", 2, 1, 18500));

        assertTrue(exception.getMessage().contains("порожня назва"));
    }

    @Test
    void constructorRejectsNegativeScore() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Match("Динамо", "Шахтар", -1, 1, 18500));

        assertTrue(exception.getMessage().contains("від'ємне"));
    }

    @Test
    void constructorRejectsNegativeAttendance() {
        assertThrows(IllegalArgumentException.class,
                () -> new Match("Динамо", "Шахтар", 2, 1, -5));
    }

    @Test
    void fromCsvCreatesValidMatch() {
        Match match = Match.fromCsv("Динамо;Шахтар;2;1;18500");

        assertEquals("Динамо", match.getHomeTeam());
        assertEquals(18500, match.getAttendance());
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Match.fromCsv("Динамо;Шахтар;2;1"));

        assertTrue(exception.getMessage().contains("полів"));
    }

    @Test
    void fromCsvRejectsNonNumericField() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Match.fromCsv("Карпати;Верес;два;0;7200"));

        assertTrue(exception.getMessage().contains("помилковий формат"));
    }

    @Test
    void toStringContainsTeamsAndScore() {
        Match match = new Match("Динамо", "Шахтар", 2, 1, 18500);

        String text = match.toString();

        assertTrue(text.contains("Динамо"));
        assertTrue(text.contains("Шахтар"));
        assertTrue(text.contains("2:1"));
    }
}
