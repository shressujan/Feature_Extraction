package approach_2;

public class ObjectModel {

    private String object;
    private int attributes;
    private boolean hasID;
    private ObjectModel parent;
    private int parents;
    private int children;
    private boolean isSrcMultiplicity;
    private boolean isDstMultiplicity;


    public ObjectModel(String object) {
        this.object = object;
        this.children = 0;
        this.hasID = false;
        this.parent = null;
        this.parents = 0;
        this.isSrcMultiplicity = false;
        this.isDstMultiplicity = false;
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

    public ObjectModel getParent() {
        return parent;
    }

    public void setParent(ObjectModel parent) {
        this.parent = parent;
    }

    public int getParents() {
        return parents;
    }

    public void setParents(int parents) {
        this.parents = parents;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public boolean isSrcMultiplicity() {
        return isSrcMultiplicity;
    }

    public void setSrcMultiplicity(boolean srcMultiplicity) {
        isSrcMultiplicity = srcMultiplicity;
    }

    public boolean isDstMultiplicity() {
        return isDstMultiplicity;
    }

    public void setDstMultiplicity(boolean dstMultiplicity) {
        isDstMultiplicity = dstMultiplicity;
    }


    @Override
    public String toString() {
        return "ObjectModel{" +
                "object='" + object + '\'' +
                ", attributes=" + attributes +
                ", hasID=" + hasID +
                ", parent=" + parent +
                ", parents=" + parents +
                ", children=" + children +
                ", isSrcMultiplicity=" + isSrcMultiplicity +
                ", isDstMultiplicity=" + isDstMultiplicity +
                '}';
    }
}
