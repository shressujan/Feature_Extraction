package approach_1;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;

public class FileUtil {

    private static final String FEATURE_CSV_FILES = "src/main/java/approach_1.feature_csv_files";
    private static final String COMBINED_FEATURE_CSV_FILES = "src/main/java/approach_1.feature_csv_files/union";
    private static final String OUTPUT_CSV = "features";
    private static final String DELIMITER = ",";
    private static final String[] EXTENSION = {"csv"};

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

    public static void combine_csv_file() throws IOException {
        File filePath = new File(COMBINED_FEATURE_CSV_FILES);
        if (!filePath.exists()) {
            filePath.mkdir();
        }

        List<File> files = (List<File>) FileUtils.listFiles(new File(FEATURE_CSV_FILES), EXTENSION, false);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COMBINED_FEATURE_CSV_FILES.concat("/")
                .concat(OUTPUT_CSV)
                .concat(".")
                .concat(EXTENSION[0])))) {
            int fileNum = 0;
            for (File file: files) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    if (fileNum != 0) br.readLine();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bw.write(line+ "\n");
                    }
                }
                fileNum++;
            }
        }

    }
}
