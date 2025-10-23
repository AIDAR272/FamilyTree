package familytree.strategy;

import familytree.model.Person;
import familytree.service.FamilyTree;

import java.util.*;

/** Breadth-first traversal for generations (level-order). */
public class BFSTraversal implements TraversalStrategy {
    @Override
    public List<List<Person>> traverseAncestors(FamilyTree registry, String personId, int generations) {
        List<List<Person>> result = new ArrayList<>();
        Person start = registry.getPerson(personId);
        List<Person> level = new ArrayList<>(); level.add(start);
        result.add(new ArrayList<>(level));

        Set<String> visited = new HashSet<>(); visited.add(personId);

        for (int g = 1; g <= generations; g++) {
            List<Person> next = new ArrayList<>();
            for (Person p : level) {
                for (String pid : p.getParentIds()) {
                    if (visited.add(pid)) next.add(registry.getPerson(pid));
                }
            }
            result.add(next);
            level = next;
        }
        return result;
    }

    @Override
    public List<List<Person>> traverseDescendants(FamilyTree registry, String personId, int generations) {
        List<List<Person>> result = new ArrayList<>();
        Person start = registry.getPerson(personId);
        List<Person> level = new ArrayList<>(); level.add(start);
        result.add(new ArrayList<>(level));

        Set<String> visited = new HashSet<>(); visited.add(personId);

        for (int g = 1; g <= generations; g++) {
            List<Person> next = new ArrayList<>();
            for (Person p : level) {
                for (String cid : p.getChildrenIds()) {
                    if (visited.add(cid)) next.add(registry.getPerson(cid));
                }
            }
            result.add(next);
            level = next;
        }
        return result;
    }
}
