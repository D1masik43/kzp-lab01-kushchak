package ua.lpnu.kzp;

/**
 * Незмінний підсумок показників ліги (варіант 17, «Спортивна ліга»).
 *
 * @param validCount кількість коректних матчів
 * @param averageGoals середня кількість голів за матч
 * @param maxAttendance найбільша відвідуваність
 * @param totalAttendance сумарна відвідуваність
 */
public record ScoreAttendance(int validCount, double averageGoals, int maxAttendance, long totalAttendance) {

    /** Перевіряє допустимість підсумкових показників. */
    public ScoreAttendance {
        if (validCount < 0 || maxAttendance < 0 || totalAttendance < 0
                || averageGoals < 0 || !Double.isFinite(averageGoals)) {
            throw new IllegalArgumentException("Показники не можуть бути від'ємними");
        }
    }
}
