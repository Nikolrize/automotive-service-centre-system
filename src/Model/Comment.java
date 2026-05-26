package Model;

import Util.InputValidator;

public class Comment {
    private String commentID;
    private Appointment appointment;
    private String commentText;
    private int rating;

    public Comment(String commentID, Appointment appointment, String commentText, int rating) {
        setCommentID(commentID);
        setAppointment(appointment);
        setCommentText(commentText);
        setRating(rating);
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = InputValidator.validateID(commentID, "CM");
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = InputValidator.validateText(commentText, "Comment");
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = InputValidator.validateRating(rating);
    }
}
