package familytree.strategy;

import familytree.model.Person;

import java.util.List;

public interface Renderer {
    String renderAncestors(List<List<Person>> generations);
    String renderDescendants(List<List<Person>> generations);
}
