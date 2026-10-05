package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScoreAttendanceTest {

    @Test
    void recordUsesValueEquality() {
        ScoreAttendance first = new ScoreAttendance(3, 2.5, 18500, 38500L);
        ScoreAttendance second = new ScoreAttendance(3, 2.5, 18500, 38500L);

        assertEquals(first, second);
    }

    @Test
    void compactConstructorRejectsNegativeValidCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScoreAttendance(-1, 0.0, 0, 0L));
    }

    @Test
    void compactConstructorRejectsNegativeAverageGoals() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScoreAttendance(1, -0.5, 0, 0L));
    }

    @Test
    void compactConstructorRejectsNegativeTotalAttendance() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScoreAttendance(1, 2.0, 100, -1L));
    }
}
