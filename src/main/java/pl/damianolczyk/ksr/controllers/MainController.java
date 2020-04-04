package pl.damianolczyk.ksr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.services.ArticleService;

@RestController
public class MainController {

	@Autowired
	private ArticleService articleService;

	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@PostMapping("/applyArticles")
	public List<Article> apply(@RequestBody String articles) {
		return articleService.convertStringToArticles(articles);
	}
}
