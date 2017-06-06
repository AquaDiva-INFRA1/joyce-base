package de.aquadiva.joyce.base.services;

import static de.aquadiva.joyce.JoyceSymbolConstants.HIBERNATE_HBM2DDL_AUTO;
import static de.aquadiva.joyce.JoyceSymbolConstants.HIBERNATE_JDBC_BATCH_SIZE;
import static de.aquadiva.joyce.JoyceSymbolConstants.HIBERNATE_SHOW_SQL;
import static de.aquadiva.joyce.JoyceSymbolConstants.JPA_JDBC_DRIVER;
import static de.aquadiva.joyce.JoyceSymbolConstants.JPA_JDBC_PW;
import static de.aquadiva.joyce.JoyceSymbolConstants.JPA_JDBC_URL;
import static de.aquadiva.joyce.JoyceSymbolConstants.JPA_JDBC_USER;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.julielab.bioportal.util.BioPortalToolUtils;

public class JoyceBaseModule {

	public static void contributeSymbolSource(Logger logger, final OrderedConfiguration<SymbolProvider> configuration) {
		try {
			String configFileName = System.getProperty(JoyceSymbolConstants.JOYCE_CONFIG_FILE);
			if (configFileName == null)
				configFileName = "configuration.properties";
			File configurationFile = new File(configFileName);
			if (configurationFile.exists()) {
				logger.info("Found configuration file {}", configurationFile.getAbsolutePath());
				Properties properties = new Properties();
				properties.load(BioPortalToolUtils.getInputStreamFromFile(configurationFile));
				Map<String, String> map = new HashMap<>();
				for (final String name : properties.stringPropertyNames())
					map.put(name, properties.getProperty(name));
				MapSymbolProvider mapSymbolProvider = new MapSymbolProvider(map);
				configuration.add("JoyceSymbols", mapSymbolProvider, "before:ApplicationDefaults");

			} else {
				ClasspathResourceSymbolProvider resSmbProv = new ClasspathResourceSymbolProvider(configFileName);
				configuration.add("JoyceSymbols", resSmbProv, "before:ApplicationDefaults");
				logger.info("Found configuration file on classpath as {}", configFileName);
			}
		} catch (NullPointerException e) {
			logger.info("No configuration file found in the classpath");
		} catch (IOException e) {
			logger.error("Error when reading configuration file: ", e);
		}
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(IOWLParsingService.class, OWLParsingService.class);
		binder.bind(IOntologyDownloadService.class, OntologyDownloadService.class);
		binder.bind(IOntologyFormatConversionService.class, OntologyFormatConversionService.class);
		binder.bind(IOntologyDBService.class, OntologyDBService.class);
		binder.bind(IMetaConceptService.class, MetaConceptService.class);
		binder.bind(IOntologyRepositoryStatsPrinterService.class, OntologyRepositoryStatsPrinterService.class);
	}

	public static EntityManagerFactory buildEntityManagerFactory(
			@Symbol(JoyceSymbolConstants.PERSISTENCE_CONTEXT) String persistenceContext,
			@Symbol(JPA_JDBC_DRIVER) String driver, @Symbol(JPA_JDBC_URL) String url,
			@Symbol(JPA_JDBC_USER) String user, @Symbol(JPA_JDBC_PW) String password,
			@Symbol(HIBERNATE_SHOW_SQL) String showSql, @Symbol(HIBERNATE_HBM2DDL_AUTO) String hbm2ddlAuto,
			@Symbol(HIBERNATE_JDBC_BATCH_SIZE) String batchsize) {
		Map<String, String> settings = new HashMap<>();
		settings.put(JPA_JDBC_DRIVER, driver);
		settings.put(JPA_JDBC_URL, url);
		settings.put(JPA_JDBC_USER, user);
		settings.put(JPA_JDBC_PW, password);
		settings.put(HIBERNATE_SHOW_SQL, showSql);
		settings.put(HIBERNATE_HBM2DDL_AUTO, hbm2ddlAuto);
		settings.put(HIBERNATE_JDBC_BATCH_SIZE, batchsize);
		return Persistence.createEntityManagerFactory(persistenceContext, settings);
	}

	/**
	 * JPA EntityManager instances are lightweight wrappers around JDBC
	 * connections and NOT threadsafe. Thus, they can be stored in service
	 * singletons on a per-thread basis.
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Scope(value = "perthread")
	public static EntityManager buildEntityManager(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}

	public static ExecutorService buildExecutorService(Logger log, RegistryShutdownHub shutdownHub) {
		OntologySelectionExecutorService executorService = new OntologySelectionExecutorService(log,
				Executors.newFixedThreadPool(12));
		executorService.startupService(shutdownHub);
		return executorService;
	}
}
