// Manager: Service Price Management
package Service;
import Model.Service;
import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class ServiceManagementService {
    private static final String FILE_NAME  = "services.txt";

    public ServiceManagementService() {
        initializeDefaultServices();
    }

    //Makes sure default service records exist before manager opens the screen
    private void initializeDefaultServices() {
        List<String> lines = FileHandler.readFile(FILE_NAME);

        if (lines.isEmpty()) {
            Service normalService = new Service("SV0001", "Normal Service", "Normal", 100.00, 1);
            Service majorService = new Service("SV0002", "Major Service", "Major", 300.00, 3);

            FileHandler.appendToFile(FILE_NAME, formatService(normalService));
            FileHandler.appendToFile(FILE_NAME, formatService(majorService));
        }
    }

    //convert one line in services.txt into service object
    private Service parseService(String line) {
        String[] data = line.split(",");
        if (data.length < 5) return null;

        String serviceID = data[0];
        String name = data[1];
        String type = data[2];
        double price = Double.parseDouble(data[3]);
        int duration = Integer.parseInt(data[4]);

        return new Service(serviceID,name,type,price,duration);
    }

    public List<Service> getAllServices(){
        List<String> lines = FileHandler.readFile(FILE_NAME);
        List<Service> services = new ArrayList<>();

        for (String line : lines) {
            Service service = parseService(line);
            if (service != null) {
                services.add(service);
            }
        }

        return services;
    }

    public Service getServiceByID(String serviceID) throws Exception {
        return getAllServices()
                .stream()
                .filter(s -> s.getServiceID().equals(serviceID))
                .findFirst()
                .orElseThrow(() -> new Exception("Service not found."));
    }

    public void updateServicePrice(String serviceID, String priceText) throws Exception {
        double newPrice = validatePrice(priceText);

        List<String> lines = FileHandler.readFile(FILE_NAME);
        boolean updated = false;

        for(int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");
            if (data.length < 5) continue;

            if (data[0].trim().equals(serviceID.trim())) {
                Service service = getServiceByID(serviceID);
                service.setPrice(newPrice);

                lines.set(i, formatService(service));
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new Exception("Service not found.");
        }

        FileHandler.writeFile(FILE_NAME,lines);
    }

    //creating new service
    public void createService(String name, String type, String priceText, String durationText) throws Exception{
        name = validateName(name);
        type = validateType(type);
        double price = validatePrice(priceText);
        int duration = validateDuration(durationText);

        List<Service> services = getAllServices();

        for (Service service : services) {
            if (service.getName().equalsIgnoreCase(name)) {
                throw new Exception("Service name already exists.");
            }
        }

        Service service = new Service(
                generateNewServiceID(),
                name,
                type,
                price,
                duration
        );

        FileHandler.appendToFile(FILE_NAME, formatService(service));
    }

    private String generateNewServiceID() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        int max = 0;

        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length < 5) continue;

            String serviceID = data[0].trim();

            if (serviceID.startsWith("SV")) {
                int number = Integer.parseInt(serviceID.substring(2));
                if (number > max) {
                    max = number;
                }
            }
        }

        return String.format("SV%04d", max + 1);
    }

    //error validation
    private String validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Service name cannot be empty.");
        }

        return name.trim();
    }

    private String validateType(String type) throws Exception {
        if (type == null || type.trim().isEmpty()) {
            throw new Exception("Service type cannot be empty.");
        }

        type = type.trim();

        if (!type.equalsIgnoreCase("Normal") && !type.equalsIgnoreCase("Major")) {
            throw new Exception("Service type must be either Normal or Major.");
        }

        return Character.toUpperCase(type.charAt(0)) + type.substring(1).toLowerCase();
    }

    private double validatePrice(String priceText) throws Exception {
        if (priceText == null || priceText.trim().isEmpty()) {
            throw new Exception("Price cannot be empty");
        }

        try {
            double price = Double.parseDouble(priceText.trim());

            if (price <= 0) {
                throw new Exception("Price must be greater than 0.");
            }

            return price;
        } catch (NumberFormatException e) {
            throw new Exception("Price must be a valid number");
        }
    }

    private int validateDuration(String durationText) throws Exception {
        if (durationText == null || durationText.trim().isEmpty()) {
            throw new Exception("Duration cannot be empty");
        }

        try {
            int duration = Integer.parseInt(durationText.trim());

            if (duration <= 0) {
                throw new Exception("Duration must be greater than 0");
            }

            return duration;
        } catch (NumberFormatException e) {
            throw new Exception("Duration must be a valid whole number.");
        }
    }

    private String formatService(Service service) {
        return String.join(",",
                service.getServiceID(),
                service.getName(),
                service.getType(),
                String.format("%.2f", service.getPrice()),
                String.valueOf(service.getDuration())
        );
    }
}
