package approach_3.FeatureExtractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    private static final String FEATURE_CSV_FILES = "src/main/java/approach_3/feature_csv_files";
    private static final String DELIMITER = ",";

    public static void generateCSVFileWithAllFeatures(List<Feature> features, String outputFile) throws IOException {

        File file = new File(FEATURE_CSV_FILES);
        if (!file.exists()) {
            file.mkdir();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FEATURE_CSV_FILES.concat("/").concat(outputFile)))) {
            String line = String.join(DELIMITER, "object", "attributes", "parents", "children",
                    "srcOneToRelation", "srcManyToRelation", "dstToManyRelation", "numCorrespondingTables", "mappingStrategy",
                    "parentMappingStrategy", "associations", "paretoOptimal");
            bw.write(line + "\n");
            for (Feature feature : features) {
                ObjectModel objectModel = feature.getObjectModel();
                String object = objectModel.getObject();
                String attributes = String.valueOf(objectModel.getAttributes());
                String parents = String.valueOf(objectModel.getParents());
                String children = String.valueOf(objectModel.getChildren());
                String srcOneToRelation = String.valueOf(objectModel.getSrcOneToRelation());
                String srcManyToRelation = String.valueOf(objectModel.getSrcManyToRelation());
                String dstToManyRelation = String.valueOf(objectModel.getDstToManyRelation());
                String numCorrespondingTables = String.valueOf(feature.getNumCorrespondingTables());
                String mapStrategy = feature.getMappingStrategy();
                String parentMapStrategy = feature.getParentMappingStrategy();

                String association;
                if (!feature.getAssociations().isEmpty()) {
                    Collections.sort(feature.getAssociations());
                    association = String.join("+", feature.getAssociations());
                }
                else association = "none";

                String paretoFrontier = String.valueOf(feature.isParetoOptimal() ? 1: 0);

                line = String.join(DELIMITER, object, attributes, parents, children, srcOneToRelation,
                        srcManyToRelation, dstToManyRelation, numCorrespondingTables,mapStrategy, parentMapStrategy,
                        association, paretoFrontier);

                bw.write(line + "\n");
            }
        }

    }
}
