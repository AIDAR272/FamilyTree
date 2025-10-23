classDiagram
    direction TB

    class Gender {
        <<enum>>
        +MALE
        +FEMALE
        +OTHER
    }

    class Person {
        -String id
        -String fullName
        -Gender gender
        -int birthYear
        -Integer deathYear
        -Set~String~ parentIds
        -Set~String~ childrenIds
        -List~Marriage~ marriages
        +getId()
        +getFullName()
        +getGender()
        +getBirthYear()
        +getDeathYear()
        +isAlive()
        +ageIn(int)
        +canMarry()
        +oneLineSummary()
    }

    class Adult
    class Minor

    class Marriage {
        -String spouseAId
        -String spouseBId
        -int marriageYear
        -Integer divorceYear
        +getOtherSpouseId(String)
        +isActive()
    }

    class PersonFactory {
        +create(String, Gender, int, Integer)
        +nextId()
    }

    class TraversalStrategy {
        <<interface>>
        +traverseAncestors(FamilyTree,String,int)
        +traverseDescendants(FamilyTree,String,int)
    }

    class BFSTraversal
    class DFSTraversal

    class Renderer {
        <<interface>>
        +renderAncestors(List~List~Person~)
        +renderDescendants(List~List~Person~)
    }

    class IndentedTreeRenderer
    class LineRenderer

    class FamilyTree {
        -Map~String,Person~ people
        -TraversalStrategy traversalStrategy
        -Renderer renderer
        +addPerson(...)
        +linkParentChild(...)
        +marry(...)
        +ancestorsOf(...)
        +descendantsOf(...)
        +renderAncestors(...)
    }

    Person <|-- Adult
    Person <|-- Minor
    Person "1" *-- "*" Marriage : marriages
    FamilyTree "1" o-- "*" Person : registry
    PersonFactory ..> Person
    TraversalStrategy <|.. BFSTraversal
    TraversalStrategy <|.. DFSTraversal
    Renderer <|.. IndentedTreeRenderer
    Renderer <|.. LineRenderer
    Gender <.. Person
