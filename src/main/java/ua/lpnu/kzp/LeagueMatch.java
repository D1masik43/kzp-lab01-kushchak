package ua.lpnu.kzp;

/**
 * Матч регулярного сезону ліги. Нічия дозволена; очки нараховуються за
 * стандартною футбольною схемою «перемога 3 – нічия 1 – поразка 0».
 */
public final class LeagueMatch extends Match {

    private static final int POINTS_WIN = 3;
    private static final int POINTS_DRAW = 1;
    private static final int POINTS_LOSS = 0;

    /**
     * Створює матч регулярного сезону, перевіряючи спільні інваріанти.
     *
     * @param homeTeam команда господарів
     * @param awayTeam команда гостей
     * @param homeScore голи господарів
     * @param awayScore голи гостей
     * @param attendance кількість глядачів
     */
    public LeagueMatch(String homeTeam, String awayTeam, int homeScore, int awayScore, int attendance) {
        super(homeTeam, awayTeam, homeScore, awayScore, attendance);
    }

    /**
     * Створює матч регулярного сезону з рядка CSV
     * {@code home;away;homeScore;awayScore;attendance}.
     *
     * @param line рядок у форматі варіанта
     * @return створений матч
     * @throws IllegalArgumentException якщо структура рядка або число в ньому некоректні
     */
    public static LeagueMatch fromCsv(String line) {
        String[] fields = splitFields(line);
        try {
            int homeScore = Integer.parseInt(fields[2].trim());
            int awayScore = Integer.parseInt(fields[3].trim());
            int attendance = Integer.parseInt(fields[4].trim());
            return new LeagueMatch(fields[0].trim(), fields[1].trim(), homeScore, awayScore, attendance);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("числове поле має помилковий формат", exception);
        }
    }

    @Override
    public MatchKind getKind() {
        return MatchKind.LEAGUE;
    }

    /** Нараховує 3 очки за перемогу, 1 за нічию, 0 за поразку. */
    @Override
    public int homePoints() {
        if (getHomeScore() > getAwayScore()) {
            return POINTS_WIN;
        }
        return getHomeScore() == getAwayScore() ? POINTS_DRAW : POINTS_LOSS;
    }

    /** Нараховує 3 очки за перемогу, 1 за нічию, 0 за поразку. */
    @Override
    public int awayPoints() {
        if (getAwayScore() > getHomeScore()) {
            return POINTS_WIN;
        }
        return getHomeScore() == getAwayScore() ? POINTS_DRAW : POINTS_LOSS;
    }
}
