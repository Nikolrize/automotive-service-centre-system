package Model;

import java.time.LocalDate;

public class Payment {
    private String paymentID;
    private Appointment appointment;
    private double amountPaid;
    private LocalDate paymentDate;

    public Payment(String paymentID, Appointment appointment, double amountPaid, LocalDate paymentDate) {
        setPaymentID(paymentID);
        setAppointment(appointment);
        setAmountPaid(amountPaid);
        setPaymentDate(paymentDate);
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

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
