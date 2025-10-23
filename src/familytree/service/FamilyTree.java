package familytree.service;

import familytree.service.PersonFactory;
import familytree.model.*;
import familytree.strategy.Renderer;
import familytree.strategy.TraversalStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory registry. Uses TraversalStrategy (Strategy pattern) for traversal.
 */
public final class FamilyTree {
    private final Map<String, Person> people = new LinkedHashMap<>();
    private TraversalStrategy traversalStrategy;
    private Renderer renderer;

    public FamilyTree(TraversalStrategy traversalStrategy, Renderer renderer) {
        this.traversalStrategy = Objects.requireNonNull(traversalStrategy);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public Person addPerson(String fullName, Gender gender, int birthYear, Integer deathYear) {
        Person p = PersonFactory.create(fullName, gender, birthYear, deathYear);
        if (people.containsKey(p.getId())) throw new IllegalArgumentException("Duplicate id: " + p.getId());
        people.put(p.getId(), p);
        return p;
    }

    public Person getPerson(String id) {
        Person p = people.get(id);
        if (p == null) throw new IllegalArgumentException("Unknown person id: " + id);
        return p;
    }

    public Collection<Person> allPeople() { return Collections.unmodifiableCollection(people.values()); }

    // Link parent->child. Enforce rules: max 2 parents, prevent cycles.
    public void linkParentChild(String parentId, String childId) {
        if (parentId.equals(childId)) throw new IllegalArgumentException("Cannot be own parent");
        Person parent = getPerson(parentId);
        Person child = getPerson(childId);
        if (child.getParentIds().size() >= 2 && !child.getParentIds().contains(parentId))
            throw new IllegalArgumentException("Child already has two parents");

        // Prevent cycles: parent cannot be descendant of child
        if (isDescendant(parentId, childId)) throw new IllegalArgumentException("Link would create a cycle");

        // perform link
        ((Person) child).addParentId(parentId);
        ((Person) parent).addChildId(childId);
    }

    // Marriage: disallow if either cannot marry, or already has active spouse
    public void marry(String aId, String bId, int year) {
        if (aId.equals(bId)) throw new IllegalArgumentException("Cannot marry oneself");
        Person a = getPerson(aId);
        Person b = getPerson(bId);
        if (!a.canMarry()) throw new IllegalArgumentException(aId + " is not allowed to marry (age/rule)");
        if (!b.canMarry()) throw new IllegalArgumentException(bId + " is not allowed to marry (age/rule)");
        if (a.getActiveSpouseId().isPresent()) throw new IllegalArgumentException(aId + " already has active spouse");
        if (b.getActiveSpouseId().isPresent()) throw new IllegalArgumentException(bId + " already has active spouse");

        Marriage m = new Marriage(aId, bId, year);
        ((Person) a).addMarriage(m);
        ((Person) b).addMarriage(m);
    }

    public void divorce(String aId, String bId, int year) {
        Person a = getPerson(aId);
        Person b = getPerson(bId);
        Optional<Marriage> am = a.getMarriages().stream().filter(m -> m.isActive() && m.involves(bId)).findFirst();
        if (am.isEmpty()) throw new IllegalArgumentException("No active marriage between " + aId + " and " + bId);
        am.get().setDivorceYear(year);
    }

    public boolean isDescendant(String candidateId, String ancestorId) {
        Person ancestor = getPerson(ancestorId);
        Deque<String> dq = new ArrayDeque<>(ancestor.getChildrenIds());
        while (!dq.isEmpty()) {
            String cur = dq.removeFirst();
            if (cur.equals(candidateId)) return true;
            dq.addAll(getPerson(cur).getChildrenIds());
        }
        return false;
    }

    public List<Person> childrenOf(String id) {
        Person p = getPerson(id);
        return p.getChildrenIds().stream().map(this::getPerson).collect(Collectors.toList());
    }

    public Optional<Person> spouseOf(String id) {
        Person p = getPerson(id);
        return p.getActiveSpouseId().map(this::getPerson);
    }

    public List<Person> siblingsOf(String id) {
        Person p = getPerson(id);
        Set<String> parents = p.getParentIds();
        if (parents.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> s = new LinkedHashSet<>();
        for (String pid : parents) {
            Person par = people.get(pid);
            if (par == null) continue;
            for (String childId : par.getChildrenIds()) if (!childId.equals(id)) s.add(childId);
        }
        return s.stream().map(this::getPerson).collect(Collectors.toList());
    }

    // Delegates to TraversalStrategy
    public List<List<Person>> ancestorsOf(String id, int generations) {
        if (generations < 0) throw new IllegalArgumentException("generations >= 0 required");
        return traversalStrategy.traverseAncestors(this, id, generations);
    }

    public List<List<Person>> descendantsOf(String id, int generations) {
        if (generations < 0) throw new IllegalArgumentException("generations >= 0 required");
        return traversalStrategy.traverseDescendants(this, id, generations);
    }

    // Rendering helper
    public String renderAncestors(String id, int generations) {
        return renderer.renderAncestors(ancestorsOf(id, generations));
    }

    public String renderDescendants(String id, int generations) {
        return renderer.renderDescendants(descendantsOf(id, generations));
    }

    // setters to change strategy/renderer at runtime (polymorphism)
    public void setTraversalStrategy(TraversalStrategy s) { this.traversalStrategy = Objects.requireNonNull(s); }
    public void setRenderer(Renderer r) { this.renderer = Objects.requireNonNull(r); }
}
