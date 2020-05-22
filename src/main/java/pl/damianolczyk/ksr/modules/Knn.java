package pl.damianolczyk.ksr.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.CommonUtils.Metryka;

@Service
public class Knn {

	public String classify(List<Article> teachingSet, Article articleToClasify, List<Boolean> attributesToProcess,
			int k, Metryka metryka) {
		List<String> placesFromKnn = new ArrayList<>();
		List<Article> knnList = new ArrayList<>();
		Map<Article, Double> articlesAndDistanceMap = new HashMap<>();
		Map<Article, Double> articlesAndDistanceMapSorted = new HashMap<>();
		
		System.err.println(articleToClasify.getPlaces().get(0));
		
		// zastosowanie odpowiedniej metryki
		switch (metryka) {
		case EUKLIDESOWA:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaEuklidesowa(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case ULICZNA:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaUliczna(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case CZEBYSZEWA:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaCzebyszewa(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case CANBERRA:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaCanberra(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case MINKOWSKIEGO:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaMinkowskiego(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case MIARA_MIN_MAX:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, miaraMinMax(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		case MIARA_SREDNIA_ARYTMETYCZNA_MINIMUM:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, miaraSredniaArytmetycznaMinimum(
					article.getAttributes(attributesToProcess), articleToClasify.getAttributes(attributesToProcess))));
			break;
		default:
			break;
		}

		// sortowanie map zawierających wyniki obliczeń
		articlesAndDistanceMapSorted = sortByValueAsc(articlesAndDistanceMap);
		

		// wybranie pierwszych k sąsiadów
		knnList.addAll(new ArrayList<Article>(articlesAndDistanceMapSorted.keySet()).subList(0, k));
		// pobranie PLACE z knn
		placesFromKnn.addAll(knnList.stream().map(article -> article.getPlaces().get(0)).collect(Collectors.toList()));

		Map<String, Integer> frequencyMap = new HashMap<>();
		Map<String, Integer> frequencyMapSorted = new HashMap<>();

		// budowanie mapy PLACE <-> frequency, do znalezienia najczęściej powtarzającego
		// się PLACE w knn
		placesFromKnn.forEach(place -> frequencyMap.put(place, Collections.frequency(placesFromKnn, place)));

		// sortowanie mapy po frequency (malejąco)
		frequencyMapSorted = sortByValueDesc(frequencyMap);
		
		// ustawianie "ClasifiedPlace" dla badanego artykułu (pierwszy element listy
		// jest najczęściej powtarzającym się PLACE)
		articleToClasify.setClasifiedPlace(new ArrayList<String>(frequencyMapSorted.keySet()).get(0));
		return articleToClasify.getClasifiedPlace();
	}

	/*
	 * ######################## ### METRYKI ############ ########################
	 */

	private Double metrykaEuklidesowa(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.pow(dataElement1.get(i) - dataElement2.get(i), 2));
		}

		Double sum = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		double distance = Math.abs(Math.sqrt(sum));
		return distance;
	}

	private Double metrykaUliczna(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i)));
		}

		Double distance = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		return distance;
	}

	private Double metrykaCzebyszewa(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i)));
		}

		double distance = Collections.max(sumElements);
		return distance;
	}

	public Double metrykaCanberra(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i))
					/ (Math.abs(dataElement1.get(i)) + Math.abs(dataElement2.get(i))));
		}

		Double distance = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		return distance;
	}

	// metryka Minkowskiego dla p=3
	public Double metrykaMinkowskiego(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.pow(dataElement1.get(i) - dataElement2.get(i), 3));
		}

		Double sum = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		double distance = Math.abs(Math.sqrt(sum));
		return distance;
	}

	public Double miaraMinMax(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements1 = new ArrayList<>();
		List<Double> sumElements2 = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements1.add(Math.min(dataElement1.get(i), dataElement2.get(i)));
			sumElements2.add(Math.max(dataElement1.get(i), dataElement2.get(i)));
		}

		return sumElements1.stream().collect(Collectors.summingDouble(Double::doubleValue))
				/ sumElements2.stream().collect(Collectors.summingDouble(Double::doubleValue));
	}

	public Double miaraSredniaArytmetycznaMinimum(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements1 = new ArrayList<>();
		List<Double> sumElements2 = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements1.add(Math.min(dataElement1.get(i), dataElement2.get(i)));
			sumElements2.add(dataElement1.get(i) + dataElement2.get(i));
		}

		return sumElements1.stream().collect(Collectors.summingDouble(Double::doubleValue))
				/ (0.5 * sumElements2.stream().collect(Collectors.summingDouble(Double::doubleValue)));
	}


	public static Map<Article, Double> sortByValueAsc(Map<Article, Double> articlesAndDistanceMap) {
		List<Entry<Article, Double>> list = new ArrayList<>(articlesAndDistanceMap.entrySet());
		list.sort(Entry.comparingByValue());

		articlesAndDistanceMap = new LinkedHashMap<>();
		for (Entry<Article, Double> entry : list) {
			articlesAndDistanceMap.put(entry.getKey(), entry.getValue());
		}

		return articlesAndDistanceMap;
	}

	public static Map<String, Integer> sortByValueDesc(Map<String, Integer> frequencyMap) {
		List<Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);

		frequencyMap = new LinkedHashMap<>();
		for (Entry<String, Integer> entry : list) {
			frequencyMap.put(entry.getKey(), entry.getValue());
			System.err.println(entry.getKey()+"\t"+entry.getValue());
		}

		return frequencyMap;
	}

}
