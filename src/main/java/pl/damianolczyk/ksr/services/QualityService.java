package pl.damianolczyk.ksr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import pl.damianolczyk.ksr.dto.Article;
import pl.damianolczyk.ksr.dto.Result;
import pl.damianolczyk.ksr.dto.ResultsForClass;
import pl.damianolczyk.ksr.modules.Constants;
import pl.damianolczyk.ksr.modules.CommonUtils.Metryka;

@Service
public class QualityService {

	public Result calculate(List<Article> teachingSet, List<Article> workingSet, Metryka metric, String k,
			List<Boolean> attributesToProcess) {
		Result result = new Result();
		result.setTeachingSetSize(teachingSet.size());
		result.setWorkingSetSize(workingSet.size());
		result.setMetric(metric.toString());
		result.setRatio(new Double(teachingSet.size()) / new Double(workingSet.size()));

		List<ResultsForClass> classes = new ArrayList<>();
		Constants.CHOSEN_PLACES.forEach(place -> {
			ResultsForClass resultsForClass = new ResultsForClass();
			resultsForClass.setName(place);
			resultsForClass.setNoInstancesInTeachingSet(teachingSet.stream()
					.filter(article -> article.getPlaces().contains(place)).collect(Collectors.toList()).size());
			resultsForClass.setTruePositive(calculateTruePositive(workingSet, resultsForClass.getName()));
			resultsForClass.setFalsePositive(calculateFalsePositive(workingSet, resultsForClass.getName()));
			resultsForClass.setTrueNegative(calculateTrueNegative(workingSet, resultsForClass.getName()));
			resultsForClass.setFalseNegative(calculateFalseNegative(workingSet, resultsForClass.getName()));

			calculateAccuracy(resultsForClass);
			calculatePrecision(resultsForClass);
			calculateRecall(resultsForClass);
			classes.add(resultsForClass);
		});

		result.setClasses(classes);

		result.setAveragePrecision(classes.stream().mapToDouble(ResultsForClass::getPrecision).sum() / classes.size());
		result.setAverageAccuracy(classes.stream().mapToDouble(ResultsForClass::getAccuracy).sum() / classes.size());
		result.setAverageRecall(classes.stream().mapToDouble(ResultsForClass::getRecall).sum() / classes.size());

		return result;
	}

	// True Positive = niepoprawnie niezakwalikowane do danej klasy
	private Integer calculateFalseNegative(List<Article> workingSet, String place) {
		Integer falseNegative = new Long(workingSet.stream().filter(article -> article.getPlaces().contains(place))
				.filter(article -> !article.getClasifiedPlace().equals(place)).count()).intValue();
		return falseNegative;
	}

	// True Positive = poprawnie niezakwalikowane do danej klasy
	private Integer calculateTrueNegative(List<Article> workingSet, String place) {
		Integer trueNegative = new Long(workingSet.stream().filter(article -> !article.getPlaces().contains(place))
				.filter(article -> !article.getClasifiedPlace().equals(place)).count()).intValue();
		return trueNegative;
	}

	// True Positive = niepoprawnie zakwalikowane do danej klasy
	private Integer calculateFalsePositive(List<Article> workingSet, String place) {
		Integer falsePositive = new Long(workingSet.stream().filter(article -> !article.getPlaces().contains(place))
				.filter(article -> article.getClasifiedPlace().equals(place)).count()).intValue();
		return falsePositive;
	}

	// True Positive = poprawnie zakwalikowane do danej klasy
	private Integer calculateTruePositive(List<Article> workingSet, String place) {
		Integer falsePositive = new Long(workingSet.stream().filter(article -> article.getPlaces().contains(place))
				.filter(article -> article.getClasifiedPlace().equals(place)).count()).intValue();
		return falsePositive;
	}

	// Recall = True Positive / (True Positive + False Negative)
	private void calculateRecall(ResultsForClass resultsForClass) {
		if ((resultsForClass.getTruePositive() + resultsForClass.getFalseNegative()) == 0) {
			resultsForClass.setRecall(0.0);
		} else {
			resultsForClass.setRecall((double) resultsForClass.getTruePositive()
					/ (double) (resultsForClass.getTruePositive() + resultsForClass.getFalseNegative()));
		}
	}

	// Precision = True Positive / (True Positive + False Positive)
	private void calculatePrecision(ResultsForClass resultsForClass) {
		if ((resultsForClass.getTruePositive() + resultsForClass.getFalsePositive()) == 0) {
			resultsForClass.setPrecision(0.0);
		} else {
			resultsForClass.setPrecision((double) (resultsForClass.getTruePositive())
					/ (double) ((resultsForClass.getTruePositive() + resultsForClass.getFalsePositive())));
		}
	}

	// Accuracy = (True Positive + True Negative)/(True Positive + True Negative +
	// False Positive + False Negative)
	private void calculateAccuracy(ResultsForClass resultsForClass) {
		if ((resultsForClass.getTruePositive() + resultsForClass.getTrueNegative() + resultsForClass.getFalsePositive()
				+ resultsForClass.getFalseNegative()) == 0.0) {
			resultsForClass.setAccuracy(0.0);
		} else {
			Double accuracy = ((double) (resultsForClass.getTruePositive() + resultsForClass.getTrueNegative())
					/ (double) (resultsForClass.getTruePositive() + resultsForClass.getTrueNegative()
							+ resultsForClass.getFalsePositive() + resultsForClass.getFalseNegative()));
			resultsForClass.setAccuracy(accuracy);
		}
	}

}
