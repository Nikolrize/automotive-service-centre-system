package Main;

import Service.UserService;
import UI.LoginUI;

public class Main {

    public static void main(String[] args) {
        UserService userService = new UserService();
        userService.generateDefaultManager();
        new LoginUI();
    }
}