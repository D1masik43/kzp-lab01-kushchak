package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * Контрольний набір — вісім рядків {@code data/schedule.csv}: дубльована пара
 * команд «Динамо;Шахтар» (рядки 1 і 6, для перевірки {@code findFirst}), межа
 * фільтра туру 2 (рядки 3-4), чотири матчі з однаковою різницею голів 3, але
 * різними датами (рядки 1, 4, 5, 8, для перевірки другого критерію
 * компаратора), і восьмий запис понад перші п'ять — для перевірки усікання
 * {@code top-5}.
 */
class LeagueScheduleQueriesTest {

    private static final List<ScheduledMatch> SCHEDULE = List.of(
            ScheduledMatch.fromCsv("Динамо;Шахтар;3;0;18500;1;2026-03-01"),
            ScheduledMatch.fromCsv("Карпати;Верес;1;1;7200;1;2026-03-02"),
            ScheduledMatch.fromCsv("Зоря;Металіст;2;2;12800;2;2026-03-08"),
            ScheduledMatch.fromCsv("Оболонь;Полісся;4;1;5400;2;2026-03-09"),
            ScheduledMatch.fromCsv("Рух;Колос;0;3;9600;3;2026-03-15"),
            ScheduledMatch.fromCsv("Динамо;Шахтар;1;1;11000;4;2026-03-22"),
            ScheduledMatch.fromCsv("Верес;Карпати;2;0;6000;4;2026-03-23"),
            ScheduledMatch.fromCsv("Шахтар;Зоря;5;2;15000;5;2026-03-30"));

    @Test
    void matchesOfTourFiltersByExactTour() {
        List<ScheduledMatch> tourTwo = LeagueScheduleQueries.matchesOfTour(SCHEDULE, 2);

        assertEquals(2, tourTwo.size());
        assertTrue(tourTwo.stream().allMatch(scheduled -> scheduled.tour() == 2));
    }

    @Test
    void matchesOfTourReturnsEmptyForUnknownTour() {
        assertTrue(LeagueScheduleQueries.matchesOfTour(SCHEDULE, 99).isEmpty());
    }

    @Test
    void scoresAsTextMapsEachMatchInOrder() {
        List<String> scores = LeagueScheduleQueries.scoresAsText(SCHEDULE);

        assertEquals(List.of("3:0", "1:1", "2:2", "4:1", "0:3", "1:1", "2:0", "5:2"), scores);
    }

    @Test
    void countByTourGroupsAndOrdersKeysAscending() {
        Map<Integer, Long> counts = LeagueScheduleQueries.countByTour(SCHEDULE);

        assertEquals(new TreeMap<>(Map.of(1, 2L, 2, 2L, 3, 1L, 4, 2L, 5, 1L)), counts);
        assertEquals(List.of(1, 2, 3, 4, 5), List.copyOf(counts.keySet()));
    }

    @Test
    void homeGoalsStatisticsAggregatesAcrossAllMatches() {
        IntSummaryStatistics statistics = LeagueScheduleQueries.homeGoalsStatistics(SCHEDULE);

        assertEquals(8, statistics.getCount());
        assertEquals(18, statistics.getSum());
        assertEquals(0, statistics.getMin());
        assertEquals(5, statistics.getMax());
        assertEquals(2.25, statistics.getAverage(), 0.0001);
    }

    @Test
    void homeGoalsStatisticsOnEmptyInputHasZeroCount() {
        IntSummaryStatistics statistics = LeagueScheduleQueries.homeGoalsStatistics(List.of());

        assertEquals(0, statistics.getCount());
        assertEquals(0, statistics.getSum());
    }

    @Test
    void topByGoalDifferenceOrdersByDifferenceDescendingThenDateAscending() {
        List<ScheduledMatch> top = LeagueScheduleQueries.topByGoalDifference(SCHEDULE, 5);

        assertEquals(5, top.size());
        List<String> pairs = top.stream().map(scheduled -> scheduled.homeTeam() + ";" + scheduled.awayTeam()).toList();
        assertEquals(List.of(
                "Динамо;Шахтар", "Оболонь;Полісся", "Рух;Колос", "Шахтар;Зоря", "Верес;Карпати"), pairs);
    }

    @Test
    void topByGoalDifferenceRejectsNegativeLimit() {
        assertThrows(IllegalArgumentException.class, () -> LeagueScheduleQueries.topByGoalDifference(SCHEDULE, -1));
    }

    @Test
    void topByGoalDifferenceWithZeroLimitReturnsEmpty() {
        assertTrue(LeagueScheduleQueries.topByGoalDifference(SCHEDULE, 0).isEmpty());
    }

    @Test
    void topByGoalDifferenceWithLimitLargerThanSizeReturnsAll() {
        assertEquals(SCHEDULE.size(), LeagueScheduleQueries.topByGoalDifference(SCHEDULE, 100).size());
    }

    @Test
    void findByTeamsReturnsFirstMatchOnDuplicateKey() {
        Optional<ScheduledMatch> found = LeagueScheduleQueries.findByTeams(SCHEDULE, "Динамо", "Шахтар");

        assertTrue(found.isPresent());
        assertEquals(LocalDate.of(2026, 3, 1), found.get().date());
    }

    @Test
    void findByTeamsReturnsEmptyWhenPairNotFound() {
        assertFalse(LeagueScheduleQueries.findByTeams(SCHEDULE, "Зоря", "Шахтар").isPresent());
    }

    @Test
    void queriesDoNotMutateInputList() {
        List<ScheduledMatch> snapshot = List.copyOf(SCHEDULE);

        LeagueScheduleQueries.matchesOfTour(SCHEDULE, 1);
        LeagueScheduleQueries.scoresAsText(SCHEDULE);
        LeagueScheduleQueries.countByTour(SCHEDULE);
        LeagueScheduleQueries.homeGoalsStatistics(SCHEDULE);
        LeagueScheduleQueries.topByGoalDifference(SCHEDULE, 5);
        LeagueScheduleQueries.findByTeams(SCHEDULE, "Динамо", "Шахтар");

        assertEquals(snapshot, SCHEDULE);
    }

    @Test
    void queriesHandleMixedMatchSubtypesPolymorphically() {
        List<ScheduledMatch> mixed = List.of(
                new ScheduledMatch(new LeagueMatch("Динамо", "Шахтар", 2, 1, 18500), 1, LocalDate.of(2026, 4, 1)),
                new ScheduledMatch(new PlayoffMatch("Карпати", "Верес", 3, 0, 7200), 1, LocalDate.of(2026, 4, 2)));

        // Жодного if/switch за конкретним підтипом Match — лише делегування через ScheduledMatch.
        List<String> scores = LeagueScheduleQueries.scoresAsText(mixed);

        assertEquals(List.of("2:1", "3:0"), scores);
        assertEquals(2, LeagueScheduleQueries.matchesOfTour(mixed, 1).size());
    }
}
