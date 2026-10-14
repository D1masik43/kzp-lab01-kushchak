package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void allReturnsSnapshotInAdditionOrder() {
        Repository<String> repository = new Repository<>();
        repository.add("Динамо");
        repository.add("Шахтар");

        assertEquals(List.of("Динамо", "Шахтар"), repository.all());
    }

    @Test
    void allSnapshotDoesNotReflectLaterAdditions() {
        Repository<String> repository = new Repository<>();
        repository.add("Динамо");

        List<String> snapshot = repository.all();
        repository.add("Шахтар");

        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("Карпати"));
    }

    @Test
    void findFiltersByPredicate() {
        Repository<Integer> repository = new Repository<>();
        repository.add(1);
        repository.add(2);
        repository.add(3);

        assertEquals(List.of(2), repository.find(value -> value % 2 == 0));
    }

    @Test
    void findOnEmptyRepositoryReturnsEmptyList() {
        assertTrue(new Repository<Integer>().find(value -> true).isEmpty());
    }

    @Test
    void addRejectsNull() {
        assertThrows(NullPointerException.class, () -> new Repository<String>().add(null));
    }
}
