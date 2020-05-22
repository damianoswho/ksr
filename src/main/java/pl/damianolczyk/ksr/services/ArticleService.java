package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.Constants;

@Service
public class ArticleService {

	public List<Article> convertStringToArticles(String articles) {
		List<Article> articleList = new ArrayList<Article>();
		Arrays.asList(articles.replaceAll("<!DOCTYPE.*>", "").replaceAll("<REUTERS.*>", "").replaceAll("\n", "")
				.split("</REUTERS>")).forEach(singleArticle -> {
					articleList.add(convertArticleFromSgmlToObject(singleArticle));
				});
		return getArticlesWithChosenPlace(getArticlesWithSinglePlace(articleList));
	}

	private Article convertArticleFromSgmlToObject(String article) {
		Article result = new Article();
		Matcher matcher = Constants.DATE_PATTERN.matcher(article);
		if (matcher.find())
			result.setDate(matcher.group(1).trim());
		matcher = Constants.TOPICS_PATTERN.matcher(article);
		if (matcher.find())
			result.setTopics(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.PLACES_PATTERN.matcher(article);
		if (matcher.find())
			result.setPlaces(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.PEOPLE_PATTERN.matcher(article);
		if (matcher.find())
			result.setPeople(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.ORGS_PATTERN.matcher(article);
		if (matcher.find())
			result.setOrgs(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.EXCHANGES_PATTERN.matcher(article);
		if (matcher.find())
			result.setExchanges(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.COMPANIES_PATTERN.matcher(article);
		if (matcher.find())
			result.setCompanies(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = Constants.TITLE_PATTERN.matcher(article);
		if (matcher.find())
			result.setTitle(matcher.group(1));
		matcher = Constants.BODY_PATTERN.matcher(article);
		if (matcher.find()) {
			result.setBody(matcher.group(1));
			result.setStemmedBody(removeStopWordsAndStem(matcher.group(1).toLowerCase()));
		}
		return result;
	}

	private List<Article> getArticlesWithSinglePlace(List<Article> articles) {
		return articles.stream().filter(article -> article.getPlaces().size() == 1).collect(Collectors.toList());
	}

	private List<Article> getArticlesWithChosenPlace(List<Article> articles) {
		return articles.stream()
				.filter(article -> Constants.CHOSEN_PLACES.contains(article.getPlaces().get(0).toLowerCase()))
				.collect(Collectors.toList());
	}

	private String removeStopWordsAndStem(String input) {
		List<String> result = new ArrayList<String>();
		Arrays.asList(input.replaceAll("[^a-z. ]", "").split("\\s")).forEach(word -> {
			if (!Arrays.asList(Constants.STOPWORDS).contains(word) && word.length() > 1) {
				EnglishStemmer stem = new EnglishStemmer();
				stem.setCurrent(word);
				stem.stem();
				result.add(stem.getCurrent());
			}
		});
		return Strings.join(result, ' ');
	}

	public List<Article> getTeachingSet(List<Article> articles, int sizeOfTeachingSet, Boolean includeDisproportion) {
		Set<Article> setOfArticles = new HashSet<>();
		List<Article> result = new ArrayList<>();
		int maxNumberOfArticlesFromSinglePlace = sizeOfTeachingSet / Constants.CHOSEN_PLACES.size();
		Constants.CHOSEN_PLACES.forEach(chosenPlace -> {
			List<Article> chosenArticles = articles.stream()
					.filter(article -> article.getPlaces().get(0).contentEquals(chosenPlace))
					.collect(Collectors.toList());
			if (chosenArticles.size() > maxNumberOfArticlesFromSinglePlace) {
				chosenArticles = chosenArticles.subList(0, maxNumberOfArticlesFromSinglePlace);
			}
			setOfArticles.addAll(chosenArticles);
		});
		if (!includeDisproportion) {
			// uzupełnianie listy po uzyskaniu optymalnej liczby artykułów do nauki
			for (Article article : articles) {
				if (setOfArticles.size() == sizeOfTeachingSet)
					break;
				setOfArticles.add(article);
			}
		}
		result.addAll(setOfArticles);
		return result;
	}
}
