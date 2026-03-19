package Model;

public class Service {
    private String serviceID;
    private String name;
    private String type;
    private double price;
    private int duration;

    public Service(String serviceID, String name, String type, double price, int duration) {
        setServiceID(serviceID);
        setName(name);
        setType(type);
        setPrice(price);
        setDuration(duration);
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
