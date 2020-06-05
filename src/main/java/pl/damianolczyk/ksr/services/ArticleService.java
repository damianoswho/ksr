package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.modules.Constants;

@Service
public class ArticleService {

    /**
     * konwertuje treść wybranych plików na objekty klasy Article
     *
     * @param articlesToCOnvert - lista treści artykułów w postacji ciągu znaków
     * @return lista objektów klasy Article
     */
    public List<Article> convertStringToArticles(List<String> articlesToCOnvert) {
        List<Article> articleList = new ArrayList<Article>();
        // scalanie treści wszystkich plików
        String articles = String.join("", articlesToCOnvert);
        Arrays.asList(
                articles
                        // usuwanie niepotrzebnych znaczników z treści plików
                        .replaceAll("<!DOCTYPE.*>", "")
                        .replaceAll("<REUTERS.*>", "")
                        // usuwanie znaków nowej linii
                        .replaceAll("\n", "")
                        // podział treści plików na poszczególne artykuły (znacznik REUTERS oddziela od siebie artykuły)
                        .split("</REUTERS>")).forEach(
                singleArticle -> {
                    articleList.add(
                            //konwersja tekstu pojedynczego artykułu na objekt kalsy Article
                            convertArticleFromSgmlToObject(singleArticle)
                    );
                });

        return
                // wybranie artykułów z interesujących nas PLACE (zawarte w stałej Constants.CHOSEN_PLACES)
                getArticlesWithChosenPlace(
                        // pobranie artykułów należących jednocześnie tylko do jednego PLACE
                        getArticlesWithSinglePlace(
                                // usunięcie z listy artykułów zawierających pusty tekst
                                filterEmptyArticles(articleList)
                        )
                );
    }


    /**
     * konwersja tekstu pojedynczego artykułu na objekt kalsy Article
     *
     * @param article artykuł pod postacją coągu znaków
     * @return artykuł pod postacią objektu klasu Article
     */
    private Article convertArticleFromSgmlToObject(String article) {
        Article result = new Article();
        Matcher matcher = Constants.DATE_PATTERN.matcher(article);
        /*
         * wyszukiwanie poszczególnych pól artukułu
         */
        if (matcher.find())
            result.setDate(matcher.group(1).trim());
        matcher = Constants.TOPICS_PATTERN.matcher(article);
        if (matcher.find())
            result.setTopics(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.PLACES_PATTERN.matcher(article);
        if (matcher.find())
            result.setPlaces(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.PEOPLE_PATTERN.matcher(article);
        if (matcher.find())
            result.setPeople(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.ORGS_PATTERN.matcher(article);
        if (matcher.find())
            result.setOrgs(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.EXCHANGES_PATTERN.matcher(article);
        if (matcher.find())
            result.setExchanges(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.COMPANIES_PATTERN.matcher(article);
        if (matcher.find())
            result.setCompanies(Arrays.asList(matcher.group(1).split("<\\/D><D>")));
        matcher = Constants.TITLE_PATTERN.matcher(article);
        if (matcher.find())
            result.setTitle(matcher.group(1));
        matcher = Constants.BODY_PATTERN.matcher(article);
        if (matcher.find()) {
            result.setBody(matcher.group(1));
            result.setStemmedBody(
                    // stemizacja oraz usunięcie słów ze stop listy z treści artykułu
                    removeStopWordsAndStem(matcher.group(1).toLowerCase())
            );
        }
        return result;
    }

    /*
     * usunięcie artykułów nie zawierających tekstu
     */
    private List<Article> filterEmptyArticles(List<Article> articles) {
        return articles.stream().filter(article -> !article.getBody().isEmpty()).collect(Collectors.toList());
    }

    /*
     * odfiltrowanie artykułów należącego do więcej niż jednego PLACE
     */
    private List<Article> getArticlesWithSinglePlace(List<Article> articles) {
        return articles.stream().filter(article -> article.getPlaces().size() == 1).collect(Collectors.toList());
    }

    /*
     * odfiltrowanie artykułów należących do nie interesujących nas PLACE (spoza listy Constants.CHOSEN_PLACES)
     */
    private List<Article> getArticlesWithChosenPlace(List<Article> articles) {
        return articles.stream()
                .filter(article -> Constants.CHOSEN_PLACES.contains(article.getPlaces().get(0).toLowerCase()))
                .collect(Collectors.toList());
    }

    /*
     * stemizacja oraz usunięcie słów ze stop listy z treści artykułu
     */
    private String removeStopWordsAndStem(String input) {
        List<String> result = new ArrayList<String>();
        Arrays.asList(
                // usunięcie z tekstu wszystkich znaków różnych od znaków alfabetu, kropki oraz znaku odstępu
                input.replaceAll("[^a-z. ]", "")
                        // podział treści artykułu na paragrafy
                        .split("\\s{4,}")
        ).forEach(
                par -> {
                    List<String> paragraph = new ArrayList<>();
                    Arrays.asList(
                            // podział paragrafu na słowa
                            par.split("\\s")
                    ).forEach(
                            word -> {
                                // stemizacja słów, które są zawierają więcej  niż jeden znak (słowa jednoznakowe są tym samym usuwane z rezultatu)
                                if (word.length() > 1) {
                                    // użycie gotowej biblioteki do stemizacji słów w języku angielskim
                                    EnglishStemmer stem = new EnglishStemmer();
                                    stem.setCurrent(word);
                                    stem.stem();
                                    paragraph.add(stem.getCurrent());
                                }
                            });
                    // usunięcie słów ze stoplisty
                    paragraph.removeAll(Arrays.asList(Constants.STOPWORDS));
                    // "złożenie" paragrafu i dodanie go do rezultatu
                    result.add(String.join(" ", paragraph));
                });
        // "złożenie" rezultatu zachowując podział na paragrafy
        return String.join("    ", result);
    }

    /**
     * Wybranie artykułów uczących
     *
     * @param articles lista wszystkich artykułów
     * @param sizeOfTeachingSet liczba artykułów, które mają być uznane za artykuły uczące
     * @param includeDisproportion flaga determinuje czy algorytm ma brać pod uwagę dysproporcje w ilości artykułów z różnych PLACE
     * @return lista artykułów wybranych na artykuły uczące
     */
    public List<Article> getTeachingSet(List<Article> articles, int sizeOfTeachingSet, Boolean includeDisproportion) {
        Set<Article> setOfArticles = new HashSet<>();
        List<Article> result = new ArrayList<>();
        // maksymalna liczba artykułów z poszczególnych PLACE, które mogą znaleźć się w zbiorze uczącym (używana gdy ustawiona jest flaga includeDisproportion)
        int maxNumberOfArticlesFromSinglePlace = sizeOfTeachingSet / Constants.CHOSEN_PLACES.size();

        // iteracja po wszystkich interesujących nas PLACE
        Constants.CHOSEN_PLACES.forEach(choosenPlace -> {
            // wybierz wszystkie artykuły dla wybranego PLACE
            List<Article> chosenArticles = articles.stream()
                    .filter(article -> article.getPlaces().get(0).contentEquals(choosenPlace))
                    .collect(Collectors.toList());

            // ograniczenie liczby artykułów do dodania do zbioru uczącego w przypadku gdy artykułów jest z danego PLACE jest wjęcej niż maxNumberOfArticlesFromSinglePlace
            if (chosenArticles.size() > maxNumberOfArticlesFromSinglePlace) {
                chosenArticles = chosenArticles.subList(0, maxNumberOfArticlesFromSinglePlace);
            }
            // dodanie odpowiedniej liczby artykułów do zbioru (SET) artykułów
            setOfArticles.addAll(chosenArticles);
        });

        // uzupełnienie listy artykułów w przypadku gdy flaga includeDisproportion jest nieustawiona
        if (!includeDisproportion) {
            // uzupełnianie listy artykułów do podanej liczby artykułów uczących
            for (Article article : articles) {
                if (setOfArticles.size() == sizeOfTeachingSet)
                    break;
                setOfArticles.add(article);
            }
        }
        // zamiana SET na LIST
        result.addAll(setOfArticles);
        return result;
    }
}
