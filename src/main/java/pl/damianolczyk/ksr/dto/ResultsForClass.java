package pl.damianolczyk.ksr.dto;

public class ResultsForClass {
	String name;
	Integer noInstancesInTeachingSet;
	Integer truePositive;
	Integer trueNegative;
	Integer falsePositive;
	Integer falseNegative;
	Double accuracy;
	Double precision;
	Double recall;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNoInstancesInTeachingSet() {
		return noInstancesInTeachingSet;
	}
	public void setNoInstancesInTeachingSet(Integer noInstancesInTeachingSet) {
		this.noInstancesInTeachingSet = noInstancesInTeachingSet;
	}
	public Integer getTruePositive() {
		return truePositive;
	}
	public void setTruePositive(Integer truePositive) {
		this.truePositive = truePositive;
	}
	public Integer getTrueNegative() {
		return trueNegative;
	}
	public void setTrueNegative(Integer trueNegative) {
		this.trueNegative = trueNegative;
	}
	public Integer getFalsePositive() {
		return falsePositive;
	}
	public void setFalsePositive(Integer falsePositive) {
		this.falsePositive = falsePositive;
	}
	public Integer getFalseNegative() {
		return falseNegative;
	}
	public void setFalseNegative(Integer falseNegative) {
		this.falseNegative = falseNegative;
	}
	public Double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}
	public Double getPrecision() {
		return precision;
	}
	public void setPrecision(Double precision) {
		this.precision = precision;
	}
	public Double getRecall() {
		return recall;
	}
	public void setRecall(Double recall) {
		this.recall = recall;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
		result = prime * result + ((falseNegative == null) ? 0 : falseNegative.hashCode());
		result = prime * result + ((falsePositive == null) ? 0 : falsePositive.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((noInstancesInTeachingSet == null) ? 0 : noInstancesInTeachingSet.hashCode());
		result = prime * result + ((precision == null) ? 0 : precision.hashCode());
		result = prime * result + ((recall == null) ? 0 : recall.hashCode());
		result = prime * result + ((trueNegative == null) ? 0 : trueNegative.hashCode());
		result = prime * result + ((truePositive == null) ? 0 : truePositive.hashCode());
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
		ResultsForClass other = (ResultsForClass) obj;
		if (accuracy == null) {
			if (other.accuracy != null)
				return false;
		} else if (!accuracy.equals(other.accuracy))
			return false;
		if (falseNegative == null) {
			if (other.falseNegative != null)
				return false;
		} else if (!falseNegative.equals(other.falseNegative))
			return false;
		if (falsePositive == null) {
			if (other.falsePositive != null)
				return false;
		} else if (!falsePositive.equals(other.falsePositive))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (noInstancesInTeachingSet == null) {
			if (other.noInstancesInTeachingSet != null)
				return false;
		} else if (!noInstancesInTeachingSet.equals(other.noInstancesInTeachingSet))
			return false;
		if (precision == null) {
			if (other.precision != null)
				return false;
		} else if (!precision.equals(other.precision))
			return false;
		if (recall == null) {
			if (other.recall != null)
				return false;
		} else if (!recall.equals(other.recall))
			return false;
		if (trueNegative == null) {
			if (other.trueNegative != null)
				return false;
		} else if (!trueNegative.equals(other.trueNegative))
			return false;
		if (truePositive == null) {
			if (other.truePositive != null)
				return false;
		} else if (!truePositive.equals(other.truePositive))
			return false;
		return true;
	}
}
