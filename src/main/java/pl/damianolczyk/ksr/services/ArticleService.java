package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import pl.damianolczyk.ksr.dto.Article;

@Service
public class ArticleService {
	public static String[] STOPWORDS = { "a", "as", "able", "about", "above", "according", "accordingly", "across",
			"actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone",
			"along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any",
			"anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate",
			"appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available",
			"away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before",
			"beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between",
			"beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause",
			"causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning",
			"consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could",
			"couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do",
			"does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight",
			"either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every",
			"everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few",
			"ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth",
			"four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going",
			"gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent",
			"having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit",
			"however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc",
			"indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is",
			"isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows",
			"known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like",
			"liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me",
			"mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my",
			"myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not",
			"nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on",
			"once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps",
			"placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv",
			"rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively",
			"respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see",
			"seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious",
			"seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some",
			"somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon",
			"sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken",
			"tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their",
			"theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore",
			"therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third",
			"this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
			"together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two",
			"un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used",
			"useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants",
			"was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent",
			"what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas",
			"whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos",
			"whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont",
			"wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your",
			"yours", "yourself", "yourselves", "zero", "reuter" };

	private Pattern DATE_PATTERN = Pattern.compile("<DATE>(.*)<\\/DATE>");
	private Pattern TOPICS_PATTERN = Pattern.compile("<TOPICS><D>(.*)<\\/D><\\/TOPICS>");
	private Pattern PLACES_PATTERN = Pattern.compile("<PLACES><D>(.*)<\\/D><\\/PLACES>");
	private Pattern PEOPLE_PATTERN = Pattern.compile("<PEOPLE><D>(.*)<\\/D><\\/PEOPLE>");
	private Pattern ORGS_PATTERN = Pattern.compile("<ORGS><D>(.*)<\\/D><\\/ORGS>");
	private Pattern EXCHANGES_PATTERN = Pattern.compile("<EXCHANGES><D>(.*)<\\/D><\\/EXCHANGES>");
	private Pattern COMPANIES_PATTERN = Pattern.compile("<COMPANIES><D>(.*)<\\/D><\\/COMPANIES>");
	private Pattern TITLE_PATTERN = Pattern.compile("<TITLE>(.*)<\\/TITLE>");
	private Pattern BODY_PATTERN = Pattern.compile("<BODY>(.*)<\\/BODY>");

	private List<String> chosenPalaces = Arrays.asList("west-germany", "usa", "france", "uk", "canada", "japan");
	private List<Article> articles;
	private List<String> keyWords;

	public List<Article> convertStringToArticles(String articles) {
		List<Article> articleList = new ArrayList<Article>();
		Arrays.asList(articles.replaceAll("<!DOCTYPE.*>", "").replaceAll("<REUTERS.*>", "").replaceAll("\n", "")
				.split("</REUTERS>")).forEach(singleArticle -> {
					articleList.add(convertArticleFromSgmlToObject(singleArticle));
				});
		this.articles = getArticlesWithChosenPlace(getArticlesWithSinglePlace(articleList));
		findKeyWords();
		return this.articles;
	}

	private Article convertArticleFromSgmlToObject(String article) {
		Article result = new Article();
		Matcher matcher = DATE_PATTERN.matcher(article);
		if (matcher.find())
			result.setDate(matcher.group(1).trim());
		matcher = TOPICS_PATTERN.matcher(article);
		if (matcher.find())
			result.setTopics(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = PLACES_PATTERN.matcher(article);
		if (matcher.find())
			result.setPlaces(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = PEOPLE_PATTERN.matcher(article);
		if (matcher.find())
			result.setPeople(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = ORGS_PATTERN.matcher(article);
		if (matcher.find())
			result.setOrgs(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = EXCHANGES_PATTERN.matcher(article);
		if (matcher.find())
			result.setExchanges(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = COMPANIES_PATTERN.matcher(article);
		if (matcher.find())
			result.setCompanies(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
		matcher = TITLE_PATTERN.matcher(article);
		if (matcher.find())
			result.setTitle(matcher.group(1));
		matcher = BODY_PATTERN.matcher(article);
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
		return articles.stream().filter(article -> chosenPalaces.contains(article.getPlaces().get(0).toLowerCase()))
				.collect(Collectors.toList());
	}

	private String removeStopWordsAndStem(String input) {
		List<String> result = new ArrayList<String>();
		Arrays.asList(input.replaceAll("[^a-z. ]", "").split("\\s")).forEach(word -> {
			if (!Arrays.asList(STOPWORDS).contains(word) && word.length() > 1) {
				EnglishStemmer stem = new EnglishStemmer();
				stem.setCurrent(word);
				stem.stem();
				result.add(stem.getCurrent());
			}
		});
		return Strings.join(result, ' ');
	}

	private List<Double> attributeExtraction(Article article) {
		List<Double> attributes = new ArrayList<Double>();
		List<String> importantWordsList = getListOfImportantWords(article);
		Double c1 = calculateC1Attribute(article);
		Double c2 = calculateC2Attribute(importantWordsList, article);
		Double c3 = calculateC3Attribute(importantWordsList, c1);
		Double c4 = calculateC4Attribute(article);
		Double c5 = calculateC5Attribute(importantWordsList, c4);
		Double c6 = calculateC6Attribute(article);
		Double c7 = calculateC7Attribute(c4, c6);
		Double c8 = calculateC8Attribute(article);
		Double c9 = calculateC9Attribute(article);
		Double c10 = calculateC10Attribute(article, c9);
		Double c11 = calculateC11Attribute(article, c9);
		Double c12 = calculateC12Attribute(article);
		Double c13 = calculateC13Attribute(article);

		attributes.add(c1);
		attributes.add(c2);
		attributes.add(c3);
		attributes.add(c4);
		attributes.add(c5);
		attributes.add(c6);
		attributes.add(c7);
		attributes.add(c8);
		attributes.add(c9);
		attributes.add(c10);
		attributes.add(c11);
		attributes.add(c12);
		attributes.add(c13);
		return attributes;
	}

	private Double calculateC1Attribute(Article article) {
		Set<String> wordSet = new HashSet<String>();
		Arrays.asList(removeDots(article.getStemmedBody()).split("\\s+"))
				.forEach(singleWord -> wordSet.add(singleWord));
		return new Double(wordSet.size());
	}

	private Double calculateC2Attribute(List<String> importantWordsList, Article article) {
		Double numberOfImportantWords = new Double(importantWordsList.size());
		Double numberOfAllWords = new Double(Arrays
				.asList(removeDots(article.getBody()).toLowerCase().replaceAll("[^a-z ]", "").split("\\s+")).size());
		return numberOfImportantWords / (numberOfAllWords - numberOfImportantWords);
	}

	private Double calculateC3Attribute(List<String> importantWordsList, Double c1) {
		return new Double(importantWordsList.size()) / c1;
	}

	private Double calculateC4Attribute(Article article) {
		return new Double(article.getBody().split("\\.").length);
	}

	private Double calculateC5Attribute(List<String> importantWordsList, Double c5) {
		return new Double(importantWordsList.size()) / c5;
	}

	private Double calculateC6Attribute(Article article) {
		return new Double(article.getStemmedBody().split("\\s{4,}").length);
	}

	private Double calculateC7Attribute(Double c4, Double c6) {
		return c4 / c6;
	}

	private Double calculateC8Attribute(Article article) {
		return new Double(article.getBody().contains("\"") ? 1.0 : 0.0);
	}

	private Double calculateC9Attribute(Article article) {
		int numberOfOccurrences = 0;
		for (String keyWord : this.keyWords) {
			if (Arrays.asList(article.getStemmedBody().split("\\s+")).contains(keyWord))
				numberOfOccurrences++;
		}
		return new Double(numberOfOccurrences);
	}

	private Double calculateC10Attribute(Article article, Double c9) {
		int numberOfOccurrences = 0;
		for (String word : article.getStemmedBody().split("\\s+")) {
			if (this.keyWords.contains(word))
				numberOfOccurrences++;
		}
		return new Double(numberOfOccurrences) / c9;
	}

	private Double calculateC11Attribute(Article article, Double c9) {
		int sumLengthOfKeyWords = 0;
		for (String word : article.getStemmedBody().split("\\s+")) {
			if (this.keyWords.contains(word))
				sumLengthOfKeyWords += word.length();
		}
		return new Double(sumLengthOfKeyWords) / c9;
	}

	private Double calculateC12Attribute(Article article) {
		int maxSpaceBetweenTwoKeyWords = 0;
		List<Integer> indexesOfKeyWords = new ArrayList<Integer>();
		List<String> articleAsList = Arrays.asList(article.getStemmedBody().split("\\s+"));
		for (int i = 0; i < articleAsList.size(); i++) {
			if (this.keyWords.contains(articleAsList.get(i))) {
				indexesOfKeyWords.add(i);
			}
		}

		Collections.reverse(indexesOfKeyWords);
		for (int i = 0; i < indexesOfKeyWords.size() - 1; i++) {
			if (indexesOfKeyWords.get(i) - indexesOfKeyWords.get(i + 1) > maxSpaceBetweenTwoKeyWords) {
				maxSpaceBetweenTwoKeyWords = indexesOfKeyWords.get(i) - indexesOfKeyWords.get(i + 1);
			}
		}

		return new Double(maxSpaceBetweenTwoKeyWords);
	}

	private Double calculateC13Attribute(Article article) {
		List<String> paragraphs = Arrays.asList(article.getStemmedBody().split("\\s{4,}"));
		int numberOfParragraphsWhereKeyWordOccurres = 0;
		for (String paragraph : paragraphs) {
			for (String keyWord : this.keyWords) {
				if (Arrays.asList(paragraph.split("\\s")).contains(keyWord)) {
					numberOfParragraphsWhereKeyWordOccurres++;
					break;
				}
			}
		}
		return numberOfParragraphsWhereKeyWordOccurres > 1.0 ? 1.0 : 0.0;
	}

	private List<String> getListOfImportantWords(Article article) {
		return Arrays.asList(removeDots(article.getStemmedBody()).split("\\s+"));
	}

	private String removeDots(String text) {
		return text.replaceAll("[.]", "");
	}

	public List<String> findKeyWords() {
		List<String> keyWordsList = new ArrayList<String>();
		Map<String, Integer> wordMap = calculateOccurrancesOfEachWord(articles);

		for (Entry<String, Integer> entry : wordMap.entrySet()) {
			Map<String, Integer> occurrencyMap = calculateOccurrienciesInEachPlace(articles, entry.getKey());
			if (checkIfWordIsGoodEnough(occurrencyMap)) {
				keyWordsList.add(entry.getKey());
			}
			if (keyWordsList.size() == 10)
				break;
		}
		this.keyWords = keyWordsList;
		return keyWordsList;
	}

	private boolean checkIfWordIsGoodEnough(Map<String, Integer> occurrencyMap) {
		List<Integer> occurrencyList = new ArrayList<Integer>(sortByValue(occurrencyMap).values());
		boolean isFirstBiggerEnoughThanSecond = occurrencyList.get(0) * 0.1 > occurrencyList.get(1);
		boolean isSecondBiggerEnoughThanThird = occurrencyList.get(1) * 0.15 > occurrencyList.get(2);
		return isFirstBiggerEnoughThanSecond || isSecondBiggerEnoughThanThird;
	}

	private Map<String, Integer> calculateOccurrienciesInEachPlace(List<Article> articles, String word) {
		Map<String, Integer> occurrencyMap = new HashMap<String, Integer>();
		articles.forEach(article -> {
			String currentPlace = article.getPlaces().get(0);
			if (!occurrencyMap.containsKey(currentPlace)) {
				occurrencyMap.put(currentPlace, 0);
			}
			occurrencyMap.put(currentPlace,
					occurrencyMap.get(currentPlace) + countOccurrencies(article.getStemmedBody(), word));
		});
		return occurrencyMap;
	}

	private Map<String, Integer> calculateOccurrancesOfEachWord(List<Article> articles) {
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		articles.forEach(article -> {
			Arrays.asList(removeDots(article.getStemmedBody()).split("\\s+")).forEach(singleWord -> {
				if (!wordMap.containsKey(singleWord)) {
					wordMap.put(singleWord, 1);
				} else {
					wordMap.put(singleWord, wordMap.get(singleWord) + 1);
				}
			});
		});
		return sortByValue(wordMap);
	}

	private Integer countOccurrencies(String stemmedBody, String word) {
		int occurrencies = 0;

		Matcher matcher = Pattern.compile(word).matcher(stemmedBody);
		while (matcher.find()) {
			occurrencies++;
		}
		return occurrencies;
	}

	private Map<String, Integer> sortByValue(Map<String, Integer> map) {
		List<Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);

		map = new LinkedHashMap<>();
		for (Entry<String, Integer> entry : list) {
			map.put(entry.getKey(), entry.getValue());
		}

		return map;
	}
}
