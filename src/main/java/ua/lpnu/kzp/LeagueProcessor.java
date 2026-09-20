package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Перевіряє й обчислює показники для записів варіанта 17 «Спортивна ліга».
 *
 * <p>Формат одного запису: {@code home;away;homeScore;awayScore;attendance}.
 */
public final class LeagueProcessor {

    private static final int FIELD_COUNT = 5;

    private LeagueProcessor() {
    }

    /** Незмінний результат обробки набору записів. */
    public static final class Result {
        private final int validCount;
        private final double averageGoals;
        private final int maxAttendance;
        private final long totalAttendance;
        private final List<String> errors;

        Result(int validCount, double averageGoals, int maxAttendance, long totalAttendance, List<String> errors) {
            this.validCount = validCount;
            this.averageGoals = averageGoals;
            this.maxAttendance = maxAttendance;
            this.totalAttendance = totalAttendance;
            this.errors = List.copyOf(errors);
        }

        public int validCount() {
            return validCount;
        }

        public double averageGoals() {
            return averageGoals;
        }

        public int maxAttendance() {
            return maxAttendance;
        }

        public long totalAttendance() {
            return totalAttendance;
        }

        public List<String> errors() {
            return List.copyOf(errors);
        }
    }

    /**
     * Розбирає та перевіряє рядки, обчислює показники за коректними записами.
     *
     * <p>Хибний рядок не зупиняє обробку інших рядків; причину пропуску додають до переліку помилок.
     *
     * @param lines рядки вхідного файла
     * @return показники та перелік помилок
     */
    public static Result process(List<String> lines) {
        List<String> errors = new ArrayList<>();
        int validCount = 0;
        long totalGoals = 0;
        long totalAttendance = 0;
        int maxAttendance = 0;

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            int rowNumber = index + 1;

            if (line.isBlank()) {
                errors.add("Рядок %d: порожній рядок".formatted(rowNumber));
                continue;
            }

            // Порожнє останнє поле зберігаємо, тому передано другий аргумент -1.
            String[] fields = line.split(";", -1);
            if (fields.length != FIELD_COUNT) {
                errors.add("Рядок %d: очікується %d полів".formatted(rowNumber, FIELD_COUNT));
                continue;
            }

            String home = fields[0];
            String away = fields[1];
            if (home.isBlank() || away.isBlank()) {
                errors.add("Рядок %d: порожня назва команди".formatted(rowNumber));
                continue;
            }

            try {
                int homeScore = Integer.parseInt(fields[2]);
                int awayScore = Integer.parseInt(fields[3]);
                int attendance = Integer.parseInt(fields[4]);

                if (homeScore < 0 || awayScore < 0 || attendance < 0) {
                    errors.add("Рядок %d: від'ємне числове значення".formatted(rowNumber));
                    continue;
                }

                validCount++;
                totalGoals += homeScore + awayScore;
                totalAttendance += attendance;
                maxAttendance = Math.max(maxAttendance, attendance);
            } catch (NumberFormatException exception) {
                errors.add("Рядок %d: числове поле має помилковий формат".formatted(rowNumber));
            }
        }

        // Явне приведення до double не дає цілочисловому діленню відкинути дробову частину.
        double averageGoals = validCount == 0 ? 0.0 : (double) totalGoals / validCount;
        return new Result(validCount, averageGoals, maxAttendance, totalAttendance, errors);
    }

    /**
     * Форматує показники й перелік помилок у текст звіту з фіксованою кількістю знаків після крапки.
     *
     * @param result результат обробки
     * @return текст звіту, придатний для виводу в консоль і файл
     */
    public static String formatReport(Result result) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ROOT, "Коректних записів: %d%n", result.validCount()));
        builder.append(String.format(Locale.ROOT, "Середня кількість голів за матч: %.2f%n", result.averageGoals()));
        builder.append(String.format(Locale.ROOT, "Найбільша відвідуваність: %d%n", result.maxAttendance()));
        builder.append(String.format(Locale.ROOT, "Сумарна відвідуваність: %d%n", result.totalAttendance()));
        builder.append(String.format(Locale.ROOT, "Помилок: %d%n", result.errors().size()));
        result.errors().forEach(error -> builder.append(error).append(System.lineSeparator()));
        return builder.toString();
    }
}
