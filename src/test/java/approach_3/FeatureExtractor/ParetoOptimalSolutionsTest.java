package approach_3.FeatureExtractor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class ParetoOptimalSolutionsTest {

	@Test
	void testLoadMeasurements() throws IOException {
		ParetoOptimalSolutions uut = new ParetoOptimalSolutions();
		uut.loadMeasurements("src/main/java/solutions/customer_order/customerOrderObjectModel.txt");
		Set<Measurement> pareto = uut.getMeasurements().stream().filter(Measurement::isParetoFrontier)
				.collect(Collectors.toSet());
		for (Measurement m : pareto) {
			System.out.printf("%f, %f, %f%n", m.getInsertTime(), m.getSelectTime(), m.getDbSpace());
		}
		assertEquals(2, pareto.size());
	}

}
