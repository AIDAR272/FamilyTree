package familytree.cli;

import familytree.model.*;
import familytree.strategy.IndentedTreeRenderer;
import familytree.strategy.BFSTraversal;
import familytree.service.FamilyTree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI (keeps same commands). Uses BFS + IndentedTreeRenderer by default.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FamilyTree registry = new FamilyTree(new BFSTraversal(), new IndentedTreeRenderer());

    public static void main(String[] args) {
        System.out.println("Family Tree CLI — type HELP for commands, QUIT to exit.");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] tokens = splitQuoted(line);
            String cmd = tokens[0].toUpperCase(Locale.ROOT);

            try {
                switch (cmd) {
                    case "QUIT": case "EXIT": System.out.println("Bye."); return;
                    case "HELP": printHelp(); break;
                    case "ADD_PERSON": cmdAddPerson(tokens); break;
                    case "ADD_PARENT_CHILD": cmdAddParentChild(tokens); break;
                    case "MARRY": cmdMarry(tokens); break;
                    case "ANCESTORS": cmdAncestors(tokens); break;
                    case "DESCENDANTS": cmdDescendants(tokens); break;
                    case "SIBLINGS": cmdSiblings(tokens); break;
                    case "SHOW": cmdShow(tokens); break;
                    case "LIST": cmdList(); break;
                    default: System.out.println("Unknown command. Type HELP.");
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  ADD_PERSON "<Full Name>" <Gender> <BirthYear> [DeathYear]
                  ADD_PARENT_CHILD <parentId> <childId>
                  MARRY <personAId> <personBId> <Year>
                  ANCESTORS <personId> <generations>
                  DESCENDANTS <personId> <generations>
                  SIBLINGS <personId>
                  SHOW <personId>
                  LIST
                  HELP
                  QUIT
                """);
    }

    private static void cmdAddPerson(String[] tokens) {
        if (tokens.length < 4) throw new IllegalArgumentException("Usage: ADD_PERSON \"<Full Name>\" <Gender> <BirthYear> [DeathYear]");
        String fullName = tokens[1];
        Gender gender = Gender.valueOf(tokens[2].toUpperCase(Locale.ROOT));
        int birthYear = Integer.parseInt(tokens[3]);
        Integer deathYear = tokens.length >= 5 ? Integer.valueOf(tokens[4]) : null;
        Person p = registry.addPerson(fullName, gender, birthYear, deathYear);
        System.out.println("-> " + p.getId());
    }

    private static void cmdAddParentChild(String[] tokens) {
        if (tokens.length != 3) throw new IllegalArgumentException("Usage: ADD_PARENT_CHILD <parentId> <childId>");
        registry.linkParentChild(tokens[1], tokens[2]);
        System.out.println("OK");
    }

    private static void cmdMarry(String[] tokens) {
        if (tokens.length != 4) throw new IllegalArgumentException("Usage: MARRY <personAId> <personBId> <Year>");
        registry.marry(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
        System.out.println("OK");
    }

    private static void cmdAncestors(String[] tokens) {
        if (tokens.length != 3) throw new IllegalArgumentException("Usage: ANCESTORS <personId> <generations>");
        System.out.println(registry.renderAncestors(tokens[1], Integer.parseInt(tokens[2])));
    }

    private static void cmdDescendants(String[] tokens) {
        if (tokens.length != 3) throw new IllegalArgumentException("Usage: DESCENDANTS <personId> <generations>");
        System.out.println(registry.renderDescendants(tokens[1], Integer.parseInt(tokens[2])));
    }

    private static void cmdSiblings(String[] tokens) {
        if (tokens.length != 2) throw new IllegalArgumentException("Usage: SIBLINGS <personId>");
        List<Person> s = registry.siblingsOf(tokens[1]);
        if (s.isEmpty()) System.out.println("<none>");
        else System.out.println(s.stream().map(p -> p.getId() + " " + p.getFullName()).collect(Collectors.joining(", ")));
    }

    private static void cmdShow(String[] tokens) {
        if (tokens.length != 2) throw new IllegalArgumentException("Usage: SHOW <personId>");
        Person p = registry.getPerson(tokens[1]);
        System.out.println(p.oneLineSummary());
        System.out.println("Parents: " + String.join(", ", p.getParentIds()));
        System.out.println("Children: " + String.join(", ", p.getChildrenIds()));
        System.out.println("Marriages:");
        for (var m : p.getMarriages()) {
            System.out.printf("  %s - %s (married %d%s)%n",
                    m.getSpouseAId(), m.getSpouseBId(), m.getMarriageYear(),
                    m.getDivorceYear() != null ? " divorced " + m.getDivorceYear() : "");
        }
    }

    private static void cmdList() {
        System.out.println("All people:");
        registry.allPeople().forEach(p -> System.out.println("  " + p.oneLineSummary()));
    }

    // Helper: split tokens but keep quoted phrase together
    private static String[] splitQuoted(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') { inQuotes = !inQuotes; if (!inQuotes) { tokens.add(cur.toString()); cur.setLength(0);} continue; }
            if (inQuotes) { cur.append(c); } else {
                if (Character.isWhitespace(c)) {
                    if (cur.length()>0) { tokens.add(cur.toString()); cur.setLength(0); }
                } else cur.append(c);
            }
        }
        if (cur.length()>0) tokens.add(cur.toString());
        return tokens.toArray(String[]::new);
    }
}

