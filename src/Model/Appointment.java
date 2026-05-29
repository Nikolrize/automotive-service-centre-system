package Model;

import Util.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private String appointmentID;
    private Service service;
    private Customer customer;
    private CounterStaff counterStaff;
    private Technician technician;
    private Vehicle vehicle;
    private boolean isCompleted;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Appointment(String appointmentID, Service service, Customer customer, CounterStaff counterStaff, Technician technician, Vehicle vehicle, boolean isCompleted, LocalDate date, LocalTime startTime, LocalTime endTime) {
        setAppointmentID(appointmentID);
        setService(service);
        setCustomer(customer);
        setCounterStaff(counterStaff);
        setTechnician(technician);
        setVehicle(vehicle);
        setIsCompleted(isCompleted);
        setDate(date);
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = InputValidator.validateID(appointmentID, "A");
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = InputValidator.validateObject(service, "Service");
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = InputValidator.validateObject(customer, "Customer");
    }

    public CounterStaff getCounterStaff() {
        return counterStaff;
    }

    public void setCounterStaff(CounterStaff counterStaff) {
        this.counterStaff = InputValidator.validateObject(counterStaff, "Counter Staff");
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = InputValidator.validateObject(technician, "Technician");
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = InputValidator.validateObject(vehicle, "Vehicle");
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = InputValidator.validateDate(date);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = InputValidator.validateTime(startTime, "Start time");
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = InputValidator.validateTime(endTime, "End time");
    }
}
