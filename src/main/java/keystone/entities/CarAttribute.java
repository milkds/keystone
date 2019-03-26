package keystone.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_attributes")
public class CarAttribute {

    @Id
    @Column(name = "CAR_ATT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carAttID;

    @Column(name = "ATT_VALUE")
    private String attValue;

    @ManyToMany(mappedBy = "attributes")
    private List<Car> cars = new ArrayList<>();

    @Override
    public String toString() {
        return "CarAttribute{" +
                "attValue='" + attValue + '\'' +
                '}';
    }

    public List<Car> getCars() {
        return cars;
    }
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
    public String getAttValue() {
        return attValue;
    }
    public void setAttValue(String attValue) {
        this.attValue = attValue;
    }
    public int getCarAttID() {
        return carAttID;
    }
    public void setCarAttID(int carAttID) {
        this.carAttID = carAttID;
    }
}
