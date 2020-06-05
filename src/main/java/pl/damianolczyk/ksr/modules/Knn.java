package pl.damianolczyk.ksr.modules;

import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * klasyfikuje podany artykuł do odpowiedniej klasy korzystając z algorytku kNN wykorzystując podaną metrykę/miarę
	 * @param teachingSet - zbiór do którego będzie mierzona odległość w algorytmie kNN
	 * @param articleToClasify - klasyfikowany artykuł
	 * @param attributesToProcess lista atrybutów, które mają być brane pod uwagę podczas klasyfikacji
	 * @param k - liczba najbliższych sąsiadów - do algorytmu kNN
	 * @param metryka - metryka/miara, która będzie wykorzystana w algorytmie kNN
	 * @return PLACE do którego został zakwalifikowany artykuł
	 */
	public String classify(List<Article> teachingSet, Article articleToClasify, List<Boolean> attributesToProcess,
			int k, Metryka metryka) {
		List<String> placesFromKnn = new ArrayList<>();
		List<Article> knnList = new ArrayList<>();
		Map<Article, Double> articlesAndDistanceMap = new HashMap<>();
		Map<Article, Double> articlesAndDistanceMapSorted = new HashMap<>();
		
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
		case METRYKA_WLASNA:
			teachingSet.forEach(article -> articlesAndDistanceMap.put(article, metrykaWlasna(
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
	 * ########################### METRYKI ####################################
	 */

	/**
	 *  sqrt(sum(i=1 -> n, pow(pi - qi ,2))), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
	private Double metrykaEuklidesowa(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.pow(dataElement1.get(i) - dataElement2.get(i), 2));
		}

		Double sum = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		double distance = Math.sqrt(sum);
		return distance;
	}

	/**
	 *  sum(i=1 -> n, pi - qi), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
	private Double metrykaUliczna(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i)));
		}

		Double distance = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		return distance;
	}


	/**
	 *  max( (i = 1 => n), |pi - qi|), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
	private Double metrykaCzebyszewa(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> elements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			elements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i)));
		}

		double distance = Collections.max(elements);
		return distance;
	}


	/**
	 *  sum(i=1 -> n, |pi - qi|/ |pi|+|qi|), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
	public Double metrykaCanberra(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.abs(dataElement1.get(i) - dataElement2.get(i))
					/ (Math.abs(dataElement1.get(i)) + Math.abs(dataElement2.get(i))));
		}

		Double distance = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		return distance;
	}


	/**
	 *  |sqrt(pow(sum(i=1 -> n, pow(pi - qi ,3)), 3))|, gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
	public Double metrykaWlasna(List<Double> dataElement1, List<Double> dataElement2) {
		List<Double> sumElements = new ArrayList<>();
		for (int i = 0; i < dataElement1.size(); i++) {
			sumElements.add(Math.pow(dataElement1.get(i) - dataElement2.get(i), 3));
		}

		Double sum = sumElements.stream().collect(Collectors.summingDouble(Double::doubleValue));
		double distance = Math.abs(Math.pow(sum, 3));
		return distance;
	}


	/**
	 *  sum(i=1 -> n, min(pi, qi)) / sum(i=1 -> n, max(pi, qi)), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
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


	/**
	 *  sum(i=1 -> n, min(pi, qi)) / (1/2 * sum(i=1 -> n, (pi + qi))), gdzie n - liczba atrybutów, p - atrybut zbioru do klasyfikacji, q - atrybut zbioru uczącego
	 */
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


	/**
	 * sortowanie wartości mapy rosnąco
	 */
	public static Map<Article, Double> sortByValueAsc(Map<Article, Double> articlesAndDistanceMap) {
		List<Entry<Article, Double>> list = new ArrayList<>(articlesAndDistanceMap.entrySet());
		list.sort(Entry.comparingByValue());

		articlesAndDistanceMap = new LinkedHashMap<>();
		for (Entry<Article, Double> entry : list) {
			articlesAndDistanceMap.put(entry.getKey(), entry.getValue());
		}

		return articlesAndDistanceMap;
	}


	/**
	 * sortowanie wartości mapy malejąco
	 */
	public static Map<String, Integer> sortByValueDesc(Map<String, Integer> frequencyMap) {
		List<Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);

		frequencyMap = new LinkedHashMap<>();
		for (Entry<String, Integer> entry : list) {
			frequencyMap.put(entry.getKey(), entry.getValue());
		}

		return frequencyMap;
	}

}
