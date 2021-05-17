package approach_3.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParetoOptimalSolutions {

    private final String DELIMETER = ":";
    List<Measurement> measurements = new ArrayList<>();

    public void loadMeasurements(String measurementFilePath) throws IOException {
        File file = new File(measurementFilePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineTokens = line.split(DELIMETER);
                String solFile = lineTokens[0];
                double insertTime = Double.parseDouble(lineTokens[1]) / 1000;
                double selectTime = Double.parseDouble(lineTokens[2]) / 1000;
                double dbSpace = Double.parseDouble(lineTokens[3]) / 1024;

                measurements.add(new Measurement(solFile, insertTime, selectTime, dbSpace));
            }
        }

        findParetoFrontiers(measurements);
    }

    private void findParetoFrontiers(List<Measurement> measurements) {

        /* Generate Pareto Frontiers for each combination */
        generateParetoFrontiers("insertTimes", "dbSpaces");
        generateParetoFrontiers("insertTimes", "selectTimes");
        generateParetoFrontiers("selectTimes", "dbSpaces");

        System.out.println(measurements);
    }

    private void generateParetoFrontiers(String X, String Y) {
        if (X.equalsIgnoreCase("insertTimes"))
            Collections.sort(measurements, Measurement.compareByInsertTime());
        else if (X.equalsIgnoreCase("selectTimes"))
            Collections.sort(measurements, Measurement.compareBySelectTime());

        /* The first measurement in the sorted list is always a Pareto Frontier */
        measurements.get(0).setParetoFrontier(true);

        if (Y.equalsIgnoreCase("selectTimes")) {
            double minSelectTime = measurements.get(0).getSelectTime();
            for (int i = 1; i < measurements.size(); i++) {
                if (Y.equalsIgnoreCase("selectTimes")) {
                    if (measurements.get(i).getSelectTime() <= minSelectTime) {
                        minSelectTime = measurements.get(i).getSelectTime();
                        measurements.get(i).setParetoFrontier(true);
                    }
                }
            }
        } else if (Y.equalsIgnoreCase("dbSpaces")) {
            double minDbSpace = measurements.get(0).getDbSpace();
            for (int i = 1; i < measurements.size(); i++) {
                if (Y.equalsIgnoreCase("dbSpaces")) {
                    if (measurements.get(i).getDbSpace() <= minDbSpace) {
                        minDbSpace = measurements.get(i).getDbSpace();
                        measurements.get(i).setParetoFrontier(true);
                    }
                }
            }
        }
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }
}
