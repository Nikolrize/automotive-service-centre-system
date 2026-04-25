package Service;

import Util.FileHandler;

import java.util.List;

public class ReportService {
    //temporary file creation for reading only
    private static final String APPOINTMENT_FILE = "appointments.txt";
    private static final String PAYMENT_FILE = "payments.txt";
    private static final String FEEDBACK_FILE = "feedback.txt";

    public int getTotalAppointments(){
        return countValidLines(APPOINTMENT_FILE);
    }

    public int getCompletedAppointments() {
        List<String> lines = FileHandler.readFile(APPOINTMENT_FILE);
        int count = 0;

        for (String line: lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 9) {
                continue;
            }

            if (Boolean.parseBoolean(data[5].trim())) {
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
            if (data.length < 4) {
                continue;
            }

            try {
                total += Double.parseDouble(data[2].trim());
            } catch (NumberFormatException e) {
                //skip invalid payment lines
            }
        }
        return total;
    }

    public int getNormalServiceCount() {
        List<String> lines = FileHandler.readFile(APPOINTMENT_FILE);
        int count = 0;

        for (String line: lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 9) {
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
            if (data.length < 9) {
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
        List<String> lines = FileHandler.readFile(FEEDBACK_FILE);

        int totalRating = 0;
        int count = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 7) {
                continue;
            }

            try {
                totalRating += Integer.parseInt(data[5].trim());
                count++;
            } catch (NumberFormatException e) {
                //skip invalid rating lines
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) totalRating / count;

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
