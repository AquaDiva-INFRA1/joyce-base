# JOYCE Base project

## Overview

For a general overview over the JOYCE project, please refer to the README.md file of the joyce repository.

The JOYCE Base project provides essential services to
- download ontologies from BioPortal (`de.aquadiva.joyce.base.services.IOntologyDownloadService`)
- convert all ontologies into the OWL format (`de.aquadiva.joyce.base.services.IOntologyFormatConversionService`)
- extract names and synonyms from OWL ontology classes (`de.aquadiva.joyce.base.services.IOntologyNameExtractionService`) that are used during the ontology module selection process to recognize ontology classes given in the input text
- manage all ontologies in an SQL database (`de.aquadiva.joyce.base.services.IOntologyDBService`)

and some more services.

This project does not have any application of its own but just offers services and a domain model (see the package `de.aquadiva.joyce.base.data` for details) to work with ontologies and ontology modules within JOYCE.

## Services

This section documents the purpose and functionality of some services that should be understood when working on JOYCE. Please do always also consider the documentation in the respective implementation class.
Note that all services are identified by their interface here. There is always an implementing class containing the actual code.

### de.aquadiva.joyce.base.services.IMetaConceptService

A *meta class* within JOYCE is an artificial class representing a set of original ontology classes that have been mapped to each other. Thus, when the class "rock" appears in two different ontologies and there is a mapping between them, saying they are of equal or very close semantics, we create a meta class representing both classes at once. This can enhance ontology class detection in text since one class might have more synonyms that the other. More importantly, the selection algorithm doesn't have the issue that a lot of seemingly different classes were found in the input text for which a lot of different ontologies would be required to cover them all when they are actually the same class. Thus, the meta concepts are an essential tool for minimizing the output set of ontology modules.

Meta classes are created by importing all ontology classes and all class mappings into a Neo4j graph database via the *INeo4jService*. Then, a file that maps ontology class IRIs to meta concept identifiers is output. This is done in the ISetupService of the joyce-processes project.

### de.aquadiva.joyce.base.services.IOntologyDBService

The ontologies, ontology modules and their contant scores (i.e. scoring that is independent from the input text for module selection) are stored in an SQL database. By default, this is a file database. JOYCE employs The [Java Persistence API](https://de.wikipedia.org/wiki/Java_Persistence_API) (JPA) and uses the [Hibernate](http://hibernate.org/) ORM system as implementation layer.

### de.aquadiva.joyce.base.services.IOntologyDownloadService and de.aquadiva.joyce.base.services.IOntologyNameExtractionService

These services are based on an external library, the [julielab-bioportal-ontology-tools](https://github.com/JULIELab/julielab-bioportal-ontology-tools) that downloads ontologies and mappings from BioPortal and extracts names and the taxonomical structure from ontologies.

### de.aquadiva.joyce.base.services.INeo4jService

Relies on the [julielab-neo4j-server-plugins](https://github.com/JULIELab/julielab-neo4j-server-plugins), especially the *julielab-neo4j-plugins-concepts* to arrange the concept graph.


## Tests

JOYCE Base has a range of JUnit tests in `src/test/java`. Since multiple services of JOYCE Base work with ontologies, some pre-downloaded ontologies are found in `src/test/resources`. Right now, all tests should work. If, for some reason, the ontologies must be download anew, check out the class `de.aquadiva.joyce.DownloadTestOntologies` and read its class comment carefully.
