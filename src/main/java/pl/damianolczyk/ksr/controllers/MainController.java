package pl.damianolczyk.ksr.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private List<Article> articles;
	private List<Article> teachingSet;
	private List<Article> workingSet;
	private List<String> keyWords;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private AttributeService attributeService;

	@Autowired
	private KeywordService keywordService;

	@Autowired
	private Knn knnService;

	@Autowired
	private QualityService qualityService;

	@GetMapping("/test")
	public Double test(@RequestParam("a") String a, @RequestParam("b") String b) {
		return Double.valueOf(a) / Double.valueOf(b);
	}

	@GetMapping("/getArticles")
	public List<Article> getArticles() {
		return this.articles;
	}

	@PostMapping("/applyArticles")
	public int applyArticles(@RequestBody String articles) {
		this.articles = articleService.convertStringToArticles(articles);
		return this.articles.size();
	}

	@PostMapping("/teach")
	public List<String> apply(@RequestParam("noTeachingItems") String noTeachingItems,@RequestParam("includeDisproportion") Boolean includeDisproportion) {
		teachingSet = articleService.getTeachingSet(articles, Integer.valueOf(noTeachingItems), includeDisproportion);
		workingSet = new ArrayList<>(articles);
		workingSet.removeAll(teachingSet);
		keyWords = keywordService.findKeyWords(teachingSet);
		teachingSet.forEach(article -> attributeService.extractAttributes(article, keyWords));
		workingSet.forEach(article -> attributeService.extractAttributes(article, keyWords));

		return keyWords;
	}

	@PostMapping("/classify")
	public Result classify(@RequestParam("metric") Metryka metric, @RequestParam("k") String k,
			@RequestParam("attributesToProcess") List<Boolean> attributesToProcess) {
		workingSet.forEach(
				article -> knnService.classify(teachingSet, article, attributesToProcess, Integer.valueOf(k), metric));

		return qualityService.calculate(teachingSet, workingSet, metric, k, attributesToProcess);
	}

	@PostMapping("/setNewWorkingSet")
	public int setNewWorkingSet(@RequestBody String articles) {
		workingSet = articleService.convertStringToArticles(articles);
		workingSet.forEach(article -> attributeService.extractAttributes(article, keyWords));
		
		return workingSet.size();
	}
}
