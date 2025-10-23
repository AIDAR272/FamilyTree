package familytree.strategy;

import familytree.model.Person;

import java.util.List;
import java.util.stream.Collectors;

/** Compact single-line per person listing grouped by generation. */
public class LineRenderer implements Renderer {
    @Override
    public String renderAncestors(List<List<Person>> generations) {
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < generations.size(); g++) {
            List<Person> lvl = generations.get(g);
            sb.append("Gen ").append(g).append(": ");
            if (lvl.isEmpty()) sb.append("(none)");
            else sb.append(lvl.stream().map(p -> p.getId() + ":" + p.getFullName()).collect(Collectors.joining(", ")));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String renderDescendants(List<List<Person>> generations) {
        return renderAncestors(generations);
    }
}

