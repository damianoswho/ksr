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
    public void extractAttributes(List<Article> teachingSet, List<Article> workingSet, List<String> keywords) {
        // ekstrakcja atrybutów w artykułach ze zbioru uczącego
        teachingSet.forEach(article -> {
            extractAttributesInSingleArticle(article, keywords);
        });

        // ekstrakcja atrybutów w artykułach ze zbioru klasyfikowanego
        workingSet.forEach(article -> {
            extractAttributesInSingleArticle(article, keywords);
        });

        // znalezienie najwyższych wartości w wektorach atrybutów do normalizacji wszystkich atrybutów
        List<Double> normalizingFactors = findNormalizingFactors(teachingSet);

        // normalizacja atrybutów wszystkich Artykułów
        normalizeAttributes(teachingSet, normalizingFactors);
        normalizeAttributes(workingSet, normalizingFactors);
    }

    private void extractAttributesInSingleArticle(Article article, List<String> keywords) {
        List<Double> attributes = new ArrayList<>();
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

    //'C1 - Liczba unikatowych słów ważnych. - Ilość unikatowych słów W.',
    private Double calculateC1Attribute(Article article) {
        Set<String> wordSet = new HashSet<>(Arrays.asList(CommonUtils.removeDots(article.getStemmedBody()).split("\\s+")));
        return (double) wordSet.size();
    }

    //'C2 - Stosunek słów ważnych do nieważnych. - Stosunek W do liczby słów oryginalnego artykułu A (oryginalnego czyli przed poddaniem stemizacji oraz usunięciu słów ze stop listy).',
    private Double calculateC2Attribute(List<String> importantWordsList, Article article) {
        double numberOfImportantWords = importantWordsList.size();
        double numberOfAllWords = Arrays
                .asList(CommonUtils.removeDots(article.getBody()).toLowerCase().replaceAll("[^a-z ]", "").split("\\s+"))
                .size();
        return numberOfImportantWords / (numberOfAllWords - numberOfImportantWords);
    }

    //'C3 - Średnia liczba powtórzeń słów ważnych - W podzielona przez C1. ',
    private Double calculateC3Attribute(List<String> importantWordsList, Double c1) {
        return (double) importantWordsList.size() / c1;
    }

    //'C4 - Liczba zdań w artykule - Liczba wystąpień kropki (znaku interpunkcyjnego). ',
    private Double calculateC4Attribute(Article article) {
        return (double) article.getBody().split("\\.").length;
    }

    //'C5 - Średnia liczba słów ważnych w zdaniu - W podzielona przez C4. ',
    private Double calculateC5Attribute(List<String> importantWordsList, Double c5) {
        return (double) importantWordsList.size() / c5;
    }

    //'C6 - Liczba akapitów - Liczba akapitów w artykule.',
    private Double calculateC6Attribute(Article article) {
        return (double) article.getStemmedBody().split("\\s{4,}").length;
    }

    //'C7 - Średnia liczba zdań w akapicie - C4 podzielona przez C6. ',
    private Double calculateC7Attribute(Double c4, Double c6) {
        return c4 / c6;
    }

    //'C8 - Czy występują cytaty - Wartość 0-1 stwierdzająca, czy w artykule wystąpił znak ".',
    private Double calculateC8Attribute(Article article) {
        return article.getBody().contains("\"") ? 1.0 : 0.0;
    }

    //'C9 - Liczba wystąpień unikatowych słów kluczowych w artykule - słowa wybrane w trakcie nauczania programu, zależne od etykiety, których algorytm wyboru zostanie podany w dalszej części sprawozdania. ',
    private Double calculateC9Attribute(Article article, List<String> keywords) {
        int numberOfOccurrences = 0;
        for (String keyWord : keywords) {
            if (Arrays.asList(article.getStemmedBody().split("\\s+")).contains(keyWord))
                numberOfOccurrences++;
        }
        return (double) numberOfOccurrences;
    }

    //'C10 - Średnia liczba powtórzeń słów kluczowych - Liczba wystąpień słów kluczowych podzielona przez C9. ',
    private Double calculateC10Attribute(Article article, Double c9, List<String> keywords) {
        int numberOfOccurrences = 0;
        for (String word : article.getStemmedBody().split("\\s+")) {
            if (keywords.contains(word))
                numberOfOccurrences++;
        }
        return c9 == 0 ? 0.0 : (double) numberOfOccurrences / c9;
    }

    //'C11 - Średnia długość słowa kluczowego - Suma znaków wszystkich słów kluczowych podzielona przez C9. ',
    private Double calculateC11Attribute(Article article, Double c9, List<String> keywords) {
        int sumLengthOfKeyWords = 0;
        for (String word : article.getStemmedBody().split("\\s+")) {
            if (keywords.contains(word))
                sumLengthOfKeyWords += word.length();
        }
        return c9 == 0 ? 0.0 : (double) sumLengthOfKeyWords / c9;
    }

    //'C12 - Maksymalna liczba słów ważnych dzielących dwa słowa kluczowe.',
    private Double calculateC12Attribute(Article article, List<String> keywords) {
        int maxSpaceBetweenTwoKeyWords = 0;
        List<Integer> indexesOfKeyWords = new ArrayList<>();
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

        return (double) maxSpaceBetweenTwoKeyWords;
    }

    //'C13 - Czy słowa kluczowe występują w więcej niż jednym akapicie.'
    private Double calculateC13Attribute(Article article, List<String> keywords) {
        String[] paragraphs = article.getStemmedBody().split("\\s{4,}");
        int numberOfParagraphsWhereKeyWordOccurs = 0;
        for (String paragraph : paragraphs) {
            for (String keyWord : keywords) {
                if (Arrays.asList(paragraph.split("\\s")).contains(keyWord)) {
                    numberOfParagraphsWhereKeyWordOccurs++;
                    break;
                }
            }
        }
        return numberOfParagraphsWhereKeyWordOccurs > 1.0 ? 1.0 : 0.0;
    }

    // lista słów uzyskana po stemizacji
    private List<String> getListOfImportantWords(Article article) {
        return Arrays.asList(CommonUtils.removeDots(article.getStemmedBody()).split("\\s+"));
    }

    // normalizacja atrybutów
    private void normalizeAttributes(List<Article> articles, List<Double> normalizingFactors) {
        articles.forEach(article -> {
            for (int i = 0; i < article.getAttributes().size(); i++) {
                article.getAttributes().set(i, article.getAttributes().get(i) / normalizingFactors.get(i));
            }
        });
    }

    // wyszukiwanie najwyższych wartości w wektorach atrybutów do normalizacji wartości wektorów
    private List<Double> findNormalizingFactors(List<Article> teachingSet) {
        List<Double> result = new ArrayList<>();
        teachingSet.forEach(article -> {
            if (result.size() == 0) {
                result.addAll(article.getAttributes());
            } else {
                for (int i = 0; i < article.getAttributes().size(); i++) {
                    if (result.get(i) < article.getAttributes().get(i)) {
                        result.set(i, article.getAttributes().get(i));
                    }
                }
            }
        });
        return result;
    }
}
