package familytree.model;

import java.util.Objects;
import java.util.Optional;

public class Marriage {
    private final String spouseAId;
    private final String spouseBId;
    private final int marriageYear;
    private Integer divorceYear;

    public Marriage(String a, String b, int marriageYear) {
        if (a == null || b == null) throw new IllegalArgumentException("spouse ids required");
        if (a.equals(b)) throw new IllegalArgumentException("Cannot marry oneself");
        this.spouseAId = a;
        this.spouseBId = b;
        this.marriageYear = marriageYear;
    }

    public String getSpouseAId() { return spouseAId; }
    public String getSpouseBId() { return spouseBId; }
    public int getMarriageYear() { return marriageYear; }
    public Integer getDivorceYear() { return divorceYear; }

    public void setDivorceYear(int year) {
        if (year < marriageYear) throw new IllegalArgumentException("Divorce before marriage?");
        this.divorceYear = year;
    }

    public String getOtherSpouseId(String id) {
        if (Objects.equals(id, spouseAId)) return spouseBId;
        if (Objects.equals(id, spouseBId)) return spouseAId;
        throw new IllegalArgumentException("Person " + id + " not in this marriage");
    }

    public boolean involves(String id) {
        return Objects.equals(id, spouseAId) || Objects.equals(id, spouseBId);
    }

    public boolean isActive() { return divorceYear == null; }
}
