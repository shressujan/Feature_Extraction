package approach_2;

public class Association {
    private String association;
    private String src;
    private String dst;
    private String src_multiplicity;
    private String dst_multiplicity;

    public Association(String association, String src, String dst, String src_multiplicity, String dst_multiplicity) {
        this.association = association;
        this.src = src;
        this.dst = dst;
        this.src_multiplicity = src_multiplicity;
        this.dst_multiplicity = dst_multiplicity;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getSrc_multiplicity() {
        return src_multiplicity;
    }

    public void setSrc_multiplicity(String src_multiplicity) {
        this.src_multiplicity = src_multiplicity;
    }

    public String getDst_multiplicity() {
        return dst_multiplicity;
    }

    public void setDst_multiplicity(String dst_multiplicity) {
        this.dst_multiplicity = dst_multiplicity;
    }

    @Override
    public String toString() {
        return "Association{" +
                "association='" + association + '\'' +
                ", src='" + src + '\'' +
                ", dst='" + dst + '\'' +
                ", src_multiplicity='" + src_multiplicity + '\'' +
                ", dst_multiplicity='" + dst_multiplicity + '\'' +
                '}';
    }
}
