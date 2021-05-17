package approach_3.FeatureExtractor;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;

public class CombineCSVFiles {
    private static final String[] EXTENSION = {"csv"};
    private static final String COMBINED_FEATURE_CSV_FILES = "src/main/java/approach_3/feature_csv_files/union";
    private static final String OUTPUT_CSV = "features";
    private static final String FEATURE_CSV_FILES = "src/main/java/approach_3/feature_csv_files";

    public static void main(String[] args) throws IOException {
        File filePath = new File(COMBINED_FEATURE_CSV_FILES);
        if (filePath.exists()) {
            filePath.delete();
        }
        filePath.mkdir();

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
