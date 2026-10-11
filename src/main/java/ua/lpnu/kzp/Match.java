package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Спільний базовий тип результату матчу ліги (варіант 17, «Спортивна ліга»).
 *
 * <p>Інваріанти: назви команд не порожні; рахунок і відвідуваність невід'ємні.
 * Підтипи {@link LeagueMatch} і {@link PlayoffMatch} надають власну реалізацію
 * нарахування очок і, за потреби, додають власні інваріанти (наприклад,
 * заборону нічиєї в плей-офф).
 */
public abstract class Match {

    private static final int FIELD_COUNT = 5;

    private final String homeTeam;
    private final String awayTeam;
    private final int homeScore;
    private final int awayScore;
    private final int attendance;

    /**
     * Перевіряє спільні інваріанти й зберігає стан матчу.
     *
     * @param homeTeam команда господарів
     * @param awayTeam команда гостей
     * @param homeScore голи господарів
     * @param awayScore голи гостей
     * @param attendance кількість глядачів на матчі
     * @throws NullPointerException якщо назва команди відсутня
     * @throws IllegalArgumentException якщо назва команди порожня або число від'ємне
     */
    protected Match(String homeTeam, String awayTeam, int homeScore, int awayScore, int attendance) {
        this.homeTeam = Objects.requireNonNull(homeTeam, "Назва команди господарів не може бути null");
        this.awayTeam = Objects.requireNonNull(awayTeam, "Назва команди гостей не може бути null");
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
     * Розбиває рядок CSV на поля варіанта й перевіряє їх кількість.
     *
     * <p>Спільна для всіх підтипів перевірка структури рядка; кожен підтип
     * далі сам перетворює числові поля й створює свій об'єкт.
     *
     * @param line рядок CSV у форматі {@code home;away;homeScore;awayScore;attendance}
     * @return масив із 5 полів запису
     * @throws IllegalArgumentException якщо кількість полів не відповідає формату
     */
    protected static String[] splitFields(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("очікується %d полів".formatted(FIELD_COUNT));
        }
        return fields;
    }

    /** Повертає назву команди господарів. */
    public final String getHomeTeam() {
        return homeTeam;
    }

    /** Повертає назву команди гостей. */
    public final String getAwayTeam() {
        return awayTeam;
    }

    /** Повертає кількість голів господарів. */
    public final int getHomeScore() {
        return homeScore;
    }

    /** Повертає кількість голів гостей. */
    public final int getAwayScore() {
        return awayScore;
    }

    /** Повертає кількість глядачів на матчі. */
    public final int getAttendance() {
        return attendance;
    }

    /** Повертає сумарну кількість голів обох команд у матчі. */
    public final int totalGoals() {
        return homeScore + awayScore;
    }

    /** Повертає категорію матчу. */
    public abstract MatchKind getKind();

    /** Обчислює очки, які отримує команда господарів за результат матчу. */
    public abstract int homePoints();

    /** Обчислює очки, які отримує команда гостей за результат матчу. */
    public abstract int awayPoints();

    /**
     * Порівнює матчі за підтипом і значеннями полів.
     *
     * <p>{@code getClass()} (а не {@code instanceof}) не дає визнати рівними
     * об'єкти різних підтипів із випадково однаковими полями.
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Match match = (Match) other;
        return homeScore == match.homeScore
                && awayScore == match.awayScore
                && attendance == match.attendance
                && homeTeam.equals(match.homeTeam)
                && awayTeam.equals(match.awayTeam);
    }

    /** Повертає хеш-код, узгоджений із {@link #equals(Object)}. */
    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), homeTeam, awayTeam, homeScore, awayScore, attendance);
    }

    /** Повертає текстове подання результату матчу для звіту й налагодження. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s %d:%d %s (%d глядачів, %s)",
                homeTeam, homeScore, awayScore, awayTeam, attendance, getKind().label());
    }
}
