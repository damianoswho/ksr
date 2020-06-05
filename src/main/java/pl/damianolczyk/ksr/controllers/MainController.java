package pl.damianolczyk.ksr.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.dto.Result;
import pl.damianolczyk.ksr.modules.CommonUtils.Metryka;
import pl.damianolczyk.ksr.modules.Knn;
import pl.damianolczyk.ksr.services.ArticleService;
import pl.damianolczyk.ksr.services.AttributeService;
import pl.damianolczyk.ksr.services.KeywordService;
import pl.damianolczyk.ksr.services.QualityService;

@RestController
public class MainController {
	/**
	 * lista wszystkich artykułów z podanego pliku
	 */
	private List<Article> articles;

	/**
	 * lista artykułów traktowanych jako uczące
	 */
	private List<Article> teachingSet;

	/**
	 * lista artykułów, które zostaną sklasyfikowane
	 */
	private List<Article> workingSet;

	/**
	 * lista słów kluczowych
	 */
	private List<String> keyWords;


	/**
	 * serwis odpowiedzialny za:
	 * - konwersję zawartości wybranych plików do objektów klasy Article
	 * - stemizację treści artykułów
	 * - usunięcie słów ze stoplisty z treści artykułów
	 * - wyselekcjonowanie artykułów uczących
	 */
	@Autowired
	private ArticleService articleService;

	/**
	 * serwis odpowiedzialny za:
	 * - ekstrakcję atrybutów zarówno w artykułach uczących jak i klasyfikowanych
	 * - normalizację atrybutów
	 */
	@Autowired
	private AttributeService attributeService;

	/**
	 * serwis odpowiedzialny za:
	 * - wybór słów kluczowych
	 */
	@Autowired
	private KeywordService keywordService;

	/**
	 * serwis odpowiedzialny za:
	 * - wykonywanie klasyfikacji artykułów
	 */
	@Autowired
	private Knn knnService;

	/**
	 * serwis odpowiedzialny za:
	 * - wykonanie obliczeń związanych z miarami jakości
	 */
	@Autowired
	private QualityService qualityService;

	/**
	 * Pobieranie zawartości plików i konwertowanie ich do postaci artykułów
	 *
	 * @param articles - lista zawierająca zawartości plików wybranych do analizy
	 * @return liczba artykułów spełniających wymagania wstępne
	 */
	@PostMapping("/applyArticles")
	public int applyArticles(@RequestBody List<String> articles) {
		this.articles = articleService.convertStringToArticles(articles);
		return this.articles.size();
	}

	/**
	 * - wybiera artykuły traktowane jako uczące,
	 * - wybiera słowa kluczowe na podstawie zestawu artykułów uczących,
	 * - wykonuje ekstrakcję artykułów zarówno w artykułach uczących jak i analizowanych
	 *
	 * @param noTeachingItems liczba artykułów, które mają być traktowane jako uczące
	 * @param includeDisproportion determinuje czy algorytm ma brać pod uwagę dysproporcje w ilości artykułów z różnych PLACE
	 * @return lista wybranych słów kluczowych
	 */
	@PostMapping("/teach")
	public List<String> teach(@RequestParam("noTeachingItems") String noTeachingItems,@RequestParam("includeDisproportion") Boolean includeDisproportion) {
		teachingSet = articleService.getTeachingSet(articles, Integer.parseInt(noTeachingItems), includeDisproportion);
		workingSet = new ArrayList<>(articles);
		workingSet.removeAll(teachingSet);
		keyWords = keywordService.findKeyWords(teachingSet);
		attributeService.extractAttributes(teachingSet, workingSet, keyWords);

		return keyWords;
	}

	/**
	 * - klasyfikuje artykuły do poszczególnych klas,
	 * - oblicza jakość klasyfikacji
	 *
	 * @param metric - metryka wybrana do klasyfikacji
	 * @param k - liczba najbliższych sąsiadów do na podstawie których ma ją zostać zakwalifikowane artykuły
	 * @param attributesToProcess - lista atrybutów, które mają być brane pod uwagę podczas obliczania odległości pomiędzy artykułami klasyfikowanymi i artykułami uczącymi
	 * @return zestaw danych określających jakość klasyfikacji
	 */
	@PostMapping("/classify")
	public Result classify(@RequestParam("metric") Metryka metric, @RequestParam("k") String k,
			@RequestParam("attributesToProcess") List<Boolean> attributesToProcess) {
		workingSet.forEach(
				article -> knnService.classify(teachingSet, article, attributesToProcess, Integer.parseInt(k), metric));

		return qualityService.calculate(teachingSet, workingSet, metric, k, attributesToProcess);
	}
}
