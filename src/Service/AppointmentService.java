package Service;

import Model.*;
import Util.FileHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private static final String FILE_NAME = "appointments.txt";
    private final UserService userService;
    private final ServiceManagementService serviceManagementService;
    private final VehicleService vehicleService;

    public AppointmentService(UserService userService, ServiceManagementService serviceManagementService, VehicleService vehicleService) {
        this.userService = userService;
        this.serviceManagementService = serviceManagementService;
        this.vehicleService = vehicleService;
    }

    private Appointment parseAppointment(String line) throws Exception {
        String[] data = line.split(",");
        if (data.length < 10) return null;

        String appointmentID = data[0];
        String serviceID = data[1];
        String customerID = data[2];
        String counterStaffID = data[3];
        String technicianID = data[4];
        String vehicleID = data[5];
        boolean isCompleted = Boolean.parseBoolean(data[6]);
        LocalDate date = LocalDate.parse(data[7]);
        LocalTime startTime = LocalTime.parse(data[8]);
        LocalTime endTime = LocalTime.parse(data[9]);

        Service service = serviceManagementService.getServiceByID(serviceID);
        Customer customer = (Customer) userService.getUserById(customerID);
        CounterStaff staff = (CounterStaff) userService.getUserById(counterStaffID);
        Technician technician = (Technician) userService.getUserById(technicianID);
        Vehicle vehicle = vehicleService.getVehicleByID(vehicleID);

        return new Appointment(appointmentID, service, customer, staff, technician, vehicle, isCompleted, date, startTime, endTime);
    }

    public List<Appointment> getAllAppointments() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        List<Appointment> list = new ArrayList<>();

        for (String line : lines) {
            try {
                Appointment a = parseAppointment(line);
                if (a != null) list.add(a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to get appointments");
            }
        }

        return list;
    }

    private LocalTime calculateEndTime(LocalTime startTime, Service service) {
        if (startTime == null || service == null) {
            throw new IllegalArgumentException("Start time or service cannot be null");
        }

        int duration = service.getDuration();
        return startTime.plusHours(duration);
    }

    public boolean isTechnicianAvailable(Technician technician, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Appointment> all = getAllAppointments();
        for (Appointment a : all) {
            if (!a.getTechnician().getUserID().equals(technician.getUserID())) continue;
            if (!a.getDate().equals(date)) continue;

            boolean overlap = !(endTime.isBefore(a.getStartTime()) || startTime.isAfter(a.getEndTime()));

            if (overlap) return false;
        }

        return true;
    }

    public List<Technician> getAvailableTechnicians(LocalDate date, LocalTime startTime, Service service) {
        LocalTime endTime = calculateEndTime(startTime, service);
        List<Technician> availableTechnicians = new ArrayList<>();

        for (User user : userService.getActiveUsers()) {
            if (user instanceof Technician technician && isTechnicianAvailable(technician, date, startTime, endTime)) {
                availableTechnicians.add(technician);
            }
        }

        return availableTechnicians;
    }

    private boolean isTechnicianAvailableForUpdate(
            String appointmentID,
            Technician technician,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        for (Appointment a : getAllAppointments()) {
            if (a.getAppointmentID().equals(appointmentID)) continue;
            if (!a.getTechnician().getUserID().equals(technician.getUserID())) continue;
            if (!a.getDate().equals(date)) continue;

            boolean overlap = startTime.isBefore(a.getEndTime()) &&
                    endTime.isAfter(a.getStartTime());

            if (overlap) return false;
        }

        return true;
    }

    private String generateNewID() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        int max = 0;

        for (String line : lines) {
            String id = line.split(",")[0];
            int num = Integer.parseInt(id.substring(1));
            if (num > max) max = num;
        }

        return String.format("A%04d", max + 1);
    }

    public void createAppointment(Service service, Customer customer, CounterStaff staff, Technician technician, Vehicle vehicle, LocalDate date, LocalTime startTime) throws Exception {
        if (service == null || customer == null || staff == null || technician == null || vehicle == null) {
            throw new Exception("All fields must be provided.");
        }

        if (date == null || startTime == null) {
            throw new Exception("Date and time cannot be empty.");
        }

        LocalTime endTime = calculateEndTime(startTime, service);

        if (!isTechnicianAvailable(technician, date, startTime, endTime)) {
            throw new Exception("Selected technician is not available at this time.");
        }

        Appointment appointment = new Appointment(
                generateNewID(),
                service,
                customer,
                staff,
                technician,
                vehicle,
                false,
                date,
                startTime,
                endTime
        );

        FileHandler.appendToFile(FILE_NAME, formatAppointment(appointment));
    }

    public void updateAppointment(Customer newCustomer, Vehicle newVehicle, Service newService, String appointmentID, LocalDate newDate, LocalTime newStartTime, Technician newTechnician) throws Exception {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");

            if (data.length < 10) continue;

            if (data[0].equals(appointmentID)) {
                LocalTime newEndTime = calculateEndTime(newStartTime, newService);

                if (!isTechnicianAvailableForUpdate(appointmentID, newTechnician, newDate, newStartTime, newEndTime)) {
                    throw new Exception("Technician is not available for new schedule.");
                }

                Appointment appointment = getAppointmentByID(appointmentID);

                appointment.setService(newService);
                appointment.setCustomer(newCustomer);
                appointment.setTechnician(newTechnician);
                appointment.setVehicle(newVehicle);
                appointment.setDate(newDate);
                appointment.setStartTime(newStartTime);
                appointment.setEndTime(newEndTime);

                lines.set(i, formatAppointment(appointment));
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new Exception("Appointment not found!");
        }

        FileHandler.writeFile(FILE_NAME, lines);
    }

    public void deleteAppointment(String appointmentID) throws Exception {
        List<String> lines = FileHandler.readFile(FILE_NAME);

        boolean removed = lines.removeIf(line -> {
            String[] data = line.split(",");
            if (data.length < 10) return false;

            return data[0].equals(appointmentID);
        });

        if (!removed) {
            throw new Exception("Appointment not found!");
        }

        FileHandler.writeFile(FILE_NAME, lines);
    }

    public void markCompleted(String appointmentID, Technician technician) throws Exception {
        List<String> lines = FileHandler.readFile(FILE_NAME);

        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");

            if (data[0].equals(appointmentID)) {
                if (!data[4].equals(technician.getUserID())) {
                    throw new Exception("Not your appointment");
                }

                Appointment appointment = getAppointmentByID(appointmentID);
                appointment.setIsCompleted(true);

                lines.set(i, formatAppointment(appointment));
                break;
            }
        }

        FileHandler.writeFile(FILE_NAME, lines);
    }

    private String formatAppointment(Appointment appointment) {
        return String.join(",",
                appointment.getAppointmentID(),
                appointment.getService().getServiceID(),
                appointment.getCustomer().getUserID(),
                appointment.getCounterStaff().getUserID(),
                appointment.getTechnician().getUserID(),
                appointment.getVehicle().getVehicleID(),
                String.valueOf(appointment.getIsCompleted()),
                appointment.getDate().toString(),
                appointment.getStartTime().toString(),
                appointment.getEndTime().toString()
        );
    }

    public List<Appointment> getAppointmentsByCustomer(Customer customer) {
        List<Appointment> list = new ArrayList<>();

        List<Appointment> all = getAllAppointments();
        for (Appointment a : all) {
            if (a.getCustomer().getUserID().equals(customer.getUserID())) {
                list.add(a);
            }
        }

        return list;
    }

    public List<Appointment> getAppointmentsByTechnician(Technician technician) {
        List<Appointment> list = new ArrayList<>();

        List<Appointment> all = getAllAppointments();
        for (Appointment a : all) {
            if (a.getTechnician().getUserID().equals(technician.getUserID())) {
                list.add(a);
            }
        }

        return list;
    }

    public Appointment getAppointmentByID(String appointmentID) {
        for (Appointment a : getAllAppointments()) {
            if (a.getAppointmentID().equals(appointmentID)) {
                return a;
            }
        }
        return null;
    }
}