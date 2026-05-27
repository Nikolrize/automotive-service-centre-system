//Manager: view all feedbacks and comments
//Technician: provide feedbacks
//Customer: provide comments

package Service;

import Model.*;
import Util.FileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FeedbackService {
    private static final String FILE_NAME = "feedback.txt";
    private final UserService userService;
    private final AppointmentService appointmentService;
    private Runnable onFeedbackChangedCallback;
    
    public FeedbackService(UserService userService, AppointmentService appointmentService) {
        FileHandler.readFile(FILE_NAME);

        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    public List<String[]> getAllFeedbackRecords() {
        List<String> lines = FileHandler.readFile(FILE_NAME);
        List<String[]> feedbackRecords = new ArrayList<>();

        for (String line: lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",", 5);

            //Format:
            // feedbackID, appointmentID, technicianID, comment, dateTime
            if (data.length < 5) {
                continue;
            }

            feedbackRecords.add(data);
        }

        return feedbackRecords;
    }

    // To maps String[] to Feedback objects
    private Feedback parseFeedback(String[] data) throws Exception {
        String feedbackID    = data[0].trim();
        String appointmentID = data[1].trim();
        String technicianID  = data[2].trim();
        String feedbackText  = data[3].trim();
        LocalDateTime dateTime = LocalDateTime.parse(data[4].trim());

        Appointment appointment = appointmentService.getAppointmentByID(appointmentID);
        Technician technician = (Technician) userService.getUserById(technicianID);

        return new Feedback(feedbackID, appointment, technician, feedbackText, dateTime);
    }

    public List<Feedback> getAllFeedbacks() throws Exception {
        List<Feedback> list = new ArrayList<>();

        for (String[] record : getAllFeedbackRecords()) {
            list.add(parseFeedback(record));
        }

        return list;
    }

    // Fetch feedbacks based on current Technician
    public List<Feedback> getFeedbacksByTechnician(Technician technician) throws Exception {
        List<Feedback> list = new ArrayList<>();

        for (String[] record : getAllFeedbackRecords()) {
            if (record[2].trim().equals(technician.getUserID())) {
                list.add(parseFeedback(record));
            }
        }

        return list;
    }

    // Fetch feedbacks based on Appointment
    public List<String[]> getFeedbacksByAppointment(String appointmentID) {
        List<String[]> list = new ArrayList<>();

        for (String[] record : getAllFeedbackRecords()) {
            if (record[1].trim().equals(appointmentID)) {
                list.add(record);
            }
        }

        return list;
    }

    public int getTotalFeedbackCount() {
        return getAllFeedbackRecords().size();
    }

    public void addFeedback(Appointment appointment, Technician technician, String feedbackText) {
        Feedback feedback = new Feedback(
                generateNewID(),
                appointment,
                technician,
                feedbackText,
                LocalDateTime.now()
        );

        FileHandler.appendToFile(FILE_NAME, formatFeedback(feedback));
        notifyFeedbackChanged();
    }

    public void setOnFeedbackChangedCallback(Runnable callback) {
        this.onFeedbackChangedCallback = callback;
    }

    private void notifyFeedbackChanged() {
        if (onFeedbackChangedCallback != null) {
            onFeedbackChangedCallback.run();
        }
    }

    private String generateNewID() {
        int max = 0;
        for (String line : FileHandler.readFile(FILE_NAME)) {
            if (line == null || line.trim().isEmpty()) continue;
            try {
                String id = line.split(",")[0].trim();
                if (id.length() < 2) continue;
                int num = Integer.parseInt(id.substring(1));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("F%04d", max + 1);
    }

    private String formatFeedback(Feedback feedback) {
        return String.join(",",
                feedback.getFeedbackID(),
                feedback.getAppointment().getAppointmentID(),
                feedback.getTechnician().getUserID(),
                feedback.getFeedbackText().replace(",", ";"),
                feedback.getDateTime().toString()
        );
    }
}
