import java.io.*;
import java.util.List;

public class FileUtil {

    private static final String FEATURE_CSV_FILES = "src/main/java/feature_csv_files";
    private static final String DELIMITER = ",";

    public static void generateCSVFileWithAllFeatures(List<Feature> features, String outputFile) throws IOException {

        File file = new File(FEATURE_CSV_FILES);
        if (!file.exists()) {
            file.mkdir();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FEATURE_CSV_FILES.concat("/").concat(outputFile)))) {
            String line = String.join(DELIMITER, "attributes", "id", "parents", "children", "toOneRelation",
                    "toManyRelation", "type", "paretoFrontier", "mapStrategy");
            bw.write(line + "\n");
            for (Feature feature : features) {
                OMFeature omFeature = feature.getOmFeature();
                String attributes = String.valueOf(omFeature.getAttributes());
                String id = String.valueOf(omFeature.hasID() ? 1 : 0);
                String parents = String.valueOf(omFeature.getParent().size());
                String children = String.valueOf(omFeature.getNumChildren());
                String toOneRelation = String.valueOf(omFeature.getToOneRelation());
                String toManyRelation = String.valueOf(omFeature.getToManyRelation());
                String type = omFeature.getType();
                String paretoFrontier = String.valueOf(feature.isParetoOptimal() ? 1: 0);
                String mapStrategy = feature.getMappingStrategy();

                line = String.join(DELIMITER, attributes, id, parents, children, toOneRelation, toManyRelation, type,
                        paretoFrontier, mapStrategy);

                bw.write(line + "\n");
            }
        }

    }
}
