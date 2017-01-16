package de.aquadiva.ontologyselection.base.data.bioportal;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class OntologyInformation {
	public String acronym;
	public boolean summaryOnly;
	@SerializedName("@type")
	public String type;
	public OntologyLinks links;
}
