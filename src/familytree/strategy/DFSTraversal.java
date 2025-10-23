package familytree.strategy;

import familytree.model.Person;
import familytree.service.FamilyTree;

import java.util.*;

/**
 * Depth-first style generation collector: collects ancestors/descendants by depth,
 * but visits depth-first (still outputs by generation level).
 */
public class DFSTraversal implements TraversalStrategy {

    @Override
    public List<List<Person>> traverseAncestors(FamilyTree registry, String personId, int generations) {
        List<List<Person>> levels = new ArrayList<>();
        for (int i = 0; i <= generations; i++) levels.add(new ArrayList<>());
        Set<String> visited = new HashSet<>();
        dfsAncestors(registry, personId, 0, generations, visited, levels);
        return levels;
    }

    private void dfsAncestors(FamilyTree reg, String id, int depth, int maxDepth, Set<String> visited, List<List<Person>> levels) {
        if (depth > maxDepth) return;
        if (!visited.add(id)) return;
        levels.get(depth).add(reg.getPerson(id));
        Person p = reg.getPerson(id);
        for (String pid : p.getParentIds()) dfsAncestors(reg, pid, depth + 1, maxDepth, visited, levels);
    }

    @Override
    public List<List<Person>> traverseDescendants(FamilyTree registry, String personId, int generations) {
        List<List<Person>> levels = new ArrayList<>();
        for (int i = 0; i <= generations; i++) levels.add(new ArrayList<>());
        Set<String> visited = new HashSet<>();
        dfsDesc(registry, personId, 0, generations, visited, levels);
        return levels;
    }

    private void dfsDesc(FamilyTree reg, String id, int depth, int maxDepth, Set<String> visited, List<List<Person>> levels) {
        if (depth > maxDepth) return;
        if (!visited.add(id)) return;
        levels.get(depth).add(reg.getPerson(id));
        Person p = reg.getPerson(id);
        for (String cid : p.getChildrenIds()) dfsDesc(reg, cid, depth + 1, maxDepth, visited, levels);
    }
}
