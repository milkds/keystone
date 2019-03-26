package keystone.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items_cars_attributes")
public class ItemCarAttribute {

    @Id
    @Column(name = "ITEM_CAR_ATTRIBUTE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemCarID;

    @Column(name = "ATTRIBUTE_NAME")
    private String attName;

    @Column(name = "ATTRIBUTE_VALUE")
    private String attValue;

    @ManyToMany(mappedBy = "attributes")
    private List<ItemCar> itemCars = new ArrayList<>();

    @Override
    public String toString() {
        return "ItemCarAttribute{" +
                "attName='" + attName + '\'' +
                ", attValue='" + attValue + '\'' +
                '}';
    }

    public String getAttName() {
        return attName;
    }
    public void setAttName(String attName) {
        this.attName = attName;
    }
    public String getAttValue() {
        return attValue;
    }
    public void setAttValue(String attValue) {
        this.attValue = attValue;
    }
    public int getItemCarID() {
        return itemCarID;
    }
    public void setItemCarID(int itemCarID) {
        this.itemCarID = itemCarID;
    }
    public List<ItemCar> getItemCars() {
        return itemCars;
    }
    public void setItemCars(List<ItemCar> itemCars) {
        this.itemCars = itemCars;
    }
}
