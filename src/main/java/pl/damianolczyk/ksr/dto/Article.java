package pl.damianolczyk.ksr.dto;

import java.util.ArrayList;
import java.util.List;

public class Article {

	private String date;
	private List<String> topics;
	private List<String> places;
	private String clasifiedPlace;
	private List<String> people;
	private List<String> orgs;
	private List<String> exchanges;
	private List<String> companies;
	private String title;
	private String body;
	private String stemmedBody;
	private List<Double> attributes;

	public Article() {
		date = "";
		topics = new ArrayList<String>();
		places = new ArrayList<String>();
		people = new ArrayList<String>();
		orgs = new ArrayList<String>();
		exchanges = new ArrayList<String>();
		companies = new ArrayList<String>();
		title = "";
		body = "";
		stemmedBody = "";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public List<String> getPlaces() {
		return places;
	}

	public void setPlaces(List<String> places) {
		this.places = places;
	}

	public String getClasifiedPlace() {
		return clasifiedPlace;
	}

	public void setClasifiedPlace(String clasifiedPlace) {
		this.clasifiedPlace = clasifiedPlace;
	}

	public List<String> getPeople() {
		return people;
	}

	public void setPeople(List<String> people) {
		this.people = people;
	}

	public List<String> getOrgs() {
		return orgs;
	}

	public void setOrgs(List<String> orgs) {
		this.orgs = orgs;
	}

	public List<String> getExchanges() {
		return exchanges;
	}

	public void setExchanges(List<String> exchanges) {
		this.exchanges = exchanges;
	}

	public List<String> getCompanies() {
		return companies;
	}

	public void setCompanies(List<String> companies) {
		this.companies = companies;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String content) {
		this.body = content;
	}

	public String getStemmedBody() {
		return stemmedBody;
	}

	public void setStemmedBody(String stemmedBody) {
		this.stemmedBody = stemmedBody;
	}

	public List<Double> getAttributes() {
		return attributes;
	}

	public List<Double> getAttributes(List<Boolean> selectedAttributes) {
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < attributes.size(); i++) {
			if (selectedAttributes.get(i).booleanValue()) {
				result.add(attributes.get(i));
			}
		}
		return result;
	}

	public void setAttributes(List<Double> attributes) {
		this.attributes = attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((companies == null) ? 0 : companies.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((exchanges == null) ? 0 : exchanges.hashCode());
		result = prime * result + ((orgs == null) ? 0 : orgs.hashCode());
		result = prime * result + ((people == null) ? 0 : people.hashCode());
		result = prime * result + ((places == null) ? 0 : places.hashCode());
		result = prime * result + ((stemmedBody == null) ? 0 : stemmedBody.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((topics == null) ? 0 : topics.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (companies == null) {
			if (other.companies != null)
				return false;
		} else if (!companies.equals(other.companies))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (exchanges == null) {
			if (other.exchanges != null)
				return false;
		} else if (!exchanges.equals(other.exchanges))
			return false;
		if (orgs == null) {
			if (other.orgs != null)
				return false;
		} else if (!orgs.equals(other.orgs))
			return false;
		if (people == null) {
			if (other.people != null)
				return false;
		} else if (!people.equals(other.people))
			return false;
		if (places == null) {
			if (other.places != null)
				return false;
		} else if (!places.equals(other.places))
			return false;
		if (stemmedBody == null) {
			if (other.stemmedBody != null)
				return false;
		} else if (!stemmedBody.equals(other.stemmedBody))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (topics == null) {
			if (other.topics != null)
				return false;
		} else if (!topics.equals(other.topics))
			return false;
		return true;
	}
}
