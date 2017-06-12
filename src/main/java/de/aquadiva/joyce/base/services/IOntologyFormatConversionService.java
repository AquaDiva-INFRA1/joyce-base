package de.aquadiva.joyce.base.services;

import java.io.File;

import de.aquadiva.joyce.JoyceSymbolConstants;

public interface IOntologyFormatConversionService {
	/**
	 * Converts all ontologies in <tt>sourceDir</tt> into the OWL format and stores the result in <tt>targetDir</tt>.
	 * Ontologies already present in OWL format are merely copied.
	 * 
	 * @param directory
	 */
	void convertToOwl(File sourceDir, File targetDir);

	/**
	 * Calls {@link #convertToOwl(File, File)} by using the values of the configuration symbols
	 * {@link JoyceSymbolConstants#ONTOLOGIES_DOWNLOAD_DIR} and {@link JoyceSymbolConstants#OWL_DIR} as places to look for
	 * ontologies to convert and the place to store the results.
	 */
	void convertFromDownloadDirToOwlDir();
}
