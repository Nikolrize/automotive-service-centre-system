package Service;

import Model.Customer;
import Model.Vehicle;
import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private static final String FILE_NAME = "vehicles.txt";
    private final UserService userService;

    public VehicleService(UserService userService) {
        this.userService = userService;
    }

    private Vehicle parseVehicle(String line) throws Exception {
        String[] data = line.split(",");

        if (data.length < 7) return null;

        String vehicleID = data[0];
        String customerID = data[1];
        String plateNumber = data[2];
        String brand = data[3];
        String model = data[4];
        int year = Integer.parseInt(data[5]);
        String color = data[6];

        Customer customer = (Customer) userService.getUserById(customerID);

        return new Vehicle(vehicleID, customer, plateNumber, brand, model, year, color);
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();

        for (String line : FileHandler.readFile(FILE_NAME)) {
            try {
                Vehicle vehicle = parseVehicle(line);

                if (vehicle != null) {
                    list.add(vehicle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public Vehicle getVehicleByID(String vehicleID) {
        for (Vehicle vehicle : getAllVehicles()) {
            if (vehicle.getVehicleID().equals(vehicleID)) {
                return vehicle;
            }
        }

        return null;
    }

    public List<Vehicle> getVehiclesByCustomer(Customer customer) {
        List<Vehicle> list = new ArrayList<>();

        for (Vehicle vehicle : getAllVehicles()) {
            if (vehicle.getCustomer().getUserID().equals(customer.getUserID())) {
                list.add(vehicle);
            }
        }

        return list;
    }

    private String generateNewID() {
        int max = 0;

        for (String line : FileHandler.readFile(FILE_NAME)) {
            String[] data = line.split(",");

            if (data.length == 0) continue;

            int num = Integer.parseInt(data[0].substring(1));

            if (num > max) {
                max = num;
            }
        }

        return String.format("V%04d", max + 1);
    }

    public void createVehicle(Customer customer, String plateNumber, String brand, String model, int year, String color) throws Exception {
        if (customer == null) {
            throw new Exception("Customer is required.");
        }

        if (plateNumber == null || plateNumber.isBlank()) {
            throw new Exception("Plate number is required.");
        }

        for (Vehicle vehicle : getAllVehicles()) {
            if (vehicle.getPlateNumber().equalsIgnoreCase(plateNumber)) {
                throw new Exception("Plate number already exists.");
            }
        }

        String vehicleID = generateNewID();

        String data = String.join(",", vehicleID, customer.getUserID(), plateNumber, brand, model, String.valueOf(year), color);

        FileHandler.appendToFile(FILE_NAME, data);
    }

    public void updateVehicle(String vehicleID, String plateNumber, String brand, String model, int year, String color) throws Exception {
        List<String> lines = FileHandler.readFile(FILE_NAME);

        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");

            if (data.length < 7) continue;

            if (data[0].equals(vehicleID)) {
                for (Vehicle vehicle : getAllVehicles()) {
                    if (vehicle.getPlateNumber().equalsIgnoreCase(plateNumber) && !vehicle.getVehicleID().equals(vehicleID)) {
                        throw new Exception("Plate number already exists.");
                    }
                }

                data[2] = plateNumber;
                data[3] = brand;
                data[4] = model;
                data[5] = String.valueOf(year);
                data[6] = color;

                lines.set(i, String.join(",", data));

                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new Exception("Vehicle not found.");
        }

        FileHandler.writeFile(FILE_NAME, lines);
    }

    public void deleteVehicle(String vehicleID) throws Exception {
        List<String> lines = FileHandler.readFile(FILE_NAME);

        boolean removed = lines.removeIf(line -> {
            String[] data = line.split(",");

            return data.length >= 1 && data[0].equals(vehicleID);
        });

        if (!removed) {
            throw new Exception("Vehicle not found.");
        }

        FileHandler.writeFile(FILE_NAME, lines);
    }
}