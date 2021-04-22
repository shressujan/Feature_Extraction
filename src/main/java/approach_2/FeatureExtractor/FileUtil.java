package approach_2.FeatureExtractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FileUtil {

    private static final String FEATURE_CSV_FILES = "src/main/java/approach_2/feature_csv_files";
    private static final String DELIMITER = ",";

    public static void generateCSVFileWithAllFeatures(List<Feature> features, String outputFile) throws IOException {

        File file = new File(FEATURE_CSV_FILES);
        if (!file.exists()) {
            file.mkdir();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FEATURE_CSV_FILES.concat("/").concat(outputFile)))) {
            String line = String.join(DELIMITER, "object", "hasId", "attributes", "parents", "children",
                    "srcOneToRelation", "srcManyToRelation", "dstToOneRelation", "dstToManyRelation", "mappingStrategy",
                    "parentMappingStrategy", "associations", "paretoOptimal");
            bw.write(line + "\n");
            for (Feature feature : features) {
                ObjectModel objectModel = feature.getObjectModel();
                String object = objectModel.getObject();
                String id = String.valueOf(objectModel.hasID() ? 1 : 0);
                String attributes = String.valueOf(objectModel.getAttributes());
                String parents = String.valueOf(objectModel.getParents());
                String children = String.valueOf(objectModel.getChildren());
                String srcOneToRelation = String.valueOf(objectModel.getSrcOneToRelation());
                String srcManyToRelation = String.valueOf(objectModel.getSrcManyToRelation());
                String dstToOneRelation =  String.valueOf(objectModel.getDstToOneRelation());
                String dstToManyRelation = String.valueOf(objectModel.getDstToManyRelation());
                String mapStrategy = feature.getMappingStrategy();
                String parentMapStrategy = feature.getParentMappingStrategy();

                String association;
                if (!feature.getAssociations().isEmpty()) {
                    Collections.sort(feature.getAssociations());
                    association = String.join("+", feature.getAssociations());
                }
                else association = "none";

                String paretoFrontier = String.valueOf(feature.isParetoOptimal() ? 1: 0);

                line = String.join(DELIMITER, object, id, attributes, parents, children, srcOneToRelation,
                        srcManyToRelation, dstToOneRelation, dstToManyRelation, mapStrategy, parentMapStrategy,
                        association, paretoFrontier);

                bw.write(line + "\n");
            }
        }

    }
}
