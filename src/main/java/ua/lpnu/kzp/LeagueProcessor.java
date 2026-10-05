package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Перевіряє й обчислює показники для записів варіанта 17 «Спортивна ліга».
 *
 * <p>Формат одного запису: {@code home;away;homeScore;awayScore;attendance}. Розбір і перевірка
 * одного рядка делеговані класу-сутності {@link Match}; тут лише читання списку рядків,
 * накопичення помилок і обчислення підсумку {@link ScoreAttendance}.
 */
public final class LeagueProcessor {

    private LeagueProcessor() {
    }

    /** Незмінний результат обробки набору записів: підсумок і перелік помилок. */
    public static final class Result {
        private final ScoreAttendance summary;
        private final List<String> errors;

        Result(ScoreAttendance summary, List<String> errors) {
            this.summary = summary;
            this.errors = List.copyOf(errors);
        }

        /** Повертає незмінний підсумок показників ліги. */
        public ScoreAttendance summary() {
            return summary;
        }

        public int validCount() {
            return summary.validCount();
        }

        public double averageGoals() {
            return summary.averageGoals();
        }

        public int maxAttendance() {
            return summary.maxAttendance();
        }

        public long totalAttendance() {
            return summary.totalAttendance();
        }

        public List<String> errors() {
            return List.copyOf(errors);
        }
    }

    /**
     * Розбирає рядки на об'єкти {@link Match}, перевіряє їх і обчислює підсумкові показники.
     *
     * <p>Хибний рядок не зупиняє обробку інших рядків; причину пропуску додають до переліку помилок.
     *
     * @param lines рядки вхідного файла
     * @return підсумок та перелік помилок
     */
    public static Result process(List<String> lines) {
        List<Match> matches = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            int rowNumber = index + 1;

            if (line.isBlank()) {
                errors.add("Рядок %d: порожній рядок".formatted(rowNumber));
                continue;
            }

            // Помилка одного CSV-рядка не перериває обробку решти вхідних даних.
            try {
                matches.add(Match.fromCsv(line));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(rowNumber, exception.getMessage()));
            }
        }

        return new Result(summarize(matches), errors);
    }

    private static ScoreAttendance summarize(List<Match> matches) {
        long totalGoals = 0;
        long totalAttendance = 0;
        int maxAttendance = 0;
        for (Match match : matches) {
            totalGoals += match.totalGoals();
            totalAttendance += match.getAttendance();
            maxAttendance = Math.max(maxAttendance, match.getAttendance());
        }
        // Явне приведення до double не дає цілочисловому діленню відкинути дробову частину.
        double averageGoals = matches.isEmpty() ? 0.0 : (double) totalGoals / matches.size();
        return new ScoreAttendance(matches.size(), averageGoals, maxAttendance, totalAttendance);
    }

    /**
     * Форматує підсумок і перелік помилок у текст звіту з фіксованою кількістю знаків після крапки.
     *
     * @param result результат обробки
     * @return текст звіту, придатний для виводу в консоль і файл
     */
    public static String formatReport(Result result) {
        ScoreAttendance summary = result.summary();
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", summary.validCount()));
        builder.append(String.format(Locale.ROOT, "Середня кількість голів за матч: %.2f%n", summary.averageGoals()));
        builder.append(String.format(Locale.ROOT, "Найбільша відвідуваність: %d%n", summary.maxAttendance()));
        builder.append(String.format(Locale.ROOT, "Сумарна відвідуваність: %d%n", summary.totalAttendance()));
        builder.append(String.format(Locale.ROOT, "Помилок: %d%n", result.errors().size()));
        result.errors().forEach(error -> builder.append(error).append(System.lineSeparator()));
        return builder.toString();
    }
}
