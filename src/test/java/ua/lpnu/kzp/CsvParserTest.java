package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CsvParserTest {

    @Test
    void parseSplitsSimpleLine() throws DataStorageException {
        assertEquals(List.of("Динамо", "Шахтар", "3"), CsvParser.parse("Динамо,Шахтар,3"));
    }

    @Test
    void parseKeepsCommaInsideQuotedField() throws DataStorageException {
        assertEquals(List.of("A-03", "Карти, що говорять"), CsvParser.parse("A-03,\"Карти, що говорять\""));
    }

    @Test
    void parseUnescapesDoubledQuoteInsideQuotedField() throws DataStorageException {
        assertEquals(List.of("Матч \"Б\""), CsvParser.parse("\"Матч \"\"Б\"\"\""));
    }

    @Test
    void parseThrowsWhenTextHasMoreThanOneRecord() {
        assertThrows(DataStorageException.class, () -> CsvParser.parse("рядок1\nрядок2"));
    }

    @Test
    void parseDocumentSplitsMultipleRecordsOnLfAndCrlf() throws DataStorageException {
        List<List<String>> records = CsvParser.parseDocument("a,b\r\nc,d\ne,f");

        assertEquals(List.of(List.of("a", "b"), List.of("c", "d"), List.of("e", "f")), records);
    }

    @Test
    void parseDocumentKeepsNewlineInsideQuotedField() throws DataStorageException {
        List<List<String>> records = CsvParser.parseDocument("\"рядок1\nрядок2\",x");

        assertEquals(List.of(List.of("рядок1\nрядок2", "x")), records);
    }

    @Test
    void parseDocumentOnEmptyTextReturnsEmptyList() throws DataStorageException {
        assertTrue(CsvParser.parseDocument("").isEmpty());
    }

    @Test
    void parseDocumentRejectsNullText() {
        assertThrows(DataStorageException.class, () -> CsvParser.parseDocument(null));
    }

    @Test
    void parseDocumentRejectsUnclosedQuote() {
        DataStorageException exception = assertThrows(DataStorageException.class,
                () -> CsvParser.parseDocument("\"незакрите поле"));

        assertTrue(exception.getMessage().contains("Незакриті"));
    }

    @Test
    void parseDocumentRejectsCharacterAfterClosedQuote() {
        assertThrows(DataStorageException.class, () -> CsvParser.parseDocument("\"ab\"x,y"));
    }
}
