package approach_1;

import java.util.ArrayList;
import java.util.List;

public class OMFeature {

    private String object;
    private int attributes;
    private boolean hasID;
    private List<OMFeature> parent;
    private int numChildren;
    private int toManyRelation;
    private int toOneRelation;
    private String type;


    public OMFeature() {
    }

    public OMFeature(String object, String type) {
        this.object = object;
        this.type = type;
        this.parent = new ArrayList<>();
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = attributes;
    }

    public boolean hasID() {
        return hasID;
    }

    public void setHasID(boolean hasID) {
        this.hasID = hasID;
    }

    public List<OMFeature> getParent() {
        return parent;
    }

    public void setParent(List<OMFeature> parent) {
        this.parent = parent;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public int getToManyRelation() {
        return toManyRelation;
    }

    public void setToManyRelation(int toManyRelation) {
        this.toManyRelation = toManyRelation;
    }

    public int getToOneRelation() {
        return toOneRelation;
    }

    public void setToOneRelation(int toOneRelation) {
        this.toOneRelation = toOneRelation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "approach_2.OMFeature{" +
                "object='" + object + '\'' +
                ", attributes=" + attributes +
                ", hasID=" + hasID +
                ", parent=" + parent +
                ", numChildren=" + numChildren +
                ", toManyRelation=" + toManyRelation +
                ", toOneRelation=" + toOneRelation +
                ", type='" + type + '\'' +
                '}';
    }
}
