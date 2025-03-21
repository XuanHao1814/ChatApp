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

    public boolean register(String username, String password, String email) {
        return userDAO.registerUser(username, password, email);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
}