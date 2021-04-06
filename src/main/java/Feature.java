import java.util.List;

public class Feature{

    private OMFeature omFeature;
    private String mappingStrategy;
    private boolean paretoOptimal;

    public Feature() {
    }

    public Feature(OMFeature omFeature, String mappingStrategy, boolean paretoOptimal) {
        this.omFeature = omFeature;
        this.mappingStrategy = mappingStrategy;
        this.paretoOptimal = paretoOptimal;
    }

    public OMFeature getOmFeature() {
        return omFeature;
    }

    public void setOmFeature(OMFeature omFeature) {
        this.omFeature = omFeature;
    }

    public String getMappingStrategy() {
        return mappingStrategy;
    }

    public void setMappingStrategy(String mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    public boolean isParetoOptimal() {
        return paretoOptimal;
    }

    public void setParetoOptimal(boolean paretoOptimal) {
        this.paretoOptimal = paretoOptimal;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "omFeature=" + omFeature +
                ", mappingStrategy='" + mappingStrategy + '\'' +
                ", paretoOptimal=" + paretoOptimal +
                '}';
    }
}
