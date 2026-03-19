package Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private String appointmentID;
    private Service service;
    private Customer customer;
    private CounterStaff counterStaff;
    private Technician technician;
    private boolean isCompleted;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public Appointment(String appointmentID, Service service, Customer customer, CounterStaff counterStaff, Technician technician, boolean isCompleted, LocalDate date, LocalTime startTime, LocalTime endTime) {
        setAppointmentID(appointmentID);
        setService(service);
        setCustomer(customer);
        setCounterStaff(counterStaff);
        setTechnician(technician);
        setISCompleted(isCompleted);
        setDate(date);
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CounterStaff getCounterStaff() {
        return counterStaff;
    }

    public void setCounterStaff(CounterStaff counterStaff) {
        this.counterStaff = counterStaff;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setISCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
