package ua.lpnu.kzp;

/** Категорія матчу ліги (варіант 17, «Спортивна ліга»). */
public enum MatchKind {
    LEAGUE("ліговий"),
    PLAYOFF("плей-офф");

    private final String label;

    /* Конструктор enum використовується JVM для створення фіксованих констант. */
    MatchKind(String label) {
        this.label = label;
    }

    /** Повертає текстове подання категорії для звіту. */
    public String label() {
        return label;
    }
}
