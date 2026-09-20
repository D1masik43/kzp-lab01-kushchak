package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LeagueProcessorTest {

    @Test
    void processesOnlyValidRecords() {
        List<String> lines = List.of(
                "Динамо;Шахтар;2;1;18500",
                "Карпати;Верес;0;0;7200",
                "Зоря;Металіст;3;2;12800");

        LeagueProcessor.Result result = LeagueProcessor.process(lines);

        assertEquals(3, result.validCount());
        assertEquals(8.0 / 3, result.averageGoals(), 0.0001);
        assertEquals(18500, result.maxAttendance());
        assertEquals(38500, result.totalAttendance());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void skipsBlankLine() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of(""));

        assertEquals(0, result.validCount());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors().get(0).contains("порожній рядок"));
    }

    @Test
    void skipsRowWithWrongFieldCount() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of("Динамо;Шахтар;2;1"));

        assertEquals(0, result.validCount());
        assertTrue(result.errors().get(0).contains("полів"));
    }

    @Test
    void skipsRowWithNonNumericField() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of("Карпати;Верес;два;0;7200"));

        assertEquals(0, result.validCount());
        assertTrue(result.errors().get(0).contains("помилковий формат"));
    }

    @Test
    void skipsRowWithNegativeValue() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of("Зоря;Металіст;3;-2;12800"));

        assertEquals(0, result.validCount());
        assertTrue(result.errors().get(0).contains("від'ємне"));
    }

    @Test
    void skipsRowWithBlankTeamName() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of(";Верес;1;1;5000"));

        assertEquals(0, result.validCount());
        assertTrue(result.errors().get(0).contains("порожня назва"));
    }

    @Test
    void averageIsZeroWhenNoValidRecords() {
        LeagueProcessor.Result result = LeagueProcessor.process(List.of("не;запис"));

        assertEquals(0, result.validCount());
        assertEquals(0.0, result.averageGoals(), 0.0001);
        assertEquals(0, result.maxAttendance());
        assertEquals(0, result.totalAttendance());
    }

    @Test
    void continuesProcessingAfterInvalidRow() {
        List<String> lines = List.of(
                "Динамо;Шахтар;2;1;18500",
                "",
                "Карпати;Верес;два;0;7200",
                "Зоря;Металіст;3;2;12800");

        LeagueProcessor.Result result = LeagueProcessor.process(lines);

        assertEquals(2, result.validCount());
        assertEquals(2, result.errors().size());
        assertEquals(31300, result.totalAttendance());
    }

    @Test
    void formatReportProducesFixedDecimalPlaces() {
        LeagueProcessor.Result result = LeagueProcessor.process(
                List.of("Динамо;Шахтар;2;1;18500", "Карпати;Верес;0;0;7200"));

        String report = LeagueProcessor.formatReport(result);

        assertTrue(report.contains("Середня кількість голів за матч: 1.50"));
        assertTrue(report.contains("Коректних записів: 2"));
        assertTrue(report.contains("Найбільша відвідуваність: 18500"));
        assertTrue(report.contains("Сумарна відвідуваність: 25700"));
    }
}
