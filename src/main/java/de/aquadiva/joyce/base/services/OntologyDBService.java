package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;

import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologyModule;
import de.aquadiva.joyce.base.data.bioportal.OntologySubmission;
import de.aquadiva.joyce.base.data.bioportal.ProjectInformation;
import de.julielab.bioportal.ontologies.BioPortalToolConstants;

public class OntologyDBService implements IOntologyDBService {

	private Logger log;
	private File owlDir;
	private File jsonDir;
	private Gson gson;
	private EntityManager em;
	private EntityManagerFactory entityManagerFactory;

	public OntologyDBService(Logger log,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_INFO_DOWNLOAD_DIR) String ontologyInfoDir,
			@Symbol(JoyceSymbolConstants.OWL_DIR) String owlDir, EntityManager entityManager) {
		this.log = log;
		this.em = entityManager;
		this.entityManagerFactory = em.getEntityManagerFactory();
		this.owlDir = new File(owlDir);
		this.jsonDir = new File(ontologyInfoDir);

		this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
	}

	@Override
	public List<Ontology> importBioPortalOntologies(File owlDir, File metaDir) {
		List<Ontology> createdOntologies = new ArrayList<>();
		File[] jsonMetas = metaDir.listFiles();
		// Map<String, File> ontoInfByAcronym = new HashMap<>();
		Map<String, File> submissionByAcronym = new HashMap<>();
		Map<String, File> submissionsByAcronym = new HashMap<>();
		Map<String, File> projectsByAcronym = new HashMap<>();
		Map<String, File> analyticsByAcronym = new HashMap<>();
		for (int i = 0; i < jsonMetas.length; i++) {
			File metaFile = jsonMetas[i];
			String acronym = metaFile.getName().split("\\.", 2)[0];
			if (metaFile.getName().contains(BioPortalToolConstants.SUBMISSIONS_EXT)) {
				submissionsByAcronym.put(acronym, metaFile);
			} else if (metaFile.getName().contains(BioPortalToolConstants.SUBMISSION_EXT)) {
				submissionByAcronym.put(acronym, metaFile);
			} else if (metaFile.getName().contains(BioPortalToolConstants.PROJECTS_EXT)) {
				projectsByAcronym.put(acronym, metaFile);
			} else if (metaFile.getName().contains(BioPortalToolConstants.ANALYTICS_EXT)) {
				analyticsByAcronym.put(acronym, metaFile);
			}
			// else {
			// ontoInfByAcronym.put(acronym, metaFile);
			// }
		}

		em.getTransaction().begin();
		File[] owlOntologies = owlDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals(".DS_Store"))
					return false;
				return true;
			}
		});
		if (owlOntologies == null) {
			log.info("No OWL ontologies found in directory {}", owlDir);
			owlOntologies = new File[0];
		}
		for (int i = 0; i < owlOntologies.length; i++) {
			File owlFile = owlOntologies[i];
			log.info("Reading file {} for database import.", owlFile.getAbsolutePath());
			String acronym = owlFile.getName().split("\\.", 2)[0];
			try {
				log.debug("Checking if ontology {} is already in the database", acronym);
				Ontology o = em.getReference(Ontology.class, acronym);
				// This will throw the "EntityNotFoundException" if there is no
				// ontology with this acronym in the database.
				o.toString();
				createdOntologies.add(o);
				log.debug("Ontology {} already present in database, not importing again.", acronym);
			} catch (EntityNotFoundException e1) {
				log.debug("Ontology {} was not found in the database and is now imported.", acronym);
				// File ontoInfFile = ontoInfByAcronym.get(acronym);

				// OntologyMetaData ontoInf = null;
				//
				// try (InputStream is =
				// BioPortalToolUtils.getInputStreamFromFile(ontoInfFile)) {
				// InputStreamReader reader = new InputStreamReader(is,
				// Charset.forName("UTF-8"));
				// ontoInf = gson.fromJson(reader, OntologyMetaData.class);
				// if (!acronym.equals(ontoInf.acronym))
				// throw new IllegalStateException(
				// "The contents of the file with name " +
				// ontoInfFile.getAbsolutePath()
				// + " did not match the expected BioPortal acronym " + acronym
				// + ".");
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

				// read submission information from file
				File ontoSubmFile = submissionByAcronym.get(acronym);
				OntologySubmission ontoSubm = null;
				try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(ontoSubmFile))) {
					InputStreamReader reader = new InputStreamReader(is);
					ontoSubm = gson.fromJson(reader, OntologySubmission.class);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// // read submissions information from file
				File ontoSubmsFile = submissionsByAcronym.get(acronym);
				OntologySubmission[] ontoSubms = null;
				try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(ontoSubmsFile))) {
					InputStreamReader reader = new InputStreamReader(is);
					ontoSubms = gson.fromJson(reader, OntologySubmission[].class);

				} catch (IOException e) {
					e.printStackTrace();
				}

				// read projects information from file
				File projectsFile = projectsByAcronym.get(acronym);
				ProjectInformation[] projectInfs = null;
				try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(projectsFile))) {
					InputStreamReader reader = new InputStreamReader(is);
					projectInfs = gson.fromJson(reader, ProjectInformation[].class);

				} catch (IOException e) {
					e.printStackTrace();
				}

				// read analytics information from file
				// TODO cannot be parsed by Gson, since the structure of the
				// file is
				// not fixed (see an example below)
				// {
				//
				// "ENVO": {
				//
				// "2013": {
				//
				// "1": 0,
				// "2": 0,
				// "3": 0,
				// "4": 0,
				// "5": 0,
				// "6": 0,
				// "7": 0,
				// "8": 0,
				// "9": 0,
				// "10": 479,
				// "11": 164,
				// "12": 208
				//
				// }, ...

				// fill the ontology object with the extracted data
				Ontology o = new Ontology();
				o.setFile(owlFile);
				o.setId(acronym);
				if (null != ontoSubm) {
					o.setLatestReleaseDate(ontoSubm.released);
					o.setDocumentationPage(ontoSubm.documentation);
					o.setHomepage(ontoSubm.hompepage);
					o.setStatus(ontoSubm.status);
				}

				// for each year determine frequency of submissions
				HashMap<Integer, Integer> submissionsPerYear = new HashMap<Integer, Integer>();
				for (OntologySubmission sub : ontoSubms) {
					if (null == sub.released)
						continue;
					String format = new SimpleDateFormat("yyyy").format(sub.released);

					int year = Integer.parseInt(format);
					if (!submissionsPerYear.containsKey(year)) {
						submissionsPerYear.put(year, 0);
					}

					int numOfSubs = submissionsPerYear.get(year);
					submissionsPerYear.put(year, numOfSubs + 1);
				}

				o.setSubmissions(submissionsPerYear);

				if (projectInfs != null) {
					o.setNumberOfReferencingProjects(projectInfs.length);
				}
				// TODO set further ontology fields from information given in
				// the
				// meta files
				// * create proper fields in IOntology, if necessary
				// * if the information is not yet given in one of the
				// downloaded
				// meta-files,
				// download it by making proper changes to
				// OntologyDownloadService
				// * if needed, create proper classes in
				// de.aquadiva.ontologyselection.base.data.bioportal
				// that represent the JSON information you are interested in

				// add this ontology into the database
				em.persist(o);
				createdOntologies.add(o);
			}
		}
		log.info("Committing ontology import to the database.");
		em.getTransaction().commit();
		log.info("Done.");
		return createdOntologies;
	}

	@Override
	public List<Ontology> importBioPortalOntologiesFromConfigDirs() {
		return importBioPortalOntologies(owlDir, jsonDir);
	}

	@Override
	public List<Ontology> getAllOntologies() {
		beginTransaction();
		List<Ontology> persistedOntos = em
				.createQuery("select o from Ontology o where type(o) = Ontology", Ontology.class).getResultList();
		commit();
		return persistedOntos;
	}

	@Override
	public List<Ontology> getAllOntologiesWithModularizationError() {
		beginTransaction();
		List<Ontology> persistedOntos = em
				.createQuery("select o from Ontology o where type(o) = Ontology and hasModularizationError=TRUE",
						Ontology.class)
				.getResultList();
		commit();
		return persistedOntos;
	}

	@Override
	public List<Ontology> getOntologiesWithParsingError() {
		beginTransaction();
		List<Ontology> persistedOntos = em
				.createQuery("select o from Ontology o where type(o) = Ontology and hasParsingError=TRUE",
						Ontology.class)
				.getResultList();
		commit();
		return persistedOntos;
	}

	@Override
	public List<OntologyModule> getModulesWithParsingError() {
		beginTransaction();
		List<OntologyModule> persistedOntos = em
				.createQuery("select o from OntologyModule o where hasParsingError=TRUE", OntologyModule.class)
				.getResultList();
		commit();
		return persistedOntos;
	}

	@Override
	public List<OntologyModule> getAllOntologyModules() {
		beginTransaction();
		List<OntologyModule> persistedOntoModules = em.createQuery("from OntologyModule", OntologyModule.class)
				.getResultList();
		commit();
		return persistedOntoModules;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ontology> getOntologiesByIds(String... ids) {
		// beginTransaction();
		// Session session = em.unwrap(Session.class);
		// Criteria cr = session.createCriteria(Ontology.class);
		// Criterion[] idCriterions = new Criterion[ids.length];
		// for (int i = 0; i < ids.length; i++) {
		// String id = ids[i];
		// idCriterions[i] = Restrictions.idEq(id);
		// }
		// cr.add(Restrictions.disjunction(idCriterions));
		// @SuppressWarnings("unchecked")
		// List<Ontology> list = cr.list();
		// commit();
		// return list;

		// in case we want to cache ontologies ourselves that have been loaded
		// once; most probably completely redundant because JPA does it itself
		// List<Ontology> knownOntos = new ArrayList<>();
		Set<String> idsToFetchSet = new HashSet<>();
		for (int i = 0; i < ids.length; i++) {
			idsToFetchSet.add(ids[i]);
		}
		// for (int i = 0; i < ids.length; i++) {
		// String id = ids[i];
		// Ontology onto = cache.get(id);
		// if (onto != null) {
		// idsToFetchSet.remove(onto.getId());
		// knownOntos.add((Ontology) onto);
		// }
		// }
		ArrayList<String> idsToFetchList = new ArrayList<>(idsToFetchSet);
		Set<Ontology> set = new LinkedHashSet<>();
		if (!idsToFetchList.isEmpty()) {
			beginTransaction();
			Session session = em.unwrap(Session.class);
			Criteria cr = session.createCriteria(Ontology.class);
			Criterion[] idCriterions = new Criterion[idsToFetchList.size()];
			for (int i = 0; i < idsToFetchList.size(); ++i) {
				String id = idsToFetchList.get(i);
				idCriterions[i] = Restrictions.idEq(id);
			}
			cr.add(Restrictions.disjunction(idCriterions));
			System.out.println("Criterion: " + cr);
			List<Ontology> list = cr.list();
			set.addAll(list);
			commit();
			// for (Ontology o : list)
			// cache.put(o.getId(), o);
		}
		List<Ontology> ret = new ArrayList<>();
		ret.addAll(set);
		// ret.addAll(knownOntos);
		return ret;
	}

	@Override
	public <T extends Ontology> void storeOntologies(Collection<T> ontologies, boolean commit) {
		beginTransaction();
		for (Ontology om : ontologies)
			try {
				Ontology o = em.getReference(Ontology.class, om.getId());
				// This will throw the "EntityNotFoundException" if there is no
				// ontology with this acronym in the database.
				o.toString();
			} catch (EntityNotFoundException e1) {
				em.persist(om);
			}
		if (commit)
			commit();
	}

	@Override
	public void commit() {
		em.getTransaction().commit();
	}

	@Override
	public void beginTransaction() {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
	}

	@Override
	public void shutdown() {
		if (entityManagerFactory.isOpen())
			entityManagerFactory.close();
	}

	@PostInjection
	public void startupService(RegistryShutdownHub shutdownHub) {
		shutdownHub.addRegistryShutdownListener(new Runnable() {
			public void run() {
				log.debug("Shutting down ontology database.");
				shutdown();
			}
		});
	}

}
