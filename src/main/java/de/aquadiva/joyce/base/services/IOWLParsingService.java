package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.aquadiva.joyce.base.data.IOntology;

public interface IOWLParsingService {
	OWLOntology parse(File ontologyFile) throws IOException;

	OWLOntology parse(byte[] ontologyData) throws OWLOntologyAlreadyExistsException;

	/**
	 * Retrieves the OWL data from <tt>ontology</tt> from its OWL data field or
	 * from the file referenced by <tt>ontology</tt>. If the data is found and
	 * successfully parsed into an {@link OWLOntology} instance, it is then
	 * directly set to <tt>ontology</tt> and returned for convenience.
	 * 
	 * @param ontology
	 * @return
	 * @throws IOException
	 */
	OWLOntology parse(IOntology ontology) throws IOException;

	public void convertOntology(File obofile, File owlfile) throws IOException;

	OWLOntologyManager getOwlOntologyManager();

	void clearOntologies();
}
