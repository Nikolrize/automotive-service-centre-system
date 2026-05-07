package Model;

public class Vehicle {
    private String vehicleID;
    private Customer customer;
    private String plateNumber;
    private String brand;
    private String model;
    private int year;
    private String color;

    public Vehicle(String vehicleID, Customer customer, String plateNumber, String brand, String model, int year, String color) {
        setVehicleID(vehicleID);
        setCustomer(customer);
        setPlateNumber(plateNumber);
        setBrand(brand);
        setModel(model);
        setYear(year);
        setColor(color);
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
