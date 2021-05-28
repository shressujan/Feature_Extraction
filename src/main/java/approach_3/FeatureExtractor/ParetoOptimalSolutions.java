package approach_3.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParetoOptimalSolutions {

	private final String DELIMETER = ":";
	private final List<Measurement> measurements = new ArrayList<>();

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
		// we want to cull any solution that is dominated by any other solution
		outer: for (Measurement self : measurements) {
			for (Measurement other : measurements) {

				// the other solution has to be as good as the current one
				// in every dimension
				boolean lteInsert = other.getInsertTime() <= self.getInsertTime();
				boolean lteSelect = other.getSelectTime() <= self.getSelectTime();
				boolean lteMemory = other.getDbSpace() <= self.getDbSpace();
				boolean asGood = lteInsert && lteSelect && lteMemory;

				// the other solution has to be better than the current one
				// in at least one dimension
				boolean betterInsert = other.getInsertTime() < self.getInsertTime();
				boolean betterSelect = other.getSelectTime() < self.getSelectTime();
				boolean betterMemory = other.getDbSpace() < self.getDbSpace();
				boolean better = betterInsert || betterSelect || betterMemory;

				// if we find any other solution that satisfies both conditions,
				// the current one is dominated and is therefore NOT in the 
				// Pareto front
				if (asGood && better) {
					self.setParetoFrontier(false);
					continue outer;
				}
			}
			// if we didn't find a dominating solution, then the current one IS
			// in the Pareto front
			self.setParetoFrontier(true);
		}
		System.out.println(measurements);
	}

	public List<Measurement> getMeasurements() {
		return measurements;
	}
}
