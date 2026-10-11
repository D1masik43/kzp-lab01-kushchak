package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Перевіряє поведінку спільну для всіх підтипів {@link Match}: рівність і поліморфізм. */
class MatchHierarchyTest {

    @Test
    void equalLeagueMatchesHaveSameHashCode() {
        LeagueMatch first = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);
        LeagueMatch second = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void hashSetDeduplicatesEqualMatches() {
        Set<Match> matches = new HashSet<>();
        matches.add(new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500));
        matches.add(new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500));

        assertEquals(1, matches.size());
    }

    @Test
    void differentSubtypesWithSameFieldsAreNotEqual() {
        LeagueMatch league = new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500);
        PlayoffMatch playoff = new PlayoffMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertNotEquals(league, playoff);
    }

    @Test
    void subtypesCalculatePointsDifferently() {
        LeagueMatch league = new LeagueMatch("Динамо", "Шахтар", 1, 1, 7200);
        PlayoffMatch playoff = new PlayoffMatch("Динамо", "Шахтар", 2, 1, 18500);

        assertEquals(1, league.homePoints());
        assertEquals(2, playoff.homePoints());
    }

    @Test
    void polymorphicCollectionSumsPointsWithoutCheckingSubtype() {
        List<Match> matches = List.of(
                new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500),
                new PlayoffMatch("Карпати", "Верес", 0, 2, 7200));

        int totalHomePoints = 0;
        for (Match match : matches) {
            // Жодного if/switch за конкретним підтипом — лише виклик через базовий тип.
            totalHomePoints += match.homePoints();
        }

        assertEquals(3, totalHomePoints);
    }

    @Test
    void getKindReflectsSubtype() {
        assertEquals(MatchKind.LEAGUE, new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500).getKind());
        assertEquals(MatchKind.PLAYOFF, new PlayoffMatch("Динамо", "Шахтар", 2, 1, 18500).getKind());
        assertEquals("ліговий", MatchKind.LEAGUE.label());
        assertEquals("плей-офф", MatchKind.PLAYOFF.label());
    }
}
