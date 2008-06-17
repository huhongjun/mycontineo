package org.contineo.core.searchengine.search;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.contineo.core.text.StringParser;

/**
 * @author Michael Scholz
 */
public class SearchOptions {

	private String queryStr = "";

	private Integer lengthMin = null;

	private Integer lengthMax = null;

	private String format = "";

	private boolean fuzzy = false;

	private String username = "";

	private String[] fields = null;

	private String[] languages = null;

	private Date creationDateFrom = null;

	private Date creationDateTo = null;

	private Date sourceDateFrom = null;

	private Date sourceDateTo = null;

	/** Creates a new instance of SearchOptions */
	public SearchOptions() {
	}

	public String getQueryStr() {
		return queryStr;
	}

	public String getFormat() {
		return format;
	}

	public boolean getFuzzy() {
		return fuzzy;
	}

	public String getUsername() {
		return username;
	}

	public String[] getFields() {
		return fields;
	}

	public void setQueryStr(String query) {
		queryStr = query;
	}

	public void setQueryStr(String query, String phrase, String any, String not) {
		if (fuzzy) {
			StringParser sp = new StringParser(query);
			Collection collquery = sp.getWordTable();
			Iterator iter = collquery.iterator();

			while (iter.hasNext()) {
				String word = (String) iter.next();
				queryStr += word + "~ ";
			}
		} else {
			queryStr = query;
		}

		if ((phrase != null) && !phrase.equals("")) {
			queryStr += " \"" + phrase + "\"";
		}

		if ((any != null) && !any.equals("")) {
			boolean first = true;
			StringParser sp = new StringParser(any);
			Collection collany = sp.getWordTable();
			Iterator iter = collany.iterator();

			while (iter.hasNext()) {
				String word = (String) iter.next();

				if (!first) {
					queryStr += " OR";
				} else {
					first = false;
				}

				queryStr += " " + word;

				if (fuzzy) {
					queryStr += "~";
				}
			}
		}

		if ((not != null) && !not.equals("")) {
			queryStr += " NOT (" + not + ")";
		}
	}

	public void setFormat(String form) {
		format = form;
	}

	public void setFuzzy(boolean fuz) {
		fuzzy = fuz;
	}

	public void setUsername(String name) {
		username = name;
	}

	public void setFields(String[] flds) {
		fields = flds;
	}

	public void addField(String s) {
		fields[fields.length] = s;
	}

	public String[] getLanguages() {
		return languages;
	}

	public void setLanguages(String[] languages) {
		this.languages = languages;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public Integer getLengthMin() {
		return lengthMin;
	}

	public void setLengthMin(Integer lengthMin) {
		this.lengthMin = lengthMin;
	}

	public Integer getLengthMax() {
		return lengthMax;
	}

	public void setLengthMax(Integer lengthMax) {
		this.lengthMax = lengthMax;
	}

	public Date getSourceDateFrom() {
		return sourceDateFrom;
	}

	public void setSourceDateFrom(Date sourceDateFrom) {
		this.sourceDateFrom = sourceDateFrom;
	}

	public Date getSourceDateTo() {
		return sourceDateTo;
	}

	public void setSourceDateTo(Date sourceDateTo) {
		this.sourceDateTo = sourceDateTo;
	}
}