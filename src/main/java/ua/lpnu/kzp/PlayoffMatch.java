package ua.lpnu.kzp;

/**
 * Матч плей-офф. Раунд на виліт виключає нічию; переможець отримує 2 очки,
 * переможений — 0.
 */
public final class PlayoffMatch extends Match {

    private static final int POINTS_WIN = 2;
    private static final int POINTS_LOSS = 0;

    /**
     * Створює матч плей-офф, перевіряючи спільні інваріанти та відсутність нічиєї.
     *
     * @param homeTeam команда господарів
     * @param awayTeam команда гостей
     * @param homeScore голи господарів
     * @param awayScore голи гостей
     * @param attendance кількість глядачів
     * @throws IllegalArgumentException якщо рахунок рівний (нічия в плей-офф неможлива)
     */
    public PlayoffMatch(String homeTeam, String awayTeam, int homeScore, int awayScore, int attendance) {
        super(homeTeam, awayTeam, homeScore, awayScore, attendance);
        if (homeScore == awayScore) {
            throw new IllegalArgumentException("матч плей-офф не може закінчитися нічиєю");
        }
    }

    /**
     * Створює матч плей-офф з рядка CSV {@code home;away;homeScore;awayScore;attendance}.
     *
     * @param line рядок у форматі варіанта
     * @return створений матч
     * @throws IllegalArgumentException якщо структура рядка, число або нічия некоректні
     */
    public static PlayoffMatch fromCsv(String line) {
        String[] fields = splitFields(line);
        try {
            int homeScore = Integer.parseInt(fields[2].trim());
            int awayScore = Integer.parseInt(fields[3].trim());
            int attendance = Integer.parseInt(fields[4].trim());
            return new PlayoffMatch(fields[0].trim(), fields[1].trim(), homeScore, awayScore, attendance);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("числове поле має помилковий формат", exception);
        }
    }

    @Override
    public MatchKind getKind() {
        return MatchKind.PLAYOFF;
    }

    /** Переможець отримує 2 очки, переможений — 0. */
    @Override
    public int homePoints() {
        return getHomeScore() > getAwayScore() ? POINTS_WIN : POINTS_LOSS;
    }

    /** Переможець отримує 2 очки, переможений — 0. */
    @Override
    public int awayPoints() {
        return getAwayScore() > getHomeScore() ? POINTS_WIN : POINTS_LOSS;
    }
}
