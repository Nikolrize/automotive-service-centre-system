package Util;

import Model.Customer;
import Model.Service;
import Model.Technician;
import Model.Vehicle;

public class TextFormatter {
    public static String formatCustomer(Customer c) {
        return String.format(
                "User ID: %s\nCustomer ID: %s\nUsername: %s\nFirst Name: %s\nLast Name: %s\nEmail: %s\nContact: %s",
                c.getUserID(), c.getRoleID(), c.getUsername(),
                c.getFirstName(), c.getLastName(), c.getEmail(), c.getContact()
        );
    }

    public static String formatTechnician(Technician t) {
        return String.format(
                "User ID: %s\nTechnician ID: %s\nUsername: %s\nFirst Name: %s\nLast Name: %s\nEmail: %s\nContact: %s",
                t.getUserID(), t.getRoleID(), t.getUsername(),
                t.getFirstName(), t.getLastName(), t.getEmail(), t.getContact()
        );
    }

    public static String formatService(Service s) {
        return String.format(
                "Service ID: %s\nName: %s\nType: %s\nPrice: RM%s\nDuration: %s hour(s)",
                s.getServiceID(), s.getName(), s.getType(), s.getPrice(), s.getDuration()
        );
    }

    public static String formatVehicle(Vehicle v) {
        return String.format(
                "Vehicle ID: %s\nCustomer ID: %s\nUsername: %s\nPlate Number: %s\nBrand: %s\nModel: %s\nYear: %s\nColor: %s",
                v.getVehicleID(), v.getCustomer().getRoleID(), v.getCustomer().getUsername(), v.getPlateNumber(), v.getBrand(), v.getModel(), v.getYear(), v.getColor()
        );
    }
}
