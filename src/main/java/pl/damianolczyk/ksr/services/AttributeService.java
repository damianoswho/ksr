package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.CommonUtils;

@Service
public class AttributeService {
	public void extractAttributes(Article article, List<String> keywords) {
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
		Double c9 = calculateC9Attribute(article, keywords);
		Double c10 = calculateC10Attribute(article, c9, keywords);
		Double c11 = calculateC11Attribute(article, c9, keywords);
		Double c12 = calculateC12Attribute(article, keywords);
		Double c13 = calculateC13Attribute(article, keywords);

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
		article.setAttributes(attributes);
	}

	private Double calculateC1Attribute(Article article) {
		Set<String> wordSet = new HashSet<String>();
		Arrays.asList(CommonUtils.removeDots(article.getStemmedBody()).split("\\s+"))
				.forEach(singleWord -> wordSet.add(singleWord));
		return new Double(wordSet.size());
	}

	private Double calculateC2Attribute(List<String> importantWordsList, Article article) {
		Double numberOfImportantWords = new Double(importantWordsList.size());
		Double numberOfAllWords = new Double(Arrays
				.asList(CommonUtils.removeDots(article.getBody()).toLowerCase().replaceAll("[^a-z ]", "").split("\\s+"))
				.size());
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

	private Double calculateC9Attribute(Article article, List<String> keywords) {
		int numberOfOccurrences = 0;
		for (String keyWord : keywords) {
			if (Arrays.asList(article.getStemmedBody().split("\\s+")).contains(keyWord))
				numberOfOccurrences++;
		}
		return new Double(numberOfOccurrences);
	}

	private Double calculateC10Attribute(Article article, Double c9, List<String> keywords) {
		int numberOfOccurrences = 0;
		for (String word : article.getStemmedBody().split("\\s+")) {
			if (keywords.contains(word))
				numberOfOccurrences++;
		}
		return c9 == 0 ? 0.0 : new Double(numberOfOccurrences) / c9;
	}

	private Double calculateC11Attribute(Article article, Double c9, List<String> keywords) {
		int sumLengthOfKeyWords = 0;
		for (String word : article.getStemmedBody().split("\\s+")) {
			if (keywords.contains(word))
				sumLengthOfKeyWords += word.length();
		}
		return c9 == 0 ? 0.0 : new Double(sumLengthOfKeyWords) / c9;
	}

	private Double calculateC12Attribute(Article article, List<String> keywords) {
		int maxSpaceBetweenTwoKeyWords = 0;
		List<Integer> indexesOfKeyWords = new ArrayList<Integer>();
		List<String> articleAsList = Arrays.asList(article.getStemmedBody().split("\\s+"));
		for (int i = 0; i < articleAsList.size(); i++) {
			if (keywords.contains(articleAsList.get(i))) {
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

	private Double calculateC13Attribute(Article article, List<String> keywords) {
		List<String> paragraphs = Arrays.asList(article.getStemmedBody().split("\\s{4,}"));
		int numberOfParragraphsWhereKeyWordOccurres = 0;
		for (String paragraph : paragraphs) {
			for (String keyWord : keywords) {
				if (Arrays.asList(paragraph.split("\\s")).contains(keyWord)) {
					numberOfParragraphsWhereKeyWordOccurres++;
					break;
				}
			}
		}
		return numberOfParragraphsWhereKeyWordOccurres > 1.0 ? 1.0 : 0.0;
	}

	private List<String> getListOfImportantWords(Article article) {
		return Arrays.asList(CommonUtils.removeDots(article.getStemmedBody()).split("\\s+"));
	}
}
