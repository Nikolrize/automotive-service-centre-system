//Manager: view all feedbacks and comments
//Technician: provide feedbacks
//Customer: provide comments

package Service;

import Model.Appointment;
import Model.Feedback;
import Model.Technician;
import Util.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FeedbackService {
    private static final String FILE_NAME = "feedback.txt";

    //temporary cuz appointment not created yet
    public FeedbackService() {
        //makes sure file exists even if no feedback
        FileHandler.readFile(FILE_NAME);
    }

    public List<String[]> getAllFeedbackRecords() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        List<String[]> feedbackRecords = new ArrayList<>();

        for (String line: lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",", 7); // split the lines to 7 parts max (comments may have commas)

            //Format:
            // feedbackID, appointmentID, customerID, technicianID, counter staff ID, rating, comment
            if (data.length < 7) {
                continue;
            }

            feedbackRecords.add(data);
        }

        return feedbackRecords;
    }

    // Fetch feedbacks based on current Technician
    public List<String[]> getFeedbacksByTechnician(Technician technician) {
        List<String[]> list = new ArrayList<>();

        for (String[] record : getAllFeedbackRecords()) {
            if (record[2].trim().equals(technician.getUserID())) {
                list.add(record);
            }
        }

        return list;
    }

    public void addFeedback(Appointment appointment, Technician technician, String feedbackText) {
        String id = generateNewID();
        String safeText = feedbackText.replace(",", ";");
        String dateTime = LocalDateTime.now().toString();

        String line = String.join(",",
                id,
                appointment.getAppointmentID(),
                technician.getUserID(),
                safeText,
                dateTime
        ) + System.lineSeparator(); //

        FileHandler.appendToFile(FILE_NAME, line);
    }

    private String generateNewID() {
        int max = 0;
        for (String[] record : getAllFeedbackRecords()) {
            try {
                String id = record[0].trim();
                if (id.length() < 2) continue;
                int num = Integer.parseInt(id.substring(1));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("F%04d", max + 1);
    }

    public boolean hasFeedbackRecords() {
        return !getAllFeedbackRecords().isEmpty();
    }

    public double getAverageRating(){
        List<String[]> feedbackRecords = getAllFeedbackRecords();

        if (feedbackRecords.isEmpty()) {
            return 0.0;
        }

        int totalRating = 0;
        int count = 0;

        for(String[] record: feedbackRecords) {
            try {
                totalRating += Integer.parseInt(record[5].trim());
                count++;
            } catch (NumberFormatException e) {
                //invalid rating skip to avoid breaking the whole screen
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return (double) totalRating / count;
    }

    public int getTotalFeedbackCount() {
        return getAllFeedbackRecords().size();
    }

    //temp helper methods for customer view feedback
    public boolean hasFeedbackForAppointment(String appointmentID) {
        for (String[] record : getAllFeedbackRecords()) {
            if (record.length >= 5 && record[1].trim().equals(appointmentID)) {
                return true;
            }

        }
        return false;
    }

    public String getFeedbackTextByAppointmentID(String appointmentID) {
        for (String[] record : getAllFeedbackRecords()) {
            if (record.length >= 7 && record[1].trim().equals(appointmentID)) {
                return record[6].trim();
            }

            if (record.length >= 5 && record[1].trim().equals(appointmentID)) {
                return record[4].trim();
            }
        }
        return null;
    }

}
