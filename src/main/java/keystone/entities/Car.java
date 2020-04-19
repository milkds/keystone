package keystone.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @Column(name = "CAR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carID;

    @Column(name = "YEAR")
    private int year;

    @Column(name = "MAKE")
    private String make;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "START_FINISH")
    private String startFinish;

    @Column(name = "ATTRIBUTE_STRING")
    private String attString;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "car")
    private List<ItemCar> itemCars;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "car_car_attributes",
            joinColumns = { @JoinColumn(name = "CAR_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CAR_ATT_ID") }
    )
    private Set<CarAttribute> attributes = new HashSet<>();

    @Transient
    private List<ItemCarAttribute> fitAttributes = new ArrayList<>();

    public Car(Car templateCar) {
        this.startFinish = templateCar.getStartFinish();
        this.make = templateCar.getMake();
        this.model = templateCar.getModel();
    }
    public Car(){}

    public Car(Car oldCar, boolean b) {
        this.year = oldCar.getYear();
        this.make = oldCar.getMake();
        this.model = oldCar.getModel();
        this.startFinish = oldCar.getStartFinish();
        this.attString = oldCar.getAttString();

        oldCar.getAttributes().forEach(oldAtt->{
            CarAttribute newAtt = new CarAttribute(oldAtt);
            attributes.add(newAtt);
        });
    }

    @Override
    public String toString() {
        return "Car{" +
                "year=" + year +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", startFinish='" + startFinish + '\'' +
                ", attString='" + attString + '\'' +
                '}';
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getStartFinish() {
        return startFinish;
    }
    public void setStartFinish(String startFinish) {
        this.startFinish = startFinish;
    }
    public String getAttString() {
        return attString;
    }
    public void setAttString(String attString) {
        this.attString = attString;
    }
    public Set<CarAttribute> getAttributes() {
        return attributes;
    }
    public void setAttributes(Set<CarAttribute> attributes) {
        this.attributes = attributes;
    }
    public List<ItemCarAttribute> getFitAttributes() {
        return fitAttributes;
    }
    public void setFitAttributes(List<ItemCarAttribute> fitAttributes) {
        this.fitAttributes = fitAttributes;
    }
    public int getCarID() {
        return carID;
    }
    public void setCarID(int carID) {
        this.carID = carID;
    }
    public List<ItemCar> getItemCars() {
        return itemCars;
    }
    public void setItemCars(List<ItemCar> itemCars) {
        this.itemCars = itemCars;
    }
}
