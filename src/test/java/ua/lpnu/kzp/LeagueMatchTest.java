package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LeagueMatchTest {

    @Test
    void constructorStoresValidFields() {
        LeagueMatch match = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals("Динамо", match.getHomeTeam());
        assertEquals("Шахтар", match.getAwayTeam());
        assertEquals(2, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
        assertEquals(18500, match.getAttendance());
        assertEquals(3, match.totalGoals());
        assertEquals(MatchKind.LEAGUE, match.getKind());
    }

    @Test
    void constructorRejectsNullHomeTeam() {
        assertThrows(NullPointerException.class,
                () -> new LeagueMatch(null, "Шахтар", 2, 1, 18500));
    }

    @Test
    void constructorRejectsBlankAwayTeam() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LeagueMatch("Динамо", " ", 2, 1, 18500));

        assertTrue(exception.getMessage().contains("порожня назва"));
    }

    @Test
    void constructorRejectsNegativeScore() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LeagueMatch("Динамо", "Шахтар", -1, 1, 18500));

        assertTrue(exception.getMessage().contains("від'ємне"));
    }

    @Test
    void constructorRejectsNegativeAttendance() {
        assertThrows(IllegalArgumentException.class,
                () -> new LeagueMatch("Динамо", "Шахтар", 2, 1, -5));
    }

    @Test
    void constructorAllowsDraw() {
        LeagueMatch draw = new LeagueMatch("Карпати", "Верес", 1, 1, 7200);

        assertEquals(1, draw.homePoints());
        assertEquals(1, draw.awayPoints());
    }

    @Test
    void fromCsvCreatesValidMatch() {
        LeagueMatch match = LeagueMatch.fromCsv("Динамо;Шахтар;2;1;18500");

        assertEquals("Динамо", match.getHomeTeam());
        assertEquals(18500, match.getAttendance());
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> LeagueMatch.fromCsv("Динамо;Шахтар;2;1"));

        assertTrue(exception.getMessage().contains("полів"));
    }

    @Test
    void fromCsvRejectsNonNumericField() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> LeagueMatch.fromCsv("Карпати;Верес;два;0;7200"));

        assertTrue(exception.getMessage().contains("помилковий формат"));
    }

    @Test
    void winnerGetsThreePointsLoserGetsZero() {
        LeagueMatch match = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals(3, match.homePoints());
        assertEquals(0, match.awayPoints());
    }

    @Test
    void toStringContainsTeamsScoreAndKind() {
        LeagueMatch match = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);

        String text = match.toString();

        assertTrue(text.contains("Динамо"));
        assertTrue(text.contains("2:1"));
        assertTrue(text.contains(MatchKind.LEAGUE.label()));
    }
}
