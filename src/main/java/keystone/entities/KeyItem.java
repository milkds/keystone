package keystone.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Column(name = "MY_PRICE")
    private BigDecimal myPrice;

    @Column(name = "JOBBER_PRICE")
    private BigDecimal jobberPrice;

    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailPrice;

    @Column(name = "DOC_LINKS")
    private String docLinks;

    @Column(name = "HTML_DESC")
    private String htmlDescription;

    @Column(name = "KIT_ELEMENTS")
    private String kitElements;

    @Column(name = "PLAIN_DESC")
    private String plainDesc;

    @Column(name = "WEB_LINK")
    private String webLink;

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
                ", shortDescription='" + shortDescription + '\'' +
                ", imgLinks='" + imgLinks + '\'' +
                ", myPrice=" + myPrice +
                ", jobberPrice=" + jobberPrice +
                ", retailPrice=" + retailPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyItem)) return false;
        KeyItem keyItem = (KeyItem) o;
        return Objects.equals(getWebLink(), keyItem.getWebLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWebLink());
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
    public BigDecimal getMyPrice() {
        return myPrice;
    }
    public void setMyPrice(BigDecimal myPrice) {
        this.myPrice = myPrice;
    }
    public BigDecimal getJobberPrice() {
        return jobberPrice;
    }
    public void setJobberPrice(BigDecimal jobberPrice) {
        this.jobberPrice = jobberPrice;
    }
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }
    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }
    public String getDocLinks() {
        return docLinks;
    }
    public void setDocLinks(String docLinks) {
        this.docLinks = docLinks;
    }
    public String getHtmlDescription() {
        return htmlDescription;
    }
    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }
    public String getKitElements() {
        return kitElements;
    }
    public void setKitElements(String kitElements) {
        this.kitElements = kitElements;
    }
    public String getPlainDesc() {
        return plainDesc;
    }
    public void setPlainDesc(String plainDesc) {
        this.plainDesc = plainDesc;
    }
    public String getWebLink() {
        return webLink;
    }
    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }
}
