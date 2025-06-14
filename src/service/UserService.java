package service;

import DAO.UserDAO;
import model.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public boolean registerUser(String username, String password, String email) {
        // Basic validation
        if (username == null || password == null || email == null ||
            username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            return false;
        }
        return userDAO.registerUser(username, password, email);
    }
}