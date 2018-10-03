# Oleg
Stored Procedure for Oleg


Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/procedures-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/procedures-1.0-SNAPSHOT.jar neo4j-enterprise-3.4.7/plugins/.
    


Restart your Neo4j Server. A new Stored Procedure is available:

    CALL com.olegs.triples(List<String> tokens)
    
For example:

    CALL com.olegs.triples(['heart', 'electron', 'physiolog', 'electr', 'output', 'implant', 'sensor', 'valv'])
