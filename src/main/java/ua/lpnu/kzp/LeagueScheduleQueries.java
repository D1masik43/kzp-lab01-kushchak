package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * П'ять Stream-запитів варіанта 17 лабораторної роботи № 4 над розкладом
 * матчів ({@link ScheduledMatch}), плюс окремий пошук із {@link Optional}.
 * Кожен метод — самостійний, перевірюваний конвеєр {@code source → проміжні
 * операції → кінцева операція} без ручного циклу і без розгалуження за
 * конкретним підтипом {@link Match}; жоден метод не форматує консоль і не
 * змінює вхідну колекцію.
 */
public final class LeagueScheduleQueries {

    private LeagueScheduleQueries() {
    }

    /** Запит 1 ({@code filter}): відбирає матчі заданого туру. */
    public static List<ScheduledMatch> matchesOfTour(List<ScheduledMatch> matches, int tour) {
        return matches.stream()
                .filter(scheduled -> scheduled.tour() == tour)
                .toList();
    }

    /** Запит 2 ({@code map}): перетворює рахунок кожного матчу в текст {@code "H:A"}. */
    public static List<String> scoresAsText(List<ScheduledMatch> matches) {
        return matches.stream()
                .map(scheduled -> "%d:%d".formatted(scheduled.homeScore(), scheduled.awayScore()))
                .toList();
    }

    /**
     * Запит 3 ({@code groupingBy}): кількість матчів за туром. Ключі
     * впорядковані зростанням номера туру ({@link TreeMap}), бо звичайна
     * {@code HashMap} не гарантує порядок ключів.
     */
    public static Map<Integer, Long> countByTour(List<ScheduledMatch> matches) {
        return matches.stream()
                .collect(Collectors.groupingBy(ScheduledMatch::tour, TreeMap::new, Collectors.counting()));
    }

    /**
     * Запит 4 ({@code summarizingInt}): зведена статистика голів господарів.
     * Для порожнього входу {@code getCount()} дорівнює 0, а {@code getMin()}/
     * {@code getMax()} набувають {@link Integer#MAX_VALUE}/
     * {@link Integer#MIN_VALUE} відповідно до контракту {@link IntSummaryStatistics}.
     */
    public static IntSummaryStatistics homeGoalsStatistics(List<ScheduledMatch> matches) {
        return matches.stream().collect(Collectors.summarizingInt(ScheduledMatch::homeScore));
    }

    /**
     * Запит 5 (складений компаратор + {@code sorted} і {@code limit}): перші
     * {@code limit} матчів за спаданням різниці голів, а за рівності — за
     * зростанням дати. {@code reversed()} застосовано лише до першого критерію
     * до додавання {@code thenComparing}, інакше він змінив би напрямок обох.
     *
     * @throws IllegalArgumentException якщо {@code limit} від'ємний
     */
    public static List<ScheduledMatch> topByGoalDifference(List<ScheduledMatch> matches, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit не може бути від'ємним");
        }
        Comparator<ScheduledMatch> byDifferenceThenDate = Comparator
                .comparingInt(ScheduledMatch::goalDifference)
                .reversed()
                .thenComparing(ScheduledMatch::date);
        return matches.stream()
                .sorted(byDifferenceThenDate)
                .limit(limit)
                .toList();
    }

    /**
     * Окремий пошук ({@code Optional}): перший матч за точною парою команд
     * господарів і гостей. За дубльованого ключа повертає перший запис у
     * вхідному порядку колекції.
     */
    public static Optional<ScheduledMatch> findByTeams(
            List<ScheduledMatch> matches, String homeTeam, String awayTeam) {
        return matches.stream()
                .filter(scheduled -> scheduled.homeTeam().equals(homeTeam) && scheduled.awayTeam().equals(awayTeam))
                .findFirst();
    }
}
