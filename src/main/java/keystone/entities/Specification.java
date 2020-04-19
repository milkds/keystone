package keystone.entities;

import javax.persistence.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "specs")
public class Specification {

    @Id
    @Column(name = "SPEC_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int specID;

    @Column(name = "SPEC_NAME")
    private String specName;

    @Column(name = "SPEC_VALUE")
    private String specValue;

    @ManyToMany(mappedBy = "specs")
    private List<KeyItem> items = new ArrayList<>();

    public Specification(Specification oldSpec) {
        this.specName = oldSpec.getSpecName();
        this.specValue = oldSpec.getSpecValue();
    }

    public Specification() {
    }

    @Override
    public String toString() {
        return "Specification{" +
                "specID=" + specID +
                ", specName='" + specName + '\'' +
                ", specValue='" + specValue + '\'' +
                '}';
    }

    public int getSpecID() {
        return specID;
    }
    public void setSpecID(int specID) {
        this.specID = specID;
    }
    public String getSpecName() {
        return specName;
    }
    public void setSpecName(String specName) {
        this.specName = specName;
    }
    public String getSpecValue() {
        return specValue;
    }
    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }
    public List<KeyItem> getItems() {
        return items;
    }
    public void setItems(List<KeyItem> items) {
        this.items = items;
    }
}
