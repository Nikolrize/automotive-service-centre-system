package Model;

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
        this.paymentID = paymentID;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public CounterStaff getCounterStaff() {
        return counterStaff;
    }

    public void setCounterStaff(CounterStaff counterStaff) {
        this.counterStaff = counterStaff;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalTime paymentTime) {
        this.paymentTime = paymentTime;
    }
}
