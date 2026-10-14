package ua.lpnu.kzp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Плоский запис матчу варіанта 17 «Спортивна ліга», придатний для
 * експорту в CSV через {@link CsvExporter} і {@link CsvColumn}. Незалежний
 * від ієрархії {@link Match}/{@link LeagueMatch}/{@link PlayoffMatch} лаб.
 * 1-3 і композиції {@link ScheduledMatch} лаб. 4 — лаб. 5 додає окремий шар
 * збереження (сховище, анотація, рефлексивний експорт), не змінюючи
 * попередню модель.
 *
 * <p>{@link CsvExporter} визначає порядок стовпців сортуванням імен
 * Java-полів, а не порядком їх оголошення тут, тому фактичний порядок
 * стовпців у файлі — {@code awayScore, awayTeam, date, homeScore, homeTeam,
 * round} (докладніше — {@code REPORT.md}, розділ 6).
 */
public final class MatchRecord {

    private static final int FIELD_COUNT = 6;

    @CsvColumn("Господарі")
    private final String homeTeam;

    @CsvColumn("Гості")
    private final String awayTeam;

    @CsvColumn("Дата")
    private final LocalDate date;

    @CsvColumn("Рахунок господарів")
    private final int homeScore;

    @CsvColumn("Рахунок гостей")
    private final int awayScore;

    @CsvColumn("Тур")
    private final int round;

    /**
     * Створює перевірений запис матчу.
     *
     * @param homeTeam команда господарів
     * @param awayTeam команда гостей
     * @param date дата проведення матчу
     * @param homeScore голи господарів
     * @param awayScore голи гостей
     * @param round номер туру (не менше 1)
     * @throws NullPointerException якщо {@code homeTeam}, {@code awayTeam} чи {@code date} дорівнюють {@code null}
     * @throws IllegalArgumentException якщо назва команди порожня, рахунок від'ємний або {@code round} менше 1
     */
    public MatchRecord(String homeTeam, String awayTeam, LocalDate date, int homeScore, int awayScore, int round) {
        this.homeTeam = Objects.requireNonNull(homeTeam, "Назва команди господарів не може бути null");
        this.awayTeam = Objects.requireNonNull(awayTeam, "Назва команди гостей не може бути null");
        this.date = Objects.requireNonNull(date, "Дата не може бути null");
        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            throw new IllegalArgumentException("порожня назва команди");
        }
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("від'ємне числове значення");
        }
        if (round < 1) {
            throw new IllegalArgumentException("тур має бути не менше 1");
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.round = round;
    }

    public String homeTeam() {
        return homeTeam;
    }

    public String awayTeam() {
        return awayTeam;
    }

    public LocalDate date() {
        return date;
    }

    public int homeScore() {
        return homeScore;
    }

    public int awayScore() {
        return awayScore;
    }

    public int round() {
        return round;
    }

    /**
     * Створює запис з одного CSV-рядка (формат — вивід {@link CsvExporter}
     * для цього класу: {@code awayScore,awayTeam,date,homeScore,homeTeam,round}).
     *
     * @param line один CSV-рядок
     * @return розібраний запис
     * @throws DataStorageException якщо структура рядка, число або дата некоректні
     */
    public static MatchRecord fromCsv(String line) throws DataStorageException {
        return fromFields(CsvParser.parse(line));
    }

    /**
     * Створює запис з уже розібраних CSV-полів у порядку експорту
     * {@link CsvExporter} ({@code awayScore, awayTeam, date, homeScore,
     * homeTeam, round} — за зростанням імені Java-поля).
     *
     * @param fields поля одного CSV-запису
     * @return розібраний запис
     * @throws DataStorageException якщо кількість полів, число або дата некоректні
     */
    public static MatchRecord fromFields(List<String> fields) throws DataStorageException {
        if (fields.size() != FIELD_COUNT) {
            throw new DataStorageException("Очікується %d полів".formatted(FIELD_COUNT));
        }

        int awayScore;
        int homeScore;
        int round;
        try {
            awayScore = Integer.parseInt(fields.get(0).trim());
            homeScore = Integer.parseInt(fields.get(3).trim());
            round = Integer.parseInt(fields.get(5).trim());
        } catch (NumberFormatException exception) {
            throw new DataStorageException("числове поле має помилковий формат", exception);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(fields.get(2).trim());
        } catch (DateTimeParseException exception) {
            throw new DataStorageException("дата має помилковий формат", exception);
        }

        String awayTeam = fields.get(1);
        String homeTeam = fields.get(4);
        try {
            return new MatchRecord(homeTeam, awayTeam, date, homeScore, awayScore, round);
        } catch (IllegalArgumentException exception) {
            throw new DataStorageException(exception.getMessage(), exception);
        }
    }

    /** Порівнює записи матчів за всіма полями. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MatchRecord match)) {
            return false;
        }
        return homeScore == match.homeScore
                && awayScore == match.awayScore
                && round == match.round
                && homeTeam.equals(match.homeTeam)
                && awayTeam.equals(match.awayTeam)
                && date.equals(match.date);
    }

    /** Повертає хеш-код, узгоджений із {@code equals}. */
    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam, date, homeScore, awayScore, round);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s %d:%d %s, тур %d, %s",
                homeTeam, homeScore, awayScore, awayTeam, round, date);
    }
}
