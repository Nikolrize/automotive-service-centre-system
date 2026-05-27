package Service;

import Model.Appointment;
import Model.Comment;
import Model.CounterStaff;
import Model.Feedback;
import Model.Payment;
import Model.Service;
import Model.Technician;
import Model.User;
import Util.FileHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private static final String USER_FILE = "users.txt";
    private static final String VEHICLE_FILE = "vehicles.txt";
    private static final String APPOINTMENT_FILE = "appointments.txt";
    private static final String PAYMENT_FILE = "payments.txt";
    private static final String COMMENT_FILE = "comments.txt";
    private static final String REPORT_NOTE_FILE = "report_notes.txt";

    //for time
    private static final DateTimeFormatter REPORT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    //default report notes
    private static final String DEFAULT_REPORT_NOTES = """
            This report gives a simple summary for manager use.
            It reads data from users, vehicles, appointments, payments, and comments files.
            The values update based on the current text-file records.
            """;

    private final UserService userService;
    private final ServiceManagementService serviceManagementService;
    private final VehicleService vehicleService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final CommentService commentService;
    private final FeedbackService feedbackService;

    public ReportService() {
        this.userService = new UserService();
        this.serviceManagementService = new ServiceManagementService();
        this.vehicleService = new VehicleService(userService);
        this.appointmentService = new AppointmentService(userService, serviceManagementService, vehicleService);
        this.paymentService = new PaymentService(appointmentService, userService);
        this.commentService = new CommentService(appointmentService);
        this.feedbackService = new FeedbackService(userService, appointmentService);
    }

    //counts all active users
    public int getTotalUsers() {
        int count = 0;

        for (String line : FileHandler.readFile(USER_FILE)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 9) {
                continue;
            }

            if (Boolean.parseBoolean(data[7].trim())) {
                count++;
            }
        }

        return count;
    }

    public int getTotalManagers() {
        return countActiveUsersByRolePrefix('M');
    }

    public int getTotalCounterStaff() {
        return countActiveUsersByRolePrefix('S');
    }

    public int getTotalTechnicians() {
        return countActiveUsersByRolePrefix('T');
    }

    public int getTotalCustomers() {
        return countActiveUsersByRolePrefix('C');
    }

    public int getTotalVehicles() {
        return countValidLines(VEHICLE_FILE);
    }

    public int getTotalAppointments() {
        return countValidLines(APPOINTMENT_FILE);
    }

    public int getCompletedAppointments() {
        int count = 0;

        for (String line : FileHandler.readFile(APPOINTMENT_FILE)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 10) {
                continue;
            }

            if (Boolean.parseBoolean(data[6].trim())) {
                count++;
            }
        }

        return count;
    }

    public int getPendingAppointments() {
        return getTotalAppointments() - getCompletedAppointments();
    }

    public double getTotalPaymentsCollected() {
        double total = 0.0;

        for (String line : FileHandler.readFile(PAYMENT_FILE)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 7) {
                continue;
            }

            try {
                total += Double.parseDouble(data[3].trim());
            } catch (NumberFormatException e) {
                // Skip invalid payment rows.
            }
        }

        return total;
    }

    public int getNormalServiceCount() {
        int count = 0;

        for (Service service : serviceManagementService.getAllServices()) {
            if ("Normal".equalsIgnoreCase(service.getType())) {
                count++;
            }
        }

        return count;
    }

    public int getMajorServiceCount() {
        int count = 0;

        for (Service service : serviceManagementService.getAllServices()) {
            if ("Major".equalsIgnoreCase(service.getType())) {
                count++;
            }
        }

        return count;
    }

    public double getAverageCustomerRating() {
        int totalRating = 0;
        int count = 0;

        for (String line : FileHandler.readFile(COMMENT_FILE)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",", 7);
            if (data.length < 7) {
                continue;
            }

            try {
                totalRating += Integer.parseInt(data[5].trim());
                count++;
            } catch (NumberFormatException e) {
                // Skip invalid rating rows.
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) totalRating / count;
    }

    //employees (counter staff + technicians)
    public List<User> getReportEmployees() {
        List<User> employees = new ArrayList<>();

        for (User user : userService.getActiveUsers()) {
            if (user instanceof CounterStaff || user instanceof Technician) {
                employees.add(user);
            }
        }

        return employees;
    }

    //manager report
    public String generateOverallReport() {
        return """
                ============================================================
                                  OVERALL MANAGER REPORT
                ============================================================

                Generated On: %s

                ACTIVE USER SUMMARY
                ------------------------------------------------------------
                Total Active Users      : %d
                Active Managers         : %d
                Active Counter Staff    : %d
                Active Technicians      : %d
                Active Customers        : %d

                OPERATION SUMMARY
                ------------------------------------------------------------
                Total Vehicles          : %d
                Total Appointments      : %d
                Completed Appointments  : %d
                Pending Appointments    : %d
                Normal Services         : %d
                Major Services          : %d

                PAYMENT AND RATING SUMMARY
                ------------------------------------------------------------
                Total Payments          : RM %.2f
                Average Customer Rating : %.2f

                REPORT NOTES
                ------------------------------------------------------------
                %s

                ============================================================
                End of overall report
                ============================================================
                """.formatted(
                LocalDateTime.now().format(REPORT_DATE_TIME_FORMAT),
                getTotalUsers(),
                getTotalManagers(),
                getTotalCounterStaff(),
                getTotalTechnicians(),
                getTotalCustomers(),
                getTotalVehicles(),
                getTotalAppointments(),
                getCompletedAppointments(),
                getPendingAppointments(),
                getNormalServiceCount(),
                getMajorServiceCount(),
                getTotalPaymentsCollected(),
                getAverageCustomerRating(),
                formatReportNotesForMonospace(getReportNotes())
        );
    }

    public String generateEmployeeWorkloadReport(User employee) throws Exception {
        if (employee == null) {
            throw new Exception("Please select an employee first.");
        }

        if (employee instanceof Technician technician) {
            return generateTechnicianWorkloadReport(technician);
        }

        if (employee instanceof CounterStaff counterStaff) {
            return generateCounterStaffWorkloadReport(counterStaff);
        }

        throw new Exception("This employee role does not have workload tracking.");
    }

    //convert to pdf file
    public void exportReportToPdf(String reportContent, File file) throws Exception {
        if (reportContent == null || reportContent.isBlank()) {
            throw new Exception("There is no report content to export.");
        }

        if (file == null) {
            throw new Exception("Please choose a file location.");
        }

        String[] lines = reportContent.replace("\r", "").split("\n", -1); //split report into lines
        List<List<String>> pages = paginateLines(lines, 42);

        int pageCount = pages.size();
        int fontObjectNumber = 3 + (pageCount * 2);

        List<String> objects = new ArrayList<>();
        objects.add(null);
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");

        //build page references
        StringBuilder kidsBuilder = new StringBuilder();
        for (int i = 0; i < pageCount; i++) {
            int pageObjectNumber = 3 + (i * 2);
            kidsBuilder.append(pageObjectNumber).append(" 0 R ");
        }

        objects.add("<< /Type /Pages /Count " + pageCount + " /Kids [" + kidsBuilder.toString().trim() + "] >>");

        for (int i = 0; i < pageCount; i++) {
            int contentObjectNumber = 4 + (i * 2);

            String pageObject = """
                    << /Type /Page
                    /Parent 2 0 R
                    /MediaBox [0 0 595 842]
                    /Resources << /Font << /F1 %d 0 R >> >>
                    /Contents %d 0 R
                    >>
                    """.formatted(fontObjectNumber, contentObjectNumber).trim();

            String contentStream = buildPdfContentStream(pages.get(i));
            String contentObject = """
                    << /Length %d >>
                    stream
                    %s
                    endstream
                    """.formatted(contentStream.getBytes(StandardCharsets.ISO_8859_1).length, contentStream);

            objects.add(pageObject);
            objects.add(contentObject);
        }

        //courier font
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        writePdfObjects(objects, file);
    }

    public String getReportNotes() {
        List<String> lines = FileHandler.readFile(REPORT_NOTE_FILE);

        if (lines.isEmpty()) {
            saveReportNotes(DEFAULT_REPORT_NOTES);
            return DEFAULT_REPORT_NOTES;
        }

        return String.join("\n", lines).trim();
    }

    public void saveReportNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            notes = DEFAULT_REPORT_NOTES;
        }

        List<String> lines = new ArrayList<>();
        String[] splitLines = notes.strip().split("\\R");
        lines.addAll(List.of(splitLines));

        FileHandler.writeFile(REPORT_NOTE_FILE, lines);
    }

    //technician
    private String generateTechnicianWorkloadReport(Technician technician) throws Exception {
        List<Appointment> assignedAppointments = appointmentService.getAppointmentsByTechnician(technician);
        List<Comment> relatedComments = commentService.getCommentsByTechnician(technician);
        List<Feedback> submittedFeedback = feedbackService.getFeedbacksByTechnician(technician);

        int completedCount = 0;
        int pendingCount = 0;
        int normalCount = 0;
        int majorCount = 0;
        int totalHours = 0;

        for (Appointment appointment : assignedAppointments) {
            if (appointment.getIsCompleted()) {
                completedCount++;
            } else {
                pendingCount++;
            }

            if ("Normal".equalsIgnoreCase(appointment.getService().getType())) {
                normalCount++;
            } else if ("Major".equalsIgnoreCase(appointment.getService().getType())) {
                majorCount++;
            }

            totalHours += appointment.getService().getDuration();
        }

        double averageRating = calculateAverageRating(relatedComments);

        return """
                ============================================================
                               EMPLOYEE WORKLOAD REPORT
                ============================================================

                Generated On: %s

                EMPLOYEE DETAILS
                ------------------------------------------------------------
                Employee Name          : %s %s
                Username               : %s
                User ID                : %s
                Role                   : %s

                WORKLOAD SUMMARY
                ------------------------------------------------------------
                Assigned Appointments  : %d
                Completed Appointments : %d
                Pending Appointments   : %d
                Normal Services        : %d
                Major Services         : %d
                Total Service Hours    : %d
                Feedback Submitted     : %d
                Comments Received      : %d
                Average Rating         : %.2f

                ASSIGNED APPOINTMENTS
                ------------------------------------------------------------
                %s

                ============================================================
                End of employee workload report
                ============================================================
                """.formatted(
                LocalDateTime.now().format(REPORT_DATE_TIME_FORMAT),
                technician.getFirstName(),
                technician.getLastName(),
                technician.getUsername(),
                technician.getUserID(),
                technician.getRoleName(),
                assignedAppointments.size(),
                completedCount,
                pendingCount,
                normalCount,
                majorCount,
                totalHours,
                submittedFeedback.size(),
                relatedComments.size(),
                averageRating,
                formatAppointmentList(assignedAppointments, true)
        );
    }

    //counterstaff
    private String generateCounterStaffWorkloadReport(CounterStaff counterStaff) {
        List<Appointment> handledAppointments = getAppointmentsByCounterStaff(counterStaff);
        List<Payment> collectedPayments = getPaymentsByCounterStaff(counterStaff);
        List<Comment> relatedComments = getCommentsByCounterStaff(counterStaff);

        int completedCount = 0;
        int pendingCount = 0;
        int normalCount = 0;
        int majorCount = 0;
        double totalCollected = 0.0;

        for (Appointment appointment : handledAppointments) {
            if (appointment.getIsCompleted()) {
                completedCount++;
            } else {
                pendingCount++;
            }

            if ("Normal".equalsIgnoreCase(appointment.getService().getType())) {
                normalCount++;
            } else if ("Major".equalsIgnoreCase(appointment.getService().getType())) {
                majorCount++;
            }
        }

        for (Payment payment : collectedPayments) {
            totalCollected += payment.getAmount();
        }

        double averageRating = calculateAverageRating(relatedComments);

        return """
                ============================================================
                               EMPLOYEE WORKLOAD REPORT
                ============================================================

                Generated On: %s

                EMPLOYEE DETAILS
                ------------------------------------------------------------
                Employee Name          : %s %s
                Username               : %s
                User ID                : %s
                Role                   : %s

                WORKLOAD SUMMARY
                ------------------------------------------------------------
                Managed Appointments   : %d
                Completed Appointments : %d
                Pending Appointments   : %d
                Normal Services        : %d
                Major Services         : %d
                Payments Collected     : %d
                Total Collected        : RM %.2f
                Related Comments       : %d
                Average Rating         : %.2f

                HANDLED APPOINTMENTS
                ------------------------------------------------------------
                %s

                COLLECTED PAYMENTS
                ------------------------------------------------------------
                %s

                ============================================================
                End of employee workload report
                ============================================================
                """.formatted(
                LocalDateTime.now().format(REPORT_DATE_TIME_FORMAT),
                counterStaff.getFirstName(),
                counterStaff.getLastName(),
                counterStaff.getUsername(),
                counterStaff.getUserID(),
                counterStaff.getRoleName(),
                handledAppointments.size(),
                completedCount,
                pendingCount,
                normalCount,
                majorCount,
                collectedPayments.size(),
                totalCollected,
                relatedComments.size(),
                averageRating,
                formatAppointmentList(handledAppointments, false),
                formatPaymentList(collectedPayments)
        );
    }

    private List<Appointment> getAppointmentsByCounterStaff(CounterStaff counterStaff) {
        List<Appointment> appointments = new ArrayList<>();

        for (Appointment appointment : appointmentService.getAllAppointments()) {
            if (appointment.getCounterStaff().getUserID().equals(counterStaff.getUserID())) {
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    private List<Payment> getPaymentsByCounterStaff(CounterStaff counterStaff) {
        List<Payment> payments = new ArrayList<>();

        for (Payment payment : paymentService.getAllPayments()) {
            if (payment.getCounterStaff().getUserID().equals(counterStaff.getUserID())) {
                payments.add(payment);
            }
        }

        return payments;
    }

    private List<Comment> getCommentsByCounterStaff(CounterStaff counterStaff) {
        List<Comment> comments = new ArrayList<>();

        for (Comment comment : commentService.getAllComments()) {
            if (comment.getAppointment().getCounterStaff().getUserID().equals(counterStaff.getUserID())) {
                comments.add(comment);
            }
        }

        return comments;
    }

    private double calculateAverageRating(List<Comment> comments) {
        if (comments.isEmpty()) {
            return 0.0;
        }

        int total = 0;

        for (Comment comment : comments) {
            total += comment.getRating();
        }

        return (double) total / comments.size();
    }

    private String formatAppointmentList(List<Appointment> appointments, boolean includeFeedbackStatus) {
        if (appointments.isEmpty()) {
            return "No appointments found for this employee.";
        }

        StringBuilder builder = new StringBuilder();

        for (Appointment appointment : appointments) {
            builder.append("Appointment ")
                    .append(appointment.getAppointmentID())
                    .append(" | ")
                    .append(appointment.getService().getName())
                    .append(" | Customer: ")
                    .append(appointment.getCustomer().getUsername())
                    .append(" | Date: ")
                    .append(appointment.getDate())
                    .append(" | Status: ")
                    .append(appointment.getIsCompleted() ? "Completed" : "Pending");

            if (includeFeedbackStatus) {
                builder.append(" | Feedback: ")
                        .append(hasFeedbackForAppointment(appointment.getAppointmentID()) ? "Submitted" : "Not Submitted");
            }

            builder.append("\n");
        }

        return builder.toString().trim();
    }

    private String formatPaymentList(List<Payment> payments) {
        if (payments.isEmpty()) {
            return "No payments collected by this employee.";
        }

        StringBuilder builder = new StringBuilder();

        for (Payment payment : payments) {
            builder.append("Payment ")
                    .append(payment.getPaymentID())
                    .append(" | Appointment: ")
                    .append(payment.getAppointment().getAppointmentID())
                    .append(" | Amount: RM ")
                    .append(String.format("%.2f", payment.getAmount()))
                    .append(" | Method: ")
                    .append(payment.getMethod())
                    .append(" | Date: ")
                    .append(payment.getPaymentDate())
                    .append("\n");
        }

        return builder.toString().trim();
    }

    private boolean hasFeedbackForAppointment(String appointmentID) {
        try {
            for (Feedback feedback : feedbackService.getAllFeedbacks()) {
                if (feedback.getAppointment().getAppointmentID().equals(appointmentID)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    //ensures displays neatly
    private String formatReportNotesForMonospace(String notes) {
        if (notes == null || notes.isBlank()) {
            return "-";
        }

        return notes.strip();
    }

    private int countActiveUsersByRolePrefix(char rolePrefix) {
        int count = 0;

        for (String line : FileHandler.readFile(USER_FILE)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 9) {
                continue;
            }

            boolean isActive = Boolean.parseBoolean(data[7].trim());
            String roleID = data[8].trim();

            if (isActive && !roleID.isEmpty() && roleID.charAt(0) == rolePrefix) {
                count++;
            }
        }

        return count;
    }

    private int countValidLines(String fileName) {
        int count = 0;

        for (String line : FileHandler.readFile(fileName)) {
            if (line != null && !line.trim().isEmpty()) {
                count++;
            }
        }

        return count;
    }

    //dividing into pages
    private List<List<String>> paginateLines(String[] lines, int linesPerPage) {
        List<List<String>> pages = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();

        for (String line : lines) {
            currentPage.add(line);

            if (currentPage.size() == linesPerPage) {
                pages.add(new ArrayList<>(currentPage));
                currentPage.clear();
            }
        }

        if (!currentPage.isEmpty() || pages.isEmpty()) {
            pages.add(new ArrayList<>(currentPage));
        }

        return pages;
    }

    //converts report lines into pdf drawing commands
    private String buildPdfContentStream(List<String> lines) {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n"); //beginning
        stream.append("/F1 10 Tf\n"); //font
        stream.append("50 790 Td\n"); //starting text position
        stream.append("14 TL\n"); //spacing to 14

        if (lines.isEmpty()) {
            stream.append("(No report data.) Tj\n");
        } else {
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    stream.append("T*\n");
                }

                //escaped text
                stream.append("(")
                        .append(escapePdfText(lines.get(i)))
                        .append(") Tj\n");
            }
        }

        stream.append("ET\n");
        return stream.toString();
    }

    //escapes special characters
    private String escapePdfText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    //writes all pdf objects into the final PDF file
    private void writePdfObjects(List<String> objects, File file) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));

        List<Integer> offsets = new ArrayList<>(); //stores byte offsets for pdf cross-reference table
        offsets.add(0);

        //write each pdf object
        for (int i = 1; i < objects.size(); i++) {
            offsets.add(output.size());
            writeText(output, i + " 0 obj\n");
            writeText(output, objects.get(i));
            writeText(output, "\nendobj\n");
        }

        int xrefOffset = output.size();

        writeText(output, "xref\n"); //begin cross-reference table
        writeText(output, "0 " + objects.size() + "\n");
        writeText(output, "0000000000 65535 f \n"); //first reserved objects

        for (int i = 1; i < objects.size(); i++) {
            writeText(output, String.format("%010d 00000 n \n", offsets.get(i)));
        }

        writeText(output, "trailer\n"); //trailer
        writeText(output, "<< /Size " + objects.size() + " /Root 1 0 R >>\n");
        writeText(output, "startxref\n"); //start cross-reference position
        writeText(output, String.valueOf(xrefOffset));
        writeText(output, "\n%%EOF"); //end

        //save into file
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            output.writeTo(fileOutputStream);
        }
    }

    private void writeText(ByteArrayOutputStream output, String text) throws Exception {
        output.write(text.getBytes(StandardCharsets.ISO_8859_1));
    }
}
