package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.CommonUtils;
@Service
public class KeywordService {
	public  List<String> findKeyWords(List<Article> articles) {
		List<String> keyWordsList = new ArrayList<String>();
		Map<String, Integer> wordMap = calculateOccurrencesOfEachWord(articles);

		for (Entry<String, Integer> entry : wordMap.entrySet()) {
			Map<String, Integer> occurrencyMap = calculateOccurrienciesInEachPlace(articles, entry.getKey());
			if (checkIfWordIsGoodEnough(occurrencyMap)) {
				keyWordsList.add(entry.getKey());
			}
			if (keyWordsList.size() == 10)
				break;
		}
		return keyWordsList;
	}

	private  boolean checkIfWordIsGoodEnough(Map<String, Integer> occurrencyMap) {
		List<Integer> occurrencyList = new ArrayList<Integer>(sortByValue(occurrencyMap).values());
		boolean isFirstBiggerEnoughThanSecond = occurrencyList.get(0) * 0.1 > occurrencyList.get(1);
		boolean isSecondBiggerEnoughThanThird = occurrencyList.get(1) * 0.15 > occurrencyList.get(2);
		return isFirstBiggerEnoughThanSecond || isSecondBiggerEnoughThanThird;
	}

	private  Map<String, Integer> calculateOccurrienciesInEachPlace(List<Article> articles, String word) {
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

	private  Map<String, Integer> calculateOccurrencesOfEachWord(List<Article> articles) {
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		articles.forEach(article -> {
			Arrays.asList(CommonUtils.removeDots(article.getStemmedBody()).split("\\s+")).forEach(singleWord -> {
				if (!wordMap.containsKey(singleWord)) {
					wordMap.put(singleWord, 1);
				} else {
					wordMap.put(singleWord, wordMap.get(singleWord) + 1);
				}
			});
		});
		return sortByValue(wordMap);
	}

	private  Integer countOccurrencies(String stemmedBody, String word) {
		int occurrencies = 0;

		Matcher matcher = Pattern.compile(word).matcher(stemmedBody);
		while (matcher.find()) {
			occurrencies++;
		}
		return occurrencies;
	}

	private  Map<String, Integer> sortByValue(Map<String, Integer> map) {
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
