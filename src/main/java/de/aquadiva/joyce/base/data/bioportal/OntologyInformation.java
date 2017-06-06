package de.aquadiva.joyce.base.data.bioportal;

import com.google.gson.annotations.SerializedName;

@Deprecated
public class OntologyInformation {
	public String acronym;
	public boolean summaryOnly;
	@SerializedName("@type")
	public String type;
	public OntologyLinks links;
}
