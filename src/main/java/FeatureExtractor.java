import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureExtractor {
    private final String FIELD_VALUE = "assignees";
    private final String[] EXTENSION = {"xml"};
    private List<OMFeature> omFeatures = new ArrayList<>();
    private List<Feature> allOMFeatures = new ArrayList<>();
    private List<Measurement> measurements = new ArrayList<>();
    /* eg: key = customerOrderOM_Sol_2, value = { key = Customer, value = UnionSubClass } */
    private Map<String, Map<String, String>> solToObjectMapStrategy = new HashMap<>();
    private final String EQUALS = "=";
    private final String SPACE = " ";
    private static final String SLASH = "/";
    private static final String DOT = "\\.";
    private final String CLASS = "Class";
    private final String ASSOCIATION = "Association";
    private static final String CSV = ".csv";
    private String alsFileName;

    public static void main(String[] args) {
        String alloyFileDirectory = null;
        String solutionDirectory;
        String solutionTxtPath;
        FeatureExtractor fe = new FeatureExtractor();
        ParetoOptimalSolutions ps = new ParetoOptimalSolutions();
        if (args.length > 2)
            alloyFileDirectory = args[1];   //"src/main/java/models/customerOrderObjectModel.als"
            solutionDirectory = args[2];    //"src/main/java/solutions/customer_order/ImplSolution";
            solutionTxtPath = args[3];      //"src/main/java/solutions/customer_order/customerOrderObjectModel.txt";
            String[] tmp = alloyFileDirectory.split(SLASH);
            fe.alsFileName = tmp[tmp.length - 1].split(DOT)[0].concat(CSV);

        try {
            fe.readAlloyFileIntoObjectModelFeature(alloyFileDirectory);
            fe.readFromFilesInDirectory(solutionDirectory);
            ps.loadMeasurements(solutionTxtPath);
            fe.measurements = ps.getMeasurements();
            fe.loadFeatures();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }


    }

    /**
     * Method to parse the input .als model and extract the information into OMFeatures.
     * @param filePath
     * @throws IOException
     */
    public void readAlloyFileIntoObjectModelFeature(String filePath) throws IOException {
        File file = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                /* Logic for reading file */
                if (line.contains("sig") && line.contains("extends") && line.contains("Class")) {

                    String[] tokens = line.split(SPACE);
                    String object = tokens[2];

                    OMFeature omFeature;
                    int omFeatureIndex = omFeatures.stream().map(OMFeature::getObject).collect(Collectors.toList()).indexOf(object);
                    /* If object not in the list of OMFeature objects */
                    if (omFeatureIndex == -1) {
                        omFeature = new OMFeature(object, CLASS);
                    }
                    else omFeature = omFeatures.get(omFeatureIndex);

                    int attributes = 0;
                    boolean hasID = false;
                    List<OMFeature> parentOMs = new ArrayList<>();
                    int children = 0;

                    while (!(line = br.readLine()).contains("}")) {
                        if (line.contains("attrSet")) {
                            String[] attrTokens = line.split(EQUALS);
                            String[] attrSet = attrTokens[1].split("\\+");
                            attributes = attrSet.length;
                        }
                        else if (line.contains("id") && line.contains(EQUALS)) {
                            hasID = true;
                        }
                        else if (line.contains("parent") && line.contains("in")) {
                            String parent = line.split(SPACE)[2].trim();
                            OMFeature parentOM = omFeatures.stream().filter(feature -> feature.getObject().equalsIgnoreCase(parent)).findFirst().orElse(new OMFeature(parent, CLASS));
                            parentOM.setNumChildren(parentOM.getNumChildren() + 1);
                            parentOMs.add(parentOM);

                            /* Update the children for all parents */
                            getParents(parentOM, parentOMs);

                        }
                    }

                    omFeature.setParent(parentOMs);
                    omFeature.setNumChildren(children);
                    omFeature.setAttributes(attributes);
                    omFeature.setHasID(hasID);

                    /* Only add OMFeature object if not already exists */
                    if (omFeatureIndex == -1) omFeatures.add(omFeature);
                }
                else if (line.contains("sig") && line.contains("extends") && line.contains("Association")) {

                    String[] tokens = line.split(SPACE);
                    String object = tokens[2];

                    OMFeature omFeature = new OMFeature(object, ASSOCIATION);
                    omFeatures.add(omFeature);

                    String src = "", dst = "", srcMul ="", dstMul = "";
                    while (!(line = br.readLine()).contains("}")) {
                        if (line.contains("src") && line.contains(EQUALS) && !line.contains("src_multiplicity")) {
                            src = line.split(EQUALS)[1].trim();
                        }
                        else if (line.contains("dst") && line.contains(EQUALS) && !line.contains("dst_multiplicity")) {
                            dst = line.split(EQUALS)[1].trim();
                        }
                        else if (line.contains("src_multiplicity") && line.contains("=")) {
                            srcMul = line.split(EQUALS)[1].trim();
                        }
                        else if (line.contains("dst_multiplicity") && line.contains("=")) {
                            dstMul = line.split(EQUALS)[1].trim();
                        }

                    }
                    /* Find the OMFeature for given source */
                    String finalSrc = src;
                    OMFeature srcOM = omFeatures.stream().filter(feature -> feature.getObject().equalsIgnoreCase(finalSrc)).findFirst().orElse(null);
                    /* If object not found */
                    if (srcOM == null) {
                        srcOM = new OMFeature(src, CLASS);
                        omFeatures.add(srcOM);
                    }

                    if (srcMul.equalsIgnoreCase("one"))
                        srcOM.setToOneRelation(srcOM.getToOneRelation() + 1);
                    else if (srcMul.equalsIgnoreCase("many"))
                        srcOM.setToManyRelation(srcOM.getToManyRelation() + 1);

                    /* Find the OMFeature for given destination */
                    String finalDst = dst;
                    OMFeature dstOM = omFeatures.stream().filter(feature -> feature.getObject().equalsIgnoreCase(finalDst)).findFirst().orElse(null);
                    /* If object not found */
                    if (dstOM == null) {
                        dstOM = new OMFeature(dst, CLASS);
                        omFeatures.add(dstOM);
                    }

                    if (dstMul.equalsIgnoreCase("one"))
                        dstOM.setToOneRelation(dstOM.getToOneRelation() + 1);
                    else if (dstMul.equalsIgnoreCase("many"))
                        dstOM.setToManyRelation(dstOM.getToManyRelation() + 1);
                }
            }
        }

//        omFeatures.forEach(System.out :: println);
    }

    /**
     * Method to parse the solution xml files and extract information into Feature objects.
     * @param directory
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void readFromFilesInDirectory(String directory) throws ParserConfigurationException, IOException, SAXException {
        File filePath = new File(directory);
        List<File> files = (List<File>) FileUtils.listFiles(filePath, EXTENSION, false);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        /* For each file extract mapping strategy and create Feature objects */
        for (File file : files) {
            Document document = db.parse(new FileInputStream(file.getCanonicalPath()));
            document.getDocumentElement().normalize();
            String fileName = file.getName().substring(0, file.getName().length() - 4);
            NodeList fields = document.getElementsByTagName("field");

            /* Initialize objectToMapStrategy to new hashmap */
            /* eg: key = Customer, value = UnionSubClass */
            Map<String, String> objectToMapStrategy = new HashMap<>();
            for (int i = 0; i < fields.getLength(); i++) {
                Element field = (Element) fields.item(i);
                if (field.getAttribute("label").trim().equalsIgnoreCase(FIELD_VALUE)) {
                    NodeList tuples = field.getElementsByTagName("tuple");
                    for (int j = 0; j < tuples.getLength(); j++) {
                        Element tuple = (Element) tuples.item(j);
                        NodeList atoms = tuple.getElementsByTagName("atom");
                        Element mappingStrategyElement = (Element) atoms.item(0);
                        String mappingStrategy = mappingStrategyElement.getAttribute("label").split("/")[1];
                        Element objectElement = (Element) atoms.item(1);
                        String object = objectElement.getAttribute("label").split("/")[1];
                        objectToMapStrategy.put(object, mappingStrategy);
                    }
                    solToObjectMapStrategy.put(fileName, objectToMapStrategy);
                }
            }
        }
        // Test
        /*for (Map.Entry<String, Map<String, String>> entry : solsToObjectMapStrategy.entrySet()) {
            System.out.println(entry.getKey());
            for (Map.Entry<String, String> objMapStrat : entry.getValue().entrySet()) {
                System.out.println(objMapStrat.getKey());
                System.out.println(objMapStrat.getValue());
            }

        }*/
    }

    /**
     * Recursive method to add all parents for a given Object model
     * @param parentOM
     * @param parentOMs
     */
    public void getParents(OMFeature parentOM, List<OMFeature> parentOMs) {
        List<OMFeature> parents = parentOM.getParent();
        if (parents.size() == 0)
                return;
        parentOMs.addAll(parents);
        /* Update number of children for each parent */
        parents.forEach(parent -> parent.setNumChildren(parent.getNumChildren() + 1));
        /* Recursive call to getParents */
        parents.forEach(parent -> getParents(parent, parentOMs));
    }

    /**
     * Load Features with attributes such as mappingStrategy and paretoFrontier.
     */
    public void loadFeatures() throws IOException {
        for (Measurement measurement : measurements) {
            String solution = measurement.getSolutionName();
            boolean paretoFrontier = measurement.isParetoFrontier();
            Map<String, String> objToMapStrategy = solToObjectMapStrategy.get(solution);
            for (Map.Entry<String, String> entry: objToMapStrategy.entrySet()) {
                String object = entry.getKey();
                String mapStrategy = entry.getValue();
                OMFeature omFeature = omFeatures.stream().filter(feature -> feature.getObject().equalsIgnoreCase(object)).findFirst().orElse(null);
                if (omFeature != null) {
                    allOMFeatures.add( new Feature(omFeature, mapStrategy, paretoFrontier));
                }

            }
        }

        allOMFeatures.forEach(System.out :: println);

        FileUtil.generateCSVFileWithAllFeatures(allOMFeatures, alsFileName);
    }
}
