package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Описує коректний результат одного матчу ліги (варіант 17, «Спортивна ліга»).
 *
 * <p>Інваріанти: назви команд не порожні; рахунок і відвідуваність невід'ємні.
 */
public final class Match {

    private static final int FIELD_COUNT = 5;

    private final String homeTeam;
    private final String awayTeam;
    private final int homeScore;
    private final int awayScore;
    private final int attendance;

    /**
     * Створює коректний результат матчу, перевіряючи інваріанти предметної області.
     *
     * @param homeTeam команда господарів
     * @param awayTeam команда гостей
     * @param homeScore голи господарів
     * @param awayScore голи гостей
     * @param attendance кількість глядачів на матчі
     * @throws NullPointerException якщо назва команди відсутня
     * @throws IllegalArgumentException якщо назва команди порожня або число від'ємне
     */
    public Match(String homeTeam, String awayTeam, int homeScore, int awayScore, int attendance) {
        this.homeTeam = Objects.requireNonNull(homeTeam, "Назва команди господарів не може бути null");
        this.awayTeam = Objects.requireNonNull(awayTeam, "Назва команди гостей не може бути null");
        // Порожній текст і відсутнє значення мають різні причини відхилення.
        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            throw new IllegalArgumentException("порожня назва команди");
        }
        if (homeScore < 0 || awayScore < 0 || attendance < 0) {
            throw new IllegalArgumentException("від'ємне числове значення");
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.attendance = attendance;
    }

    /**
     * Створює результат матчу з рядка CSV {@code home;away;homeScore;awayScore;attendance}.
     *
     * <p>Фабрика відповідає лише за структуру й типи полів, конструктор — за інваріанти.
     *
     * @param line рядок у форматі варіанта
     * @return створений результат матчу
     * @throws IllegalArgumentException якщо структура рядка або число в ньому некоректні
     */
    public static Match fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("очікується %d полів".formatted(FIELD_COUNT));
        }
        try {
            int homeScore = Integer.parseInt(fields[2].trim());
            int awayScore = Integer.parseInt(fields[3].trim());
            int attendance = Integer.parseInt(fields[4].trim());
            return new Match(fields[0].trim(), fields[1].trim(), homeScore, awayScore, attendance);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("числове поле має помилковий формат", exception);
        }
    }

    /** Повертає назву команди господарів. */
    public String getHomeTeam() {
        return homeTeam;
    }

    /** Повертає назву команди гостей. */
    public String getAwayTeam() {
        return awayTeam;
    }

    /** Повертає кількість голів господарів. */
    public int getHomeScore() {
        return homeScore;
    }

    /** Повертає кількість голів гостей. */
    public int getAwayScore() {
        return awayScore;
    }

    /** Повертає кількість глядачів на матчі. */
    public int getAttendance() {
        return attendance;
    }

    /** Повертає сумарну кількість голів обох команд у матчі. */
    public int totalGoals() {
        return homeScore + awayScore;
    }

    /** Повертає текстове подання результату матчу для звіту/налагодження. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s %d:%d %s (%d глядачів)",
                homeTeam, homeScore, awayScore, awayTeam, attendance);
    }
}
