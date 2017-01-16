package de.aquadiva.ontologyselection.base.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.slf4j.Logger;

import de.aquadiva.ontologyselection.base.data.IOntology;

/**
 * A service for parsing OWL ontologies. This has been made a service to have a
 * central facility for the parsing settings.
 * 
 * @author faessler
 * 
 */
public class OWLParsingService implements IOWLParsingService {

	private OWLOntologyManager owlOntologyManager;
	private Logger log;

	public OWLParsingService(Logger log) {
		this.log = log;
		owlOntologyManager = OWLManager.createOWLOntologyManager();
	}

	public OWLOntology parse(File ontologyFile) throws IOException {
		byte[] ontologyData = IOUtils.toByteArray(new FileInputStream(ontologyFile));
		try {
			OWLOntology owlOntology = parse(ontologyData);
			String filename = ontologyFile.getName();
			String acronym = filename.substring(0, filename.indexOf('.'));
			renameTEMPOntology(owlOntology, acronym);
			return owlOntology;
		} catch (OWLOntologyAlreadyExistsException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OWLOntology parse(byte[] ontologyData) throws OWLOntologyAlreadyExistsException {
		try {
			long time = System.currentTimeMillis();
			InputStream is = new ByteArrayInputStream(ontologyData);
			// check if the byte array is encoding a gzip stream, the first to
			// bytes should have a signiture, the gzip magic number
			if (ontologyData[0] == (byte) 0x1f && ontologyData[1] == (byte) 0x8b) {
				is = new GZIPInputStream(is);
			}
			OWLOntology o = owlOntologyManager.loadOntologyFromOntologyDocument(is);
			time = System.currentTimeMillis() - time;
			log.debug("Parsed OWL ontology {} in {}ms ({}s)", new Object[] { o.getOntologyID(), time, time / 1000 });
			return o;
		} catch (org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException e) {
			log.warn(
					"The ontology with ID {} was requested for repeated parsing. The already existing ontology is returned. However, we should check if the ontology IDs are really unique. Because, when not, we would return the wrong ontology at some point.");
			return owlOntologyManager.getOntology(e.getOntologyID());
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OWLOntology parse(IOntology ontology) throws IOException {
		OWLOntology owlOntology;
		try {
			owlOntology = ontology.getFile() != null && ontology.getFile().exists() ? parse(ontology.getFile())
					: parse(ontology.getOntologyData());
		} catch (OWLOntologyAlreadyExistsException e) {
			log.error(
					"Could parse ontology {} because the ID of the actual OWL ontology already exists on another ontology parsed before.");
			throw new RuntimeException(e);
		}
		renameTEMPOntology(owlOntology, ontology.getId());
		ontology.setOwlOntology(owlOntology);
		// Remove the ontology again because we get problems with duplicate
		// names; hopefully this has not unwanted side effects;TODO perhaps we
		// should remove the ontologies after parsing in each method. I do it
		// here because the SetupService uses this method
		// UPDATE: But then we get issues with LocalityModularization: first the
		// ontology is parsed, then removed and then required by the module
		// extraction algorithm where the Exception "unknown ontology" would
		// appear....
		// owlOntologyManager.removeOntology(owlOntology);
		return owlOntology;
	}

	/**
	 * If the OWLOntology ID equals 'http://purl.obolibrary.org/obo/TEMP' -
	 * which happens after OBO-conversions - the ontology is renamed to
	 * <tt>http://purl.obolibrary.org/obo/&lt;newname&gt;</tt> It is important
	 * to do this because otherwise multiple ontologies (i.e. all the OBO
	 * converted ones) will have the same ID which the OWL ontology manager
	 * won't accept.
	 * 
	 * @param owlOntology
	 * @param newname
	 */
	private void renameTEMPOntology(OWLOntology owlOntology, String newname) {
		// Most OBO ontologies do not set their ontology name; if so, we just
		// set some IRI that should be unique due to the use of the ontology's
		// acronym
		if (owlOntology.getOntologyID().getOntologyIRI() != null && owlOntology.getOntologyID().getOntologyIRI()
				.equals(IRI.create("http://purl.obolibrary.org/obo/TEMP"))) {
			SetOntologyID setOntologyID = new SetOntologyID(owlOntology,
					IRI.create("http://purl.obolibrary.org/obo/" + newname));
			owlOntologyManager.applyChange(setOntologyID);
		}
	}

	@Override
	public void convertOntology(File obofile, File owlfile) throws IOException {
		OWLOntology owlOntology = parse(obofile);
		File dir = owlfile.getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		try (OutputStream os = new GZIPOutputStream(new FileOutputStream(owlfile))) {
			owlOntologyManager.saveOntology(owlOntology, new RDFXMLOntologyFormat(), os);
		} catch (OWLOntologyStorageException e) {
			throw new IOException(e);
		}
		owlOntologyManager.removeOntology(owlOntology);
	}

	@Override
	public OWLOntologyManager getOwlOntologyManager() {
		return owlOntologyManager;
	}

	@Override
	public void removeIntology(IRI ontologyIri) {
		OWLOntologyID ontologyID = new OWLOntologyID(ontologyIri);
		if (owlOntologyManager.getOntology(ontologyID) != null) {
			log.debug("Removing ontology with IRI {} from OWLOntologyManager.", ontologyIri.toString());
			owlOntologyManager.removeOntology(ontologyID);
		}
	}
}
