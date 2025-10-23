package familytree.model;

import java.util.*;

public abstract class Person {
    private final String id;
    private String fullName;
    private final Gender gender;
    private int birthYear;
    private Integer deathYear; // nullable

    // relationships - encapsulated
    private final Set<String> parentIds = new LinkedHashSet<>(2);
    private final Set<String> childrenIds = new LinkedHashSet<>();
    private final List<Marriage> marriages = new ArrayList<>();

    protected Person(String id, String fullName, Gender gender, int birthYear, Integer deathYear) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        setFullName(fullName);
        this.gender = Objects.requireNonNull(gender, "gender required");
        setBirthYear(birthYear);
        setDeathYear(deathYear);
    }

    // Getters (no setters exposed publicly except controlled ones)
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public Gender getGender() { return gender; }
    public int getBirthYear() { return birthYear; }
    public Integer getDeathYear() { return deathYear; }

    // Encapsulated access to relationship ids
    public Set<String> getParentIds() { return Collections.unmodifiableSet(parentIds); }
    public Set<String> getChildrenIds() { return Collections.unmodifiableSet(childrenIds); }
    public List<Marriage> getMarriages() { return Collections.unmodifiableList(marriages); }

    // Controlled setters & validation
    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name cannot be blank");
        this.fullName = fullName.strip();
    }

    public void setBirthYear(int birthYear) {
        if (birthYear < 1000 || birthYear > 3000) throw new IllegalArgumentException("Birth year implausible");
        this.birthYear = birthYear;
    }

    public void setDeathYear(Integer deathYear) {
        if (deathYear != null && deathYear < birthYear) throw new IllegalArgumentException("Death year before birth");
        this.deathYear = deathYear;
    }

    // Package-private relationship mutators (only registry/service should call)
    public void addParentId(String parentId) {
        if (parentId == null) throw new IllegalArgumentException("parentId null");
        if (parentIds.size() >= 2 && !parentIds.contains(parentId))
            throw new IllegalArgumentException("Person cannot have more than 2 parents");
        parentIds.add(parentId);
    }
    public void removeParentId(String parentId) { parentIds.remove(parentId); }

    public void addChildId(String childId) {
        if (childId == null) throw new IllegalArgumentException("childId null");
        childrenIds.add(childId);
    }
    public void removeChildId(String childId) { childrenIds.remove(childId); }

    public void addMarriage(Marriage m) { marriages.add(m); }

    // Computed
    public boolean isAlive() { return deathYear == null; }
    public int ageIn(int year) {
        if (year < birthYear) throw new IllegalArgumentException("Year is before birth");
        return (deathYear == null || deathYear > year) ? year - birthYear : deathYear - birthYear;
    }

    // Marriage rules: default allow marriage; subclasses may override
    public boolean canMarry() { return true; }

    public Optional<Marriage> getActiveMarriage() {
        return marriages.stream().filter(Marriage::isActive).findFirst();
    }

    public Optional<String> getActiveSpouseId() {
        return getActiveMarriage().map(m -> m.getOtherSpouseId(id));
    }

    public String oneLineSummary() {
        return String.format("%s | %s | %s | b.%d%s | spouse=%s | children=%d",
                id,
                fullName,
                gender,
                birthYear,
                (deathYear != null ? " d." + deathYear : ""),
                getActiveSpouseId().orElse("none"),
                childrenIds.size());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person p = (Person) o;
        return id.equals(p.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
