//Manager: view all feedbacks and comments
package Service;

import Util.FileHandler;

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
}
