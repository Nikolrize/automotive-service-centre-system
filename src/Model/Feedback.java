package Model;

import java.time.LocalDateTime;

public class Feedback {
    private String feedbackID;
    private Appointment appointment;
    private String feedbackText;
    private LocalDateTime dateTime;

    public Feedback(String feedbackID, Appointment appointment, String feedbackText, LocalDateTime dateTime) {
        setFeedbackID(feedbackID);
        setAppointment(appointment);
        setFeedbackText(feedbackText);
        setDateTime(dateTime);
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

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
