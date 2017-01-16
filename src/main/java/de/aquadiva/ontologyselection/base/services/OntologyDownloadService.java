package de.aquadiva.ontologyselection.base.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.aquadiva.ontologyselection.OSSymbolConstants;
import de.aquadiva.ontologyselection.base.data.bioportal.OntologyInformation;
import de.aquadiva.ontologyselection.base.data.bioportal.OntologySubmission;

public class OntologyDownloadService implements IOntologyDownloadService {
	public static final String REST_URL_BIOPORTAL = "http://data.bioontology.org";
	public static final String JSON_DIR = "meta-json";
	public static final String ONTO_DIR = "ontologies";
	/**
	 * File name part identifying a JSON meta information file as being the
	 * submission meta data.
	 */
	public static final String SUBMISSION = "submission";
	public static final String SUBMISSIONS = "submissions";
	public static final String PROJECTS = "projects";
	public static final String ANALYTICS = "analytics";
	private String apiKey;
	private File jsonDir;
	private File ontoDir;
	private Gson gson;
	private Logger log;
	private File errorFile;

	public OntologyDownloadService(Logger log, @Symbol(OSSymbolConstants.ONTOLOGY_DOWNLOAD_DIR) String downloadDir,
			@Symbol(OSSymbolConstants.BIOPORTAL_API_KEY) String apiKey,
			@Symbol(OSSymbolConstants.ONTOLOGY_DOWNLOAD_ERROR_FILE) File errorFile) {
		this.log = log;
		this.apiKey = apiKey;
		this.errorFile = errorFile;

		this.jsonDir = new File(downloadDir + File.separator + JSON_DIR);
		this.ontoDir = new File(downloadDir + File.separator + ONTO_DIR);

		if (!jsonDir.exists())
			jsonDir.mkdirs();
		if (!ontoDir.exists())
			ontoDir.mkdirs();

		this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
	}

	@Override
	public void downloadBioPortalOntologies(File downloadDir, File jsonMetaDir, String... requestedAcronyms) {
		if (errorFile.exists())
			errorFile.delete();
		// We need sorting for binary search below
		Arrays.sort(requestedAcronyms);
		log.info("Downloading BioPortal ontologies to {}. {}.", downloadDir,
				requestedAcronyms.length == 0 ? "No restrictions on downloaded ontologies imposed"
						: "Ontology download is restricted to the ontologies with the following acronyms: "
								+ StringUtils.join(requestedAcronyms, ", "));

		log.info("Requesting information about all ontologies from BioPortal...");
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(
					BioPortalUtil.getFromUrl(new URL(REST_URL_BIOPORTAL + "/ontologies/"), this.apiKey));
		} catch (Exception e) {
			log.error("Error while retrieving ontology list from BioPortal, aborting:", e);
			return;
		}

		// iterate over ontologies
		Iterator<Object> iterator = jsonArray.iterator();
		int downloadedOntos = 0;
		String acronym = null;
		while (iterator.hasNext()) {
			try {
				JSONObject ontInfo = (JSONObject) iterator.next();

				OntologyInformation inf = gson.fromJson(ontInfo.toCompactString(), OntologyInformation.class);

				// String acronym = ontInfo.getString("acronym");
				acronym = inf.acronym;
				// If the acronyms of ontologies to download are specified, skip
				// those ontologies that have not been
				// specified.
				if (requestedAcronyms.length > 0 && Arrays.binarySearch(requestedAcronyms, acronym) < 0)
					continue;

				log.info("Downloading ontology with acronym \"{}\".", acronym);

				try (BufferedWriter br = new BufferedWriter(
						new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(
								new File(jsonDir.getAbsolutePath() + File.separator + acronym + ".json.gz")))))) {
					br.write(ontInfo.toString());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// if not a summary and of type ontology
				// String summaryOnly = "";
				// summaryOnly = ontInfo.getString("summaryOnly");
				boolean summaryOnly = inf.summaryOnly;

				// String type = "";
				// type = ontInfo.getString("@type");

				String type = inf.type;

				if (summaryOnly)
					log.info("Skipping ontology {} because it is just a summary", acronym);

				// we just want real ontologies
				if (!summaryOnly && type.equals("http://data.bioontology.org/metadata/Ontology")) {
					// collect information

					log.debug("Fetching latest submission from BioPortal");
					int retries = 0;
					String jsonLatestSubmission = null;
					while (retries < 10) {
						try {
							log.debug("Trying to download submission for the {}. time", retries + 1);
							String requestedSubmissionProperties = "include=submissionId,ontology,released,contact,status,description,creationDate,version,publication,hasOntologyLanguage,homepage,documentation,synonymProperty,definitionProperty,prefLabelProperty";
							jsonLatestSubmission = BioPortalUtil.getFromUrl(new URL(inf.links.latest_submission.toString() + "?" + requestedSubmissionProperties), this.apiKey);
							break;
						} catch (IOException e) {
							log.error("Error occured when trying to retrieve latest submission of ontology " + acronym
									+ ":", e);
							// HTTP 504 = gateway timeout
							// try again
							if (e.getMessage().contains("504")) {
								log.info("Error was a gateway timeout. Waiting an hour and then retry.");
								Thread.sleep(3600000);
								++retries;
							} else {
								log.info("Unrecoverable error, skipping this ontology");
								throw e;
							}
						} catch (Exception e) {
							throw e;
						}
					}

					String submissionPath = jsonDir.getAbsolutePath() + File.separator + acronym + "." + SUBMISSION
							+ ".json.gz";
					log.debug("Storing submission data to file {}", submissionPath);
					try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
							new GZIPOutputStream(new FileOutputStream(new File(submissionPath)))))) {
						br.write(new JSONObject(jsonLatestSubmission).toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					log.debug("Fetching submission links from BioPortal");
					// get information related to the submissions of this
					// ontology to BioPortal
					String jsonSubmissions = BioPortalUtil.getFromUrl(inf.links.submissions, this.apiKey);

					String submissionLinksPath = jsonDir.getAbsolutePath() + File.separator + acronym + "."
							+ SUBMISSIONS + ".json.gz";
					log.debug("Storing submission links data to file {}", submissionLinksPath);
					try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
							new GZIPOutputStream(new FileOutputStream(new File(submissionLinksPath)))))) {
						br.write(new JSONArray(jsonSubmissions).toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// get information related to the projects that are using
					// this ontology
					String jsonProjects = BioPortalUtil.getFromUrl(inf.links.projects, this.apiKey);

					try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
							new GZIPOutputStream(new FileOutputStream(new File(jsonDir.getAbsolutePath()
									+ File.separator + acronym + "." + PROJECTS + ".json.gz")))))) {
						br.write(new JSONArray(jsonProjects).toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// get information related to usage statistics of the
					// ontologie's website on BioPortal
					String jsonAnalytics = BioPortalUtil.getFromUrl(inf.links.analytics, this.apiKey);

					try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
							new GZIPOutputStream(new FileOutputStream(new File(jsonDir.getAbsolutePath()
									+ File.separator + acronym + "." + ANALYTICS + ".json.gz")))))) {
						br.write(new JSONObject(jsonAnalytics).toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					OntologySubmission latestSubmission = gson.fromJson(jsonLatestSubmission, OntologySubmission.class);

					// download ontology and store it to a file
					try {
						downloadOntologyFile(inf, latestSubmission);
						++downloadedOntos;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				try {
					FileUtils.write(errorFile,
							"Exception occurred during download of ontology "
									+ (acronym != null ? acronym : "<ID could not be retrieved>") + ": "
									+ e.getMessage() + "\n",
							"UTF-8", true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		log.info("Ontology download has finished. {} ontologies and their meta data have been downloaded to {}.",
				downloadedOntos, downloadDir);

	}

	@Override
	public void downloadBioPortalOntologiesToConfigDirs(String... validAcronyms) {
		downloadBioPortalOntologies(ontoDir, jsonDir, validAcronyms);
	}

	// private String getFromUrl(String url) {
	// try {
	// return BioPortalUtil.getFromUrl(new URL(url), this.apiKey);
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * Downloads a file from the given URL. The default file name is taken, if
	 * the HTTP response-header does not provide a file name.
	 * 
	 * @param submission
	 * 
	 * @param downloadUrl
	 *            URL of the file to be downloaded
	 * @param defaultFileName
	 *            default file name
	 * @throws IOException
	 */
	private void downloadOntologyFile(OntologyInformation ontoInf, OntologySubmission submission) throws IOException {

		// establish connection
		log.trace("Downloading ontology {} from {}.", ontoInf.acronym, ontoInf.links.download);
		HttpURLConnection conn = (HttpURLConnection) ontoInf.links.download.openConnection();
		conn.setRequestProperty("Authorization", "apikey token=" + apiKey);

		// if the request was successful, ...
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {

			// get file name
			String fileName = ontoInf.acronym + "." + submission.hasOntologyLanguage.toLowerCase() + ".gz";

			// get input stream
			try (InputStream is = conn.getInputStream()) {
				String path = ontoDir.getAbsolutePath() + File.separator + fileName;

				// write to file
				try (OutputStream os = new GZIPOutputStream(new FileOutputStream(path))) {

					int bytesRead = -1;
					byte[] buffer = new byte[4096];
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
				}
			}
		} else {
			// okay, there could possibly another reason for this error but for now only the message that there is nothing to download has occurred
			throw new IllegalStateException(
					"Ontology with acronym " + ontoInf.acronym + " does not yet have a file ready for download.");
		}

		conn.disconnect();
	}

}
