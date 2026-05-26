package Model;

import Util.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;

public class Payment {
    private String paymentID;
    private Appointment appointment;
    private CounterStaff counterStaff;
    private double amount;
    private String method;
    private LocalDate paymentDate;
    private LocalTime paymentTime;

    public Payment(String paymentID, Appointment appointment, CounterStaff counterStaff, double amount, String method, LocalDate paymentDate, LocalTime paymentTime) {
        setPaymentID(paymentID);
        setAppointment(appointment);
        setCounterStaff(counterStaff);
        setAmount(amount);
        setMethod(method);
        setPaymentDate(paymentDate);
        setPaymentTime(paymentTime);
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = InputValidator.validateID(paymentID, "P");
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = InputValidator.validateObject(appointment, "Appointment");
    }

    public CounterStaff getCounterStaff() {
        return counterStaff;
    }

    public void setCounterStaff(CounterStaff counterStaff) {
        this.counterStaff = InputValidator.validateObject(counterStaff, "Counter Staff");
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = InputValidator.validateAmount(amount);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = InputValidator.validateText(method, "Payment method");
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = InputValidator.validateDate(paymentDate);
    }

    public LocalTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalTime paymentTime) {
        this.paymentTime = InputValidator.validateTime(paymentTime, "Payment time");
    }
}
