package pl.damianolczyk.ksr.dto;

import java.util.List;

public class Result {
	Integer teachingSetSize;
	Integer workingSetSize;
	String metric;
	Double ratio;
	Double averagePrecision;
	Double averageAccuracy;
	Double averageRecall;
	List<ResultsForClass> classes;

	public Integer getTeachingSetSize() {
		return teachingSetSize;
	}

	public void setTeachingSetSize(Integer teachingSetSize) {
		this.teachingSetSize = teachingSetSize;
	}

	public Integer getWorkingSetSize() {
		return workingSetSize;
	}

	public void setWorkingSetSize(Integer workingSetSize) {
		this.workingSetSize = workingSetSize;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public Double getAveragePrecision() {
		return averagePrecision;
	}

	public void setAveragePrecision(Double averagePrecision) {
		this.averagePrecision = averagePrecision;
	}

	public Double getAverageAccuracy() {
		return averageAccuracy;
	}

	public void setAverageAccuracy(Double averageAccuracy) {
		this.averageAccuracy = averageAccuracy;
	}

	public Double getAverageRecall() {
		return averageRecall;
	}

	public void setAverageRecall(Double averageRecall) {
		this.averageRecall = averageRecall;
	}

	public List<ResultsForClass> getClasses() {
		return classes;
	}

	public void setClasses(List<ResultsForClass> classes) {
		this.classes = classes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((averageAccuracy == null) ? 0 : averageAccuracy.hashCode());
		result = prime * result + ((averagePrecision == null) ? 0 : averagePrecision.hashCode());
		result = prime * result + ((averageRecall == null) ? 0 : averageRecall.hashCode());
		result = prime * result + ((classes == null) ? 0 : classes.hashCode());
		result = prime * result + ((metric == null) ? 0 : metric.hashCode());
		result = prime * result + ((ratio == null) ? 0 : ratio.hashCode());
		result = prime * result + ((teachingSetSize == null) ? 0 : teachingSetSize.hashCode());
		result = prime * result + ((workingSetSize == null) ? 0 : workingSetSize.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (averageAccuracy == null) {
			if (other.averageAccuracy != null)
				return false;
		} else if (!averageAccuracy.equals(other.averageAccuracy))
			return false;
		if (averagePrecision == null) {
			if (other.averagePrecision != null)
				return false;
		} else if (!averagePrecision.equals(other.averagePrecision))
			return false;
		if (averageRecall == null) {
			if (other.averageRecall != null)
				return false;
		} else if (!averageRecall.equals(other.averageRecall))
			return false;
		if (classes == null) {
			if (other.classes != null)
				return false;
		} else if (!classes.equals(other.classes))
			return false;
		if (metric == null) {
			if (other.metric != null)
				return false;
		} else if (!metric.equals(other.metric))
			return false;
		if (ratio == null) {
			if (other.ratio != null)
				return false;
		} else if (!ratio.equals(other.ratio))
			return false;
		if (teachingSetSize == null) {
			if (other.teachingSetSize != null)
				return false;
		} else if (!teachingSetSize.equals(other.teachingSetSize))
			return false;
		if (workingSetSize == null) {
			if (other.workingSetSize != null)
				return false;
		} else if (!workingSetSize.equals(other.workingSetSize))
			return false;
		return true;
	}
}