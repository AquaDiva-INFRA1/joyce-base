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

## Tests

JOYCE Base has a range of JUnit tests in `src/test/java`. Since multiple services of JOYCE Base work with ontologies, some pre-downloaded ontologies are found in `src/test/resources`. Right now, all tests should work. If, for some reason, the ontologies must be download anew, check out the class `de.aquadiva.joyce.DownloadTestOntologies` and read its class comment carefully.
