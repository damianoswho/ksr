package pl.damianolczyk.ksr.dto;

import java.util.ArrayList;
import java.util.List;

public class Article {
	private String date;
	private List<String> topics;
	private List<String> places;
	private List<String> people;
	private List<String> orgs;
	private List<String> exchanges;
	private List<String> companies;
	private String title;
	private String body;
	private String stemmedBody;

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

}
