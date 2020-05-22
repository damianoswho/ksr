export interface Result{
    teachingSetSize: number;
    workingSetSize: number;
    metric: String;
    ratio: number;
    averagePrecision: number;
    averageAccuracy: number;
    averageRecall: number;
    classes: Array<{
        name: String;
        noInstancesInTeachingSet: number;
        truePositive: number;
        trueNegative: number;
        falsePositive: number;
        falseNegative: number;
        accuracy: number;
        precision: number;
        recall: number;
    }>;
}