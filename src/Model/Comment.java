package Model;

public class Comment {
    private String commentID;
    private Appointment appointmentID;
    private String commentText;
    private int rating;

    public Comment(String commentID, Appointment appointmentID, String commentText, int rating) {
        this.commentID = commentID;
        this.appointmentID = appointmentID;
        this.commentText = commentText;
        this.rating = rating;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public Appointment getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(Appointment appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
