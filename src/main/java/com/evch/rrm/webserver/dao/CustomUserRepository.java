package com.evch.rrm.webserver.dao;

import com.evch.rrm.webserver.model.User;
import com.evch.rrm.webserver.util.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomUserRepository {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = Repository.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserById(Long id) {
        User user = new User();

        try (Connection conn = Repository.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User createUser(User user) {
        try (Connection conn = Repository.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO users (name, email, address) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getAddress());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                user.setId(id);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User updateUser(Long id, User user) {
        try (Connection conn = Repository.getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE users SET name = ?, email = ?, address = ? WHERE id = ?")) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getAddress());
            statement.setLong(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User patchUser(Long id, Map<String, Object> updates) {
        User user = getUserById(id);
        updates.forEach((field, value) -> {
            switch (field) {
                case "name" -> user.setName((String) value);
                case "email" -> user.setEmail((String) value);
                case "address" -> user.setAddress((String) value);
            }
        });
        updateUser(id, user);
        return user;
    }

    public void deleteUser(Long id) {
        try (Connection conn = Repository.getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
