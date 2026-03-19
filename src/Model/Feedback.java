package Model;

public class Feedback {
    private String feedbackID;
    private Appointment appointment;
    private String comment;
    private int rating;

    public Feedback(String feedbackID, Appointment appointment, String comment, int rating) {
        setFeedbackID(feedbackID);
        setAppointment(appointment);
        setComment(comment);
        setRating(rating);
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(String feedbackID) {
        this.feedbackID = feedbackID;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
