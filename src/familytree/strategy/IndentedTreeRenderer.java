package familytree.strategy;

import familytree.model.Person;

import java.util.List;
import java.util.stream.Collectors;

/** Renders generations as indented tree (child -> parent or parent -> child). */
public class IndentedTreeRenderer implements Renderer {

    @Override
    public String renderAncestors(List<List<Person>> generations) {
        StringBuilder sb = new StringBuilder();
        for (int depth = 0; depth < generations.size(); depth++) {
            List<Person> lvl = generations.get(depth);
            if (depth == 0) {
                // show self on top
                for (Person p : lvl) sb.append("- ").append(nodeLine(p)).append("\n");
            } else {
                for (Person p : lvl) {
                    sb.append("  ".repeat(Math.max(0, depth))).append("- ").append(nodeLine(p)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String renderDescendants(List<List<Person>> generations) {
        StringBuilder sb = new StringBuilder();
        for (int depth = 0; depth < generations.size(); depth++) {
            List<Person> lvl = generations.get(depth);
            if (depth == 0) {
                for (Person p : lvl) sb.append("- ").append(nodeLine(p)).append("\n");
            } else {
                for (Person p : lvl) {
                    sb.append("  ".repeat(Math.max(0, depth))).append("- ").append(nodeLine(p)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String nodeLine(Person p) {
        return String.format("%s %s (b.%d)", p.getId(), p.getFullName(), p.getBirthYear());
    }
}

