package Model;

import Util.InputValidator;

public class LoyaltyReward {
    private String rewardID;
    private Customer customer;
    private String rewardName;
    private double discountAmount;
    private String status;

    public LoyaltyReward(String rewardID, Customer customer, String rewardName, double discountAmount, String status) {
        setRewardID(rewardID);
        setCustomer(customer);
        setRewardName(rewardName);
        setDiscountAmount(discountAmount);
        setStatus(status);
    }

    public String getRewardID() {
        return rewardID;
    }

    public void setRewardID(String rewardID) {
        this.rewardID = InputValidator.validateID(rewardID, "R");
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = InputValidator.validateObject(customer, "Customer");
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = InputValidator.validateText(rewardName, "Reward name");
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = InputValidator.validateAmount(discountAmount);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = InputValidator.validateRewardStatus(status);
    }
}