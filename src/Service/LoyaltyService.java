package Service;

import Model.Customer;
import Model.Appointment;
import Model.LoyaltyReward;
import Util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyService {
    private static final String POINTS_FILE_NAME = "loyalty_points.txt";
    private static final String REWARDS_FILE_NAME = "loyalty_rewards.txt";

    public void addPoints(Appointment appointment) {
        Customer customer = appointment.getCustomer();

        int points = calculatePoints(appointment);
        int currentPoints = getPoints(customer);
        int newPoints = currentPoints + points;

        savePoints(customer, newPoints);
    }

    private int calculatePoints(Appointment appointment) {
        String serviceType = appointment.getService().getType();

        if (serviceType.equalsIgnoreCase("Major")) {
            return 50;
        } else {
            return 20;
        }
    }

    public int getPoints(Customer customer) {
        List<String> lines = FileHandler.readFile(POINTS_FILE_NAME);

        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length < 2) continue;

            if (data[0].equals(customer.getUserID())) {
                return Integer.parseInt(data[1]);
            }
        }

        return 0;
    }

    private void savePoints(Customer customer, int points) {
        List<String> lines = FileHandler.readFile(POINTS_FILE_NAME);
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");
            if (data.length < 2) continue;

            if (data[0].equals(customer.getUserID())) {
                data[1] = String.valueOf(points);
                lines.set(i, String.join(",", data));
                updated = true;
                break;
            }
        }

        if (!updated) {
            lines.add(customer.getUserID() + "," + points);
        }

        FileHandler.writeFile(POINTS_FILE_NAME, lines);
    }

    private String generateRewardID() {
        List<String> lines = FileHandler.readFile(REWARDS_FILE_NAME);

        int max = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length < 1) {
                continue;
            }

            String rewardID = data[0].trim();
            if (!rewardID.startsWith("R")) {
                continue;
            }

            int number = Integer.parseInt(rewardID.substring(1));
            if (number > max) {
                max = number;
            }
        }

        return String.format("R%04d", max + 1);
    }

    public String redeemReward(Customer customer, String rewardName, double discountAmount, int pointsNeeded) throws Exception {
        int points = getPoints(customer);

        if (points < pointsNeeded) {
            throw new Exception("Not enough points to redeem this reward.");
        }

        for (LoyaltyReward reward : getRewardsByCustomer(customer)) {
            if (reward.getStatus().equalsIgnoreCase("In Use")) {
                throw new Exception("You already have a reward waiting to be used.");
            }
        }

        savePoints(customer, points - pointsNeeded);

        String rewardID = generateRewardID();

        LoyaltyReward reward = new LoyaltyReward(
                rewardID,
                customer,
                rewardName,
                discountAmount,
                "In Use"
        );

        String data = String.join(",",
                reward.getRewardID(),
                reward.getCustomer().getUserID(),
                reward.getRewardName(),
                String.format("%.2f", reward.getDiscountAmount()),
                String.valueOf(reward.getStatus())
        );

        FileHandler.appendToFile(REWARDS_FILE_NAME, data);

        return rewardName + " redeemed successfully. It will be used on your next service payment.";
    }

    public double applyReward(Customer customer, double originalAmount) {
        List<String> lines = FileHandler.readFile(REWARDS_FILE_NAME);

        for (int i = 0; i < lines.size(); i++) {
            LoyaltyReward reward = parseReward(lines.get(i), customer);

            if (reward == null) {
                continue;
            }

            if (reward.getStatus().equalsIgnoreCase("In Use")) {
                double finalAmount = originalAmount - reward.getDiscountAmount();

                if (finalAmount < 0) {
                    finalAmount = 0;
                }

                String updatedData = String.join(",",
                        reward.getRewardID(),
                        reward.getCustomer().getUserID(),
                        reward.getRewardName(),
                        String.format("%.2f", reward.getDiscountAmount()),
                        "Used"
                );

                lines.set(i, updatedData);
                FileHandler.writeFile(REWARDS_FILE_NAME, lines);

                return finalAmount;
            }
        }

        return originalAmount;
    }

    private LoyaltyReward parseReward(String line, Customer customer) {
        String[] data = line.split(",");
        if (data.length < 5) return null;

        String rewardID = data[0].trim();
        String customerID = data[1].trim();
        String rewardName = data[2].trim();
        double discountAmount = Double.parseDouble(data[3].trim());
        String status = data[4].trim();

        if (!customerID.equals(customer.getUserID())) {
            return null;
        }

        return new LoyaltyReward(
                rewardID,
                customer,
                rewardName,
                discountAmount,
                status
        );
    }

    public List<LoyaltyReward> getRewardsByCustomer(Customer customer) {
        List<LoyaltyReward> rewards = new ArrayList<>();

        for (String line : FileHandler.readFile(REWARDS_FILE_NAME)) {
            LoyaltyReward reward = parseReward(line, customer);

            if (reward != null) {
                rewards.add(reward);
            }
        }

        return rewards;
    }

    public LoyaltyReward getLatestUsedReward(Customer customer) {
        List<LoyaltyReward> rewards = getRewardsByCustomer(customer);

        for (int i = rewards.size() - 1; i >= 0; i--) {
            LoyaltyReward reward = rewards.get(i);

            if (reward.getStatus().equalsIgnoreCase("Used")) {
                return reward;
            }
        }

        return null;
    }
}