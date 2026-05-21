package Service;

import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private static final String APPOINTMENT_FILE = "appointments.txt";
    private static final String PAYMENT_FILE = "payments.txt";
    private static final String COMMENT_FILE = "comments.txt";
    private static final String REPORT_NOTE_FILE = "report_notes.txt";

    private static final String DEFAULT_REPORT_NOTES = """
            This report gives a simple summary for manager use.
            It reads data from appointments, payments, comments, and services files.
            The values update based on the current text-file records.
            """;

    public int getTotalAppointments() {
        return countValidLines(APPOINTMENT_FILE);
    }

    public int getCompletedAppointments() {
        List<String> lines = FileHandler.readFile(APPOINTMENT_FILE);
        int count = 0;

        for (String line : lines) {
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

    public double getTotalPaymentsCollected() {
        List<String> lines = FileHandler.readFile(PAYMENT_FILE);
        double total = 0.0;

        for (String line : lines) {
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
                // skip invalid payment rows
            }
        }

        return total;
    }

    public int getNormalServiceCount() {
        List<String> lines = FileHandler.readFile(APPOINTMENT_FILE);
        int count = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 10) {
                continue;
            }

            String serviceID = data[1].trim();

            if ("SV0001".equalsIgnoreCase(serviceID)) {
                count++;
            }
        }

        return count;
    }

    public int getMajorServiceCount() {
        List<String> lines = FileHandler.readFile(APPOINTMENT_FILE);
        int count = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 10) {
                continue;
            }

            String serviceID = data[1].trim();

            if ("SV0002".equalsIgnoreCase(serviceID)) {
                count++;
            }
        }

        return count;
    }

    public double getAverageCustomerRating() {
        List<String> lines = FileHandler.readFile(COMMENT_FILE);

        int totalRating = 0;
        int count = 0;

        for (String line : lines) {
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
                // Skip invalid rating rows
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) totalRating / count;
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

    private int countValidLines(String fileName) {
        List<String> lines = FileHandler.readFile(fileName);
        int count = 0;

        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                count++;
            }
        }

        return count;
    }
}
