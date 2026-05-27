package Service;

import Model.*;
import Util.FileHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private static final String FILE_NAME = "payments.txt";

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final LoyaltyService loyaltyService = new LoyaltyService();

    public PaymentService(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    private Payment parsePayment(String line) throws Exception {
        String[] data = line.split(",");
        if (data.length < 7) return null;

        String paymentID = data[0];
        String appointmentID = data[1];
        String staffID = data[2];
        double amount = Double.parseDouble(data[3]);
        String method = data[4];
        LocalDate date = LocalDate.parse(data[5]);
        LocalTime time = LocalTime.parse(data[6]);

        Appointment appointment = appointmentService.getAppointmentByID(appointmentID);
        CounterStaff staff = (CounterStaff) userService.getUserById(staffID);

        return new Payment(paymentID, appointment, staff, amount, method, date, time);
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();

        for (String line : FileHandler.readFile(FILE_NAME)) {
            try {
                Payment payment = parsePayment(line);
                if (payment != null) payments.add(payment);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to get payments");
            }
        }

        return payments;
    }

    public Payment getPaymentByID(String paymentID) {
        for (Payment payment : getAllPayments()) {
            if (payment.getPaymentID().equals(paymentID)) {
                return payment;
            }
        }

        return null;
    }

    public Payment getPaymentByAppointmentID(String appointmentID) {
        for (Payment payment : getAllPayments()) {
            if (payment.getAppointment().getAppointmentID().equals(appointmentID)) {
                return payment;
            }
        }

        return null;
    }

    public boolean isAppointmentPaid(String appointmentID) {
        return getPaymentByAppointmentID(appointmentID) != null;
    }

    private String generateNewID() {
        int max = 0;

        for (String line : FileHandler.readFile(FILE_NAME)) {
            String[] data = line.split(",");
            if (data.length < 1) continue;

            int num = Integer.parseInt(data[0].substring(1));
            if (num > max) max = num;
        }

        return String.format("P%04d", max + 1);
    }

    public void createPayment(Appointment appointment, User currentUser, String method) throws Exception {
        if (appointment == null || currentUser == null || method == null || method.isBlank()) {
            throw new Exception("All payment fields must be provided.");
        }

        if (isAppointmentPaid(appointment.getAppointmentID())) {
            throw new Exception("This appointment has already been paid.");
        }

        if (!appointment.getIsCompleted()) {
            throw new Exception("Payment can only be collected for completed appointments.");
        }

        double originalAmount = appointment.getService().getPrice();
        double amount = loyaltyService.applyReward(appointment.getCustomer(), originalAmount);

        Payment payment = new Payment(
                generateNewID(),
                appointment,
                (CounterStaff) currentUser,
                amount,
                method,
                LocalDate.now(),
                LocalTime.now()
        );

        FileHandler.appendToFile(FILE_NAME, formatPayment(payment));
        loyaltyService.addPoints(appointment);
    }

    public String generateReceipt(Payment payment) {
        Appointment a = payment.getAppointment();
        LoyaltyReward reward = loyaltyService.getLatestUsedReward(a.getCustomer());

        double originalAmount = a.getService().getPrice();
        double discountAmount = 0;
        if (reward != null) {
            discountAmount = reward.getDiscountAmount();
        }
        double finalAmount = originalAmount - discountAmount;

        int pointsEarned;
        if (a.getService().getType().equalsIgnoreCase("Major")) {
            pointsEarned = 50;
        } else {
            pointsEarned = 20;
        }

        int currentPoints = loyaltyService.getPoints(a.getCustomer());

        return """
            =========================================
                         SERVICE RECEIPT
            =========================================

            Payment ID: %s
            Appointment ID: %s

            Customer Username: %s
            Vehicle: %s %s (%s)
            Plate Number: %s

            Service: %s
            Original Amount: RM %.2f
            Loyalty Discount: RM %.2f
            Final Amount Paid: RM %.2f
            Payment Method: %s

            Points Earned: %d
            Current Loyalty Points: %d

            Paid On: %s %s
            Collected By: %s

            =========================================
            Thank you for your payment!
            =========================================
            """.formatted(
                payment.getPaymentID(),
                a.getAppointmentID(),
                a.getCustomer().getUsername(),
                a.getVehicle().getBrand(),
                a.getVehicle().getModel(),
                a.getVehicle().getYear(),
                a.getVehicle().getPlateNumber(),
                a.getService().getName(),
                originalAmount,
                discountAmount,
                finalAmount,
                payment.getMethod(),
                pointsEarned,
                currentPoints,
                payment.getPaymentDate(),
                payment.getPaymentTime(),
                payment.getCounterStaff().getUsername()
        );
    }

    public List<Payment> getPaymentsByCustomer(Customer customer) {
        List<Payment> list = new ArrayList<>();

        for (Payment payment : getAllPayments()) {
            if (payment.getAppointment().getCustomer().getUserID().equals(customer.getUserID())) {
                list.add(payment);
            }
        }

        return list;
    }

    private String formatPayment(Payment payment) {
        return String.join(",",
                payment.getPaymentID(),
                payment.getAppointment().getAppointmentID(),
                payment.getCounterStaff().getUserID(),
                String.format("%.2f", payment.getAmount()),
                payment.getMethod(),
                payment.getPaymentDate().toString(),
                payment.getPaymentTime().toString()
        );
    }
}