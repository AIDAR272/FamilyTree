package familytree.model;

/** Minor: cannot marry (simple rule). Could be extended with guardianship fields later. */
public final class Minor extends Person {
    public Minor(String id, String fullName, Gender gender, int birthYear, Integer deathYear) {
        super(id, fullName, gender, birthYear, deathYear);
    }

    @Override
    public boolean canMarry() {
        return false;
    }
}
