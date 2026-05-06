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
            FileHandler.appendToFile(FILE_NAME, "SV001,Normal Service,Normal,100.00,1");
            FileHandler.appendToFile(FILE_NAME, "SV002,Major Service,Major,300.00,3");
        }
    }

    //parsing information
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
                data[3] = String.format("%.2f", newPrice);
                lines.set(i,String.join(",", data));
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new Exception("Service not found.");
        }

        FileHandler.writeFile(FILE_NAME,lines);
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
}
