package com.olegs;

import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Procedures {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    @Procedure(name = "com.olegs.triples", mode = Mode.READ)
    @Description("CALL com.olegs.triples(List<String> tokens)")
    public Stream<TripleResult> yield(@Name("tokens") List<String> tokens) throws IOException {

        ArrayList<TripleResult> results = new ArrayList<>();

        // Get the Stems
        ArrayList<Node> stems = new ArrayList<>();
        for (String token : tokens) {
            Node stem = db.findNode(Labels.Stem, "stem", token);
            if (stem != null) {
                stems.add(stem);
            }
        }

        // Sort by degree
        stems.sort(Comparator.comparing(Node::getDegree));

        // Get the Patents for the lowest degree Stem
        Node firstStem = stems.get(0);
        for (Relationship r : firstStem.getRelationships(Direction.INCOMING, RelationshipTypes.CONTAINS_STEM)) {
            Node patent = r.getStartNode();
            String patentTitle = (String) patent.getProperty("title");
            String patentUCID = String.valueOf(patent.getProperty("ucid"));

            // Get all the stems this patent has
            ArrayList<Node> have = new ArrayList<>();
            for (Relationship r2 : patent.getRelationships(Direction.OUTGOING, RelationshipTypes.CONTAINS_STEM)) {
                have.add(r2.getEndNode());
            }
            // Find out which stems this patent needs
            ArrayList<Node> need = new ArrayList<>();
            for (Node stem : stems) {
                if (!have.contains(stem)) {
                    need.add(stem);
                }
            }

            // Find other patents that fill this need
            for (Node needed : need) {
                for (Relationship r3 : needed.getRelationships(Direction.INCOMING, RelationshipTypes.CONTAINS_STEM)) {
                    Node secondPatent = r3.getStartNode();
                    String secondPatentTitle = (String) secondPatent.getProperty("title");
                    String secondPatentUCID = String.valueOf(secondPatent.getProperty("ucid"));

                    // Get all the stems this patent has
                    ArrayList<Node> have2 = new ArrayList<>();
                    for (Relationship r4 : secondPatent.getRelationships(Direction.OUTGOING, RelationshipTypes.CONTAINS_STEM)) {
                        have2.add(r4.getEndNode());
                    }
                    // Find out which stems this patent needs
                    ArrayList<Node> need2 = new ArrayList<>();
                    for (Node stem : need) {
                        if (!have2.contains(stem)) {
                            need2.add(stem);
                        }
                    }

                    // Find other patents that fill this need
                    for (Node needed2 : need2) {
                        for (Relationship r5 : needed2.getRelationships(Direction.INCOMING, RelationshipTypes.CONTAINS_STEM)) {
                            Node thirdPatent = r5.getStartNode();
                            // Get all the stems this patent has
                            ArrayList<Node> have3 = new ArrayList<>();
                            for (Relationship r6 : thirdPatent.getRelationships(Direction.OUTGOING, RelationshipTypes.CONTAINS_STEM)) {
                                have3.add(r6.getEndNode());
                            }

                            // Find out which stems this patent needs
                            ArrayList<Node> need3 = new ArrayList<>();
                            for (Node stem : need2) {
                                if (!have3.contains(stem)) {
                                    need3.add(stem);
                                }
                            }

                            if (need3.isEmpty()) {
                                String thirdPatentTitle = (String) thirdPatent.getProperty("title");
                                String thirdPatentUCID = String.valueOf(thirdPatent.getProperty("ucid"));

                                results.add(new TripleResult(patentTitle, patentUCID, secondPatentTitle, secondPatentUCID, thirdPatentTitle, thirdPatentUCID));
                            }
                        }
                    }
                }
            }
        }
        return results.stream();
    }
}