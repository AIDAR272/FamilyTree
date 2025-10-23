package familytree.strategy;

import familytree.model.Person;
import familytree.service.FamilyTree;

import java.util.List;

/**
 * Strategy for producing level-wise generations for ancestors/descendants.
 * Returns list-of-levels: index 0 = self, index 1 = parents/children, ...
 */
public interface TraversalStrategy {
    List<List<Person>> traverseAncestors(FamilyTree registry, String personId, int generations);
    List<List<Person>> traverseDescendants(FamilyTree registry, String personId, int generations);
}

