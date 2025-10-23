package familytree.model;

/** Concrete Person representing an adult - uses default marriage rules. */
public final class Adult extends Person {
    public Adult(String id, String fullName, Gender gender, int birthYear, Integer deathYear) {
        super(id, fullName, gender, birthYear, deathYear);
    }

    // inherits canMarry() true
}
