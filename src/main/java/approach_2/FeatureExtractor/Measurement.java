package approach_2.FeatureExtractor;

import java.util.Comparator;

public class Measurement {
    private String solutionName;
    private double insertTime;
    private double selectTime;
    private double dbSpace;
    private boolean paretoFrontier;

    public Measurement() {
    }

    public Measurement(String solutionName, double insertTime, double selectTime, double dbSpace) {
        this.solutionName = solutionName;
        this.insertTime = insertTime;
        this.selectTime = selectTime;
        this.dbSpace = dbSpace;
        this.paretoFrontier = false;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public double getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(double insertTime) {
        this.insertTime = insertTime;
    }

    public double getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(double selectTime) {
        this.selectTime = selectTime;
    }

    public double getDbSpace() {
        return dbSpace;
    }

    public void setDbSpace(double dbSpace) {
        this.dbSpace = dbSpace;
    }

    public boolean isParetoFrontier() {
        return paretoFrontier;
    }

    public void setParetoFrontier(boolean paretoFrontier) {
        this.paretoFrontier = paretoFrontier;
    }

    public static Comparator<Measurement> compareByInsertTime() {
        return (o1, o2) -> {
            if (o1.insertTime == o2.insertTime)
                return 0;
            else if (o1.insertTime > o2.insertTime)
                return 1;
            else
                return -1;
        };
    }

    public static Comparator<Measurement> compareBySelectTime() {
        return (o1, o2) -> {
            if (o1.selectTime == o2.selectTime)
                return 0;
            else if (o1.selectTime > o2.selectTime)
                return 1;
            else
                return -1;
        };
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "solutionName='" + solutionName + '\'' +
                ", insertTime=" + insertTime +
                ", selectTime=" + selectTime +
                ", dbSpace=" + dbSpace +
                ", paretoFrontier=" + paretoFrontier +
                '}';
    }
}
