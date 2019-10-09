package keystone.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ExcelCar {

    private String year;
    private String yearRange;
    private String make;
    private String model;
    private Set<String> subModels = new HashSet<>();

    public String getYearRange() {
        return yearRange;
    }

    public void setYearRange(String yearRange) {
        this.yearRange = yearRange;
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

    public Set<String> getSubModels() {
        return subModels;
    }

    public void setSubModels(Set<String> subModels) {
        this.subModels = subModels;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcelCar excelCar = (ExcelCar) o;
        return year.equals(excelCar.year) &&
                make.equals(excelCar.make) &&
                model.equals(excelCar.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, make, model);
    }
}
