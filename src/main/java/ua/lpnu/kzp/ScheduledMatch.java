package ua.lpnu.kzp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Матч ліги, прив'язаний до туру й дати — поля варіанта 17, потрібні лише для
 * Stream-запитів лабораторної роботи № 4. Композиція над {@link Match}, а не
 * зміна самої ієрархії: {@code data/input.csv} і конвеєр {@link LeagueProcessor}
 * (лаб. 1-3) лишаються незмінними, бо методика вимагає зберегти їхній формат.
 */
public final class ScheduledMatch {

    private static final int FIELD_COUNT = 7;

    private final Match match;
    private final int tour;
    private final LocalDate date;

    /**
     * Створює запланований матч, перевіряючи інваріанти тура й дати.
     *
     * @param match матч (будь-який підтип {@link Match})
     * @param tour номер туру (не менше 1)
     * @param date дата проведення матчу
     * @throws IllegalArgumentException якщо {@code tour} менше 1
     */
    public ScheduledMatch(Match match, int tour, LocalDate date) {
        this.match = Objects.requireNonNull(match, "Матч не може бути null");
        this.date = Objects.requireNonNull(date, "Дата не може бути null");
        if (tour < 1) {
            throw new IllegalArgumentException("тур має бути не менше 1");
        }
        this.tour = tour;
    }

    public Match match() {
        return match;
    }

    public int tour() {
        return tour;
    }

    public LocalDate date() {
        return date;
    }

    public String homeTeam() {
        return match.getHomeTeam();
    }

    public String awayTeam() {
        return match.getAwayTeam();
    }

    public int homeScore() {
        return match.getHomeScore();
    }

    public int awayScore() {
        return match.getAwayScore();
    }

    /** Абсолютна різниця голів матчу — величина переваги переможця (константа варіанта для top-N). */
    public int goalDifference() {
        return Math.abs(match.getHomeScore() - match.getAwayScore());
    }

    /**
     * Створює запланований матч ліги з рядка CSV
     * {@code home;away;homeScore;awayScore;attendance;tour;date}, де
     * {@code date} подано у форматі ISO-8601 ({@code yyyy-MM-dd}). Перші п'ять
     * полів розбирає {@link LeagueMatch#fromCsv(String)} — та сама перевірка й
     * ті самі тексти помилок, що й у лаб. 1-3.
     *
     * @param line рядок у форматі варіанта лаб. 4
     * @return створений запланований матч
     * @throws IllegalArgumentException якщо структура рядка, число або дата некоректні
     */
    public static ScheduledMatch fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("очікується %d полів".formatted(FIELD_COUNT));
        }

        LeagueMatch match = LeagueMatch.fromCsv(
                String.join(";", fields[0], fields[1], fields[2], fields[3], fields[4]));

        int tour;
        try {
            tour = Integer.parseInt(fields[5].trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("тур має помилковий формат", exception);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(fields[6].trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("дата має помилковий формат", exception);
        }

        return new ScheduledMatch(match, tour, date);
    }
}
