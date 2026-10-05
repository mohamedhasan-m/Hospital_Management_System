package com.hospital.service;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import com.hospital.util.PasswordUtil;

import java.sql.SQLException;

/** Login logic: hash the typed password and compare it with the one stored in the database. */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /** Returns the logged-in user, or null when the username/password is wrong. */
    public User login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return userDAO.findByLogin(username.trim(), PasswordUtil.sha256(password));
    }
}
