package approach_2.FeatureExtractor;

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
    private final String ASSOCIATION_MAPPING = "AssociationMappings";
    private final String[] EXTENSION = {"xml"};
    private List<ObjectModel> objectModels = new ArrayList<>();
    private List<Association> associations = new ArrayList<>();
    private List<Feature> allOMFeatures = new ArrayList<>();
    private List<Measurement> measurements = new ArrayList<>();
    /* eg: key = customerOrderOM_Sol_2, value = { key = Customer, value = UnionSubClass } */
    private Map<String, Map<String, String>> solToObjectMapStrategy = new HashMap<>();

    /* eg: key = customerOrderOM_Sol_2, value = { key = CustomerOrderAssociation, value = OwnAssociationTable } */
    private Map<String, Map<String, String>> solToAssociationMapStrategy = new HashMap<>();
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
        String solutionDirectory = null;
        String solutionTxtPath = null;
        FeatureExtractor fe = new FeatureExtractor();
        ParetoOptimalSolutions ps = new ParetoOptimalSolutions();
        if (args.length > 2) {
            alloyFileDirectory = args[1];
            solutionDirectory = args[2];
            solutionTxtPath = args[3];
        }

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

                    ObjectModel objectModel;
                    int omFeatureIndex = objectModels.stream().map(ObjectModel::getObject).collect(Collectors.toList()).indexOf(object);
                    /* If object not in the list of approach_2.OMFeature objects */
                    if (omFeatureIndex == -1) {
                        objectModel = new ObjectModel(object);
                    }
                    else objectModel = objectModels.get(omFeatureIndex);

                    int attributes = 0;
                    boolean hasID = false;
                    ObjectModel parentOM = null;
                    int parents = 0;

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
                            parentOM = objectModels.stream().filter(feature -> feature.getObject().equalsIgnoreCase(parent)).findFirst().orElse(new ObjectModel(parent));
                            parentOM.setChildren(parentOM.getChildren() + 1);
                            parents = 1;
                            parents = getParents(parentOM, parents);
                        }
                    }

                    objectModel.setParent(parentOM);
                    objectModel.setParents(parents);
                    objectModel.setAttributes(attributes);
                    objectModel.setHasID(hasID);

                    /* Only add ObjectModel if not already exists */
                    if (omFeatureIndex == -1) objectModels.add(objectModel);
                }
                else if (line.contains("sig") && line.contains("extends") && line.contains("Association")) {

                    String[] tokens = line.split(SPACE);
                    String association = tokens[2];

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

                    associations.add(new Association(association, src, dst, srcMul, dstMul));

                    /* Find the ObjectModel for given source */
                    String finalSrc = src;
                    ObjectModel srcOM = objectModels.stream().filter(feature -> feature.getObject().equalsIgnoreCase(finalSrc)).findFirst().orElse(null);
                    /* If object not found */
                    if (srcOM == null) {
                        srcOM = new ObjectModel(src);
                        objectModels.add(srcOM);
                    }

                    if (srcMul.equalsIgnoreCase("one"))
                        srcOM.setSrcOneToRelation(srcOM.getSrcOneToRelation() + 1);
                    else if (srcMul.equalsIgnoreCase("many"))
                        srcOM.setSrcManyToRelation(srcOM.getSrcManyToRelation() + 1);

                    /* Find the ObjectModel for given destination */
                    String finalDst = dst;
                    ObjectModel dstOM = objectModels.stream().filter(feature -> feature.getObject().equalsIgnoreCase(finalDst)).findFirst().orElse(null);
                    /* If object not found */
                    if (dstOM == null) {
                        dstOM = new ObjectModel(dst);
                        objectModels.add(dstOM);
                    }

                    if (dstMul.equalsIgnoreCase("one"))
                        dstOM.setDstToOneRelation(dstOM.getDstToOneRelation());
                    else if (dstMul.equalsIgnoreCase("many"))
                        dstOM.setDstToManyRelation(dstOM.getDstToManyRelation() + 1);
                }
            }
        }

//        objectModels.forEach(System.out :: println);
    }

    /**
     * Method to parse the solution xml files and extract information into approach_2.FeatureExtractor.Feature objects.
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
        /* For each file extract mapping strategy and create approach_2.FeatureExtractor.Feature objects */
        for (File file : files) {
            Document document = db.parse(new FileInputStream(file.getCanonicalPath()));
            document.getDocumentElement().normalize();
            String fileName = file.getName().substring(0, file.getName().length() - 4);
            NodeList fields = document.getElementsByTagName("field");

            /* Initialize objectToMapStrategy to new hashmap */
            /* eg: key = Customer, value = UnionSubClass */
            Map<String, String> objectToMapStrategy = new HashMap<>();
            Map<String, String> associationToMapStrategy = new HashMap<>();
            for (int i = 0; i < fields.getLength(); i++) {
                Element field = (Element) fields.item(i);
                if (field.getAttribute("label").trim().equalsIgnoreCase(FIELD_VALUE)) {
                    NodeList tuples = field.getElementsByTagName("tuple");
                    for (int j = 0; j < tuples.getLength(); j++) {
                        Element tuple = (Element) tuples.item(j);
                        NodeList atoms = tuple.getElementsByTagName("atom");
                        Element mappingStrategyElement = (Element) atoms.item(0);
                        String[] mappingStrategy = mappingStrategyElement.getAttribute("label").split("/");
                        Element objectElement = (Element) atoms.item(1);
                        String object = objectElement.getAttribute("label").split("/")[1];
                        if (!mappingStrategy[0].contains(ASSOCIATION_MAPPING)) {
                            objectToMapStrategy.put(object, mappingStrategy[1]);
                        } else {
                            associationToMapStrategy.put(object, mappingStrategy[1]);
                        }
                    }
                    solToObjectMapStrategy.put(fileName, objectToMapStrategy);
                    solToAssociationMapStrategy.put(fileName, associationToMapStrategy);
                }
            }
        }
        // Test
       /* for (Map.Entry<String, Map<String, String>> entry : solToAssociationMapStrategy.entrySet()) {
            System.out.println(entry.getKey());
            for (Map.Entry<String, String> objMapStrat : entry.getValue().entrySet()) {
                System.out.println(objMapStrat.getKey());
                System.out.println(objMapStrat.getValue());
            }

        }*/
    }

    /**
     * Recursive method to update the number of children for all parents for a given Object model
     */
    public int getParents(ObjectModel om, int parents) {
        ObjectModel parent = om.getParent();
        /* Base rule */
        if (parent == null)
            return parents;
        parents++;
        /* Update number of children for each parent */
        parent.setChildren(parent.getChildren() + 1);
        /* Recursive call to getParents */
        return getParents(parent, parents);
    }

    /**
     * Load Features with attributes such as mappingStrategy, associationStrategies, and paretoFrontier.
     * Measurements refer to each measurement(select time, insert time, database space, isParetoFrontier, solutionName)
     */
    public void loadFeatures() throws IOException {
        /* For each solution get all the object mappings and association mappings */
        for (Measurement measurement : measurements) {
            String solution = measurement.getSolutionName();
            boolean paretoFrontier = measurement.isParetoFrontier();
            Set<Feature> solToFeatures = new HashSet<>();
            Map<String, String> objToMapStrategy = solToObjectMapStrategy.get(solution);
            Map<String, String> ascToMapStrategy = solToAssociationMapStrategy.get(solution);

            /* Iterating over Object to Mapping Strategy for the selected solution */
            for (Map.Entry<String, String> entry: objToMapStrategy.entrySet()) {
                String object = entry.getKey();
                String mapStrategy = entry.getValue();
                ObjectModel objectModel = objectModels.stream().filter(feature -> feature.getObject().equalsIgnoreCase(object)).findFirst().orElse(null);
                if (objectModel != null) {

                    List<String> associationList = new ArrayList<>();
                    /* Iterating over Association to Mapping Strategy to find all the association related to the
                       specified object */
                    for (Map.Entry<String, String> entry1 : ascToMapStrategy.entrySet()) {
                        String association = entry1.getKey();
                        String ascMapStrategy = entry1.getValue();
                        Association ascObject = associations.stream().filter(asc -> asc.getAssociation().equalsIgnoreCase(association)).findFirst().orElse(null);

                        if (ascObject != null) {
                            if (ascObject.getSrc().equalsIgnoreCase(object) || ascObject.getDst().equalsIgnoreCase(object)) {
                                associationList.add(ascMapStrategy);
                            }
                        }
                    }

                    /* Check if the specified objectModel has a parent, if yes add the parents Mapping Strategy to the
                       newly create Feature */
                    if (objectModel.getParent() != null) {
                        String parentName = objectModel.getParent().getObject();
                        for (Map.Entry<String, String> entry2 : objToMapStrategy.entrySet()) {
                            String parentMappingStrategy = entry2.getValue();
                            if (entry2.getKey().equalsIgnoreCase(parentName)) {
                                solToFeatures.add(new Feature(objectModel, mapStrategy, parentMappingStrategy, associationList, paretoFrontier));
                            }
                        }
                    } else {    /* In case of no parent, set the parentMappingStrategy to none  */
                        solToFeatures.add(new Feature(objectModel, mapStrategy, "none", associationList, paretoFrontier));
                    }
                }
            }
            allOMFeatures.addAll(solToFeatures);
        }

        FileUtil.generateCSVFileWithAllFeatures(allOMFeatures, alsFileName);
    }
}
