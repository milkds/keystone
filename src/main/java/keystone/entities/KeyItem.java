package keystone.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "items")
public class KeyItem {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemID;

    @Column(name = "MAKE")
    private String make;

    @Column(name = "PART_NO")
    private String partNo;

    @Column(name = "FEATURES")
    private String features;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SHORT_DESCRIPTION")
    private String shortDescription;

    @Column(name = "IMG_LINKS")
    private String imgLinks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<ItemCar> itemCars;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "items_specs",
            joinColumns = { @JoinColumn(name = "ITEM_ID") },
            inverseJoinColumns = { @JoinColumn(name = "SPEC_ID") }
    )
    private List<Specification> specs = new ArrayList<>();

    @Transient
    private List<Car> cars = new ArrayList<>();


    @Override
    public String toString() {
        return "KeyItem{" +
                "make='" + make + '\'' +
                ", partNo='" + partNo + '\'' +
                ", features='" + features + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", imgLinks='" + imgLinks + '\'' +
                '}';
    }

    public int getItemID() {
        return itemID;
    }
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getPartNo() {
        return partNo;
    }
    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }
    public String getFeatures() {
        return features;
    }
    public void setFeatures(String features) {
        this.features = features;
    }
    public List<Specification> getSpecs() {
        return specs;
    }
    public void setSpecs(List<Specification> specs) {
        this.specs = specs;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public String getImgLinks() {
        return imgLinks;
    }
    public void setImgLinks(String imgLinks) {
        this.imgLinks = imgLinks;
    }
    public List<Car> getCars() {
        return cars;
    }
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
    public List<ItemCar> getItemCars() {
        return itemCars;
    }
    public void setItemCars(List<ItemCar> itemCars) {
        this.itemCars = itemCars;
    }
}
