package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ScheduledMatchTest {

    @Test
    void fromCsvParsesValidRow() {
        ScheduledMatch scheduled = ScheduledMatch.fromCsv("Динамо;Шахтар;3;0;18500;1;2026-03-01");

        assertEquals("Динамо", scheduled.homeTeam());
        assertEquals("Шахтар", scheduled.awayTeam());
        assertEquals(3, scheduled.homeScore());
        assertEquals(0, scheduled.awayScore());
        assertEquals(1, scheduled.tour());
        assertEquals(LocalDate.of(2026, 3, 1), scheduled.date());
        assertEquals(3, scheduled.goalDifference());
    }

    @Test
    void goalDifferenceIsAbsoluteValue() {
        ScheduledMatch scheduled = ScheduledMatch.fromCsv("Рух;Колос;0;3;9600;3;2026-03-15");

        assertEquals(3, scheduled.goalDifference());
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ScheduledMatch.fromCsv("Динамо;Шахтар;3;0;18500;1"));

        assertTrue(exception.getMessage().contains("полів"));
    }

    @Test
    void fromCsvRejectsNonNumericTour() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ScheduledMatch.fromCsv("Динамо;Шахтар;3;0;18500;перший;2026-03-01"));

        assertTrue(exception.getMessage().contains("тур"));
    }

    @Test
    void fromCsvRejectsInvalidDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ScheduledMatch.fromCsv("Динамо;Шахтар;3;0;18500;1;2026-13-99"));

        assertTrue(exception.getMessage().contains("дата"));
    }

    @Test
    void fromCsvDelegatesBaseValidationToLeagueMatch() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ScheduledMatch.fromCsv("Динамо;Шахтар;два;0;18500;1;2026-03-01"));

        assertTrue(exception.getMessage().contains("помилковий формат"));
    }

    @Test
    void constructorRejectsTourLessThanOne() {
        LeagueMatch match = new LeagueMatch("Динамо", "Шахтар", 3, 0, 18500);

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledMatch(match, 0, LocalDate.of(2026, 3, 1)));
    }
}
