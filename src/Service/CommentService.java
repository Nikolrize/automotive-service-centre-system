package Service;

import Model.Appointment;
import Model.Comment;
import Model.Customer;
import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class CommentService {
    private static final String FILE_NAME = "comments.txt";

    private final AppointmentService appointmentService;

    public CommentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;

        // ensure file exists
        FileHandler.readFile(FILE_NAME);
    }

    private Comment parseComment(String line) throws Exception {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] data = line.split(",", 7);
        if (data.length < 7) return null;

        String commentID = data[0].trim();
        String appointmentID = data[1].trim();
        int rating = Integer.parseInt(data[5].trim());
        String commentText = data[6].trim();

        Appointment appointment = appointmentService.getAppointmentByID(appointmentID);

        if (appointment == null) {
            throw new Exception("Appointment not found for comment.");
        }

        return new Comment(commentID, appointment, commentText, rating);
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();

        for (String line : FileHandler.readFile(FILE_NAME)) {
            try {
                Comment comment = parseComment(line);
                if (comment != null) {
                    comments.add(comment);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to get comments.");
            }
        }

        return comments;
    }

    public List<Comment> getCommentsByCustomer(Customer customer) {
        List<Comment> comments = new ArrayList<>();

        for (Comment comment : getAllComments()) {
            if (comment.getAppointment().getCustomer().getUserID().equals(customer.getUserID())) {
                comments.add(comment);
            }
        }

        return comments;
    }

    public Comment getCommentByAppointmentID(String appointmentID) {
        for (Comment comment : getAllComments()) {
            if (comment.getAppointment().getAppointmentID().equals(appointmentID)) {
                return comment;
            }
        }

        return null;
    }

    public boolean hasCommentForAppointment(String appointmentID) {
        return getCommentByAppointmentID(appointmentID) != null;
    }

    private String generateNewID() {
        int max = 0;

        for (String line : FileHandler.readFile(FILE_NAME)) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 1) continue;

            String id = data[0].trim();

            if (!id.startsWith("CM") || id.length() <= 2) {
                continue;
            }

            try {
                int num = Integer.parseInt(id.substring(2));
                if (num > max) {
                    max = num;
                }
            } catch (NumberFormatException e) {
                // skip malformed IDs
            }
        }

        return String.format("CM%04d", max + 1);
    }

    public void createComment(Customer customer, Appointment appointment, String ratingText, String commentText) throws Exception {
        if (customer == null || appointment == null) {
            throw new Exception("Customer and appointment are required.");
        }

        if (!appointment.getCustomer().getUserID().equals(customer.getUserID())) {
            throw new Exception("You can only comment on your own appointments.");
        }

        if (!appointment.getIsCompleted()) {
            throw new Exception("You can only comment after the appointment is completed.");
        }

        if (hasCommentForAppointment(appointment.getAppointmentID())) {
            throw new Exception("A comment already exists for this appointment.");
        }

        if (commentText == null || commentText.trim().isEmpty()) {
            throw new Exception("Comment cannot be empty.");
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingText.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Rating must be a valid whole number.");
        }

        if (rating < 1 || rating > 5) {
            throw new Exception("Rating must be between 1 and 5.");
        }

        String commentID = generateNewID();

        String data = String.join(",",
                commentID,
                appointment.getAppointmentID(),
                customer.getUserID(),
                appointment.getTechnician().getUserID(),
                appointment.getCounterStaff().getUserID(),
                String.valueOf(rating),
                commentText.trim()
        );

        FileHandler.appendToFile(FILE_NAME, data);
    }
}
