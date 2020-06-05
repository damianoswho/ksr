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
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.CommonUtils;

@Service
public class KeywordService {

    /**
     * wybór słów kluczowych
     *
     * @param articles lista artykułów
     * @return lista słów kluczowych
     */
    public List<String> findKeyWords(List<Article> articles) {
        List<String> keyWordsList = new ArrayList<String>();

        // budowanie mapy: słowo -> liczba wystąpień słowa w treści artykułów
        Map<String, Integer> wordMap = calculateOccurrencesOfEachWord(articles);

        // entry = pojedyncza para słowo -> ilość wystąpień
        for (Entry<String, Integer> entry : wordMap.entrySet()) {

            // budowanie mapy PLACE -> ilość wystąpień dla danego słowa
            Map<String, Integer> occurrencyMap = calculateOccurrienciesInEachPlace(articles, entry.getKey());
            // sprawdzenie czy słowo spełnia jedno z wymagań słowa kluczowego
            if (checkIfWordIsGoodEnough(occurrencyMap)) {
                // dodawanie słowa do listy słów kluczowych jeżeli słowo spełnia wymagania
                keyWordsList.add(entry.getKey());
            }
            // zakończenie algorytmu jeżeli zostanei znalezionych 10 słów kluczowych
            if (keyWordsList.size() == 10)
                break;
        }
        return keyWordsList;
    }

    private Map<String, Integer> calculateOccurrienciesInEachPlace(List<Article> articles, String word) {
        Map<String, Integer> occurrencyMap = new HashMap<>();

        articles.forEach(article -> {
            String currentPlace = article.getPlaces().get(0);
            // dodanie klucza jeżeli nie istnieje dla danego PLACE
            if (!occurrencyMap.containsKey(currentPlace)) {
                occurrencyMap.put(currentPlace, 0);
            }

            occurrencyMap.put(currentPlace,
                    // dodanie do mapy liczby wystąpień danego słowa w artykule
                    occurrencyMap.get(currentPlace) + countOccurrencies(article.getStemmedBody(), word));
        });
        return occurrencyMap;
    }

    private boolean checkIfWordIsGoodEnough(Map<String, Integer> occurrencyMap) {
        // sortowanie mapy malejąco po wartościach i utworzenie listy z wartości mapy
        List<Integer> occurrencyList = new ArrayList<>(sortByValue(occurrencyMap).values());

        // Warunek 1
        // 10% pierwszego elementu listy (najwyższa liczba wystąpień)
        // powinna być nadal większa od drugiego elementu listy (druga najwyższa liczba wystąpień)
        boolean isFirstBiggerEnoughThanSecond = occurrencyList.get(0) * 0.1 > occurrencyList.get(1);

        // Warunek 2
        // 15% drugiego elementu listy (druga najwyższa liczba wystąpień)
        // powinna być nadal większa od trzeciego elementu listy (trzecia najwyższa liczba wystąpień)
        boolean isSecondBiggerEnoughThanThird = occurrencyList.get(1) * 0.15 > occurrencyList.get(2);

        // jeżeli słowo spełnia jedno z dwóch warunków to
        return isFirstBiggerEnoughThanSecond || isSecondBiggerEnoughThanThird;
    }

    // obliczanie ilości wystąpień każdego słowa we wszystkich artykułach
    private Map<String, Integer> calculateOccurrencesOfEachWord(List<Article> articles) {
        Map<String, Integer> wordMap = new HashMap<>();
        articles.forEach(article -> {
            Arrays.asList(
                    // usuwanie kropek i dzielenie artykułu na słowa
                    CommonUtils.removeDots(article.getStemmedBody())
                            .split("\\s+"))
                    .forEach(singleWord -> {
                        // dodawanie klucza ze słowem do mapy, jeżeli mapa nie zawiera klucza z danym słowem
                        if (!wordMap.containsKey(singleWord)) {
                            wordMap.put(singleWord, 0);
                        }
                        // inkrementowanie liczby wystąpień danego słowa
                        wordMap.put(singleWord, wordMap.get(singleWord) + 1);

                    });
        });
        // sortowanie mapy po malejąco wartościach
        return sortByValue(wordMap);
    }

    // zliczanie liczby wystąpień słów w danym ciągu znaków
    private Integer countOccurrencies(String stemmedBody, String word) {
        return Arrays.asList(CommonUtils.removeDots(stemmedBody).split("\\s+"))
                .stream()
                .filter(wordInBody -> wordInBody.equals(word))
                .collect(Collectors.toList()).size();
    }

    /**
     * sortowanie mapy malejąco po value
     */
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
