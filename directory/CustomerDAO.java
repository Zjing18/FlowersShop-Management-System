package dao;

import db.DBConnection;
import model.Customer;
import java.sql.*;
import java.util.*;

public class CustomerDAO {
    // Retrieve all customers from the database
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT id, account, password, name, sex, tel FROM Customer_Tbl WHERE del = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setAccount(rs.getString("account"));
                c.setPassword(rs.getString("password"));
                c.setName(rs.getString("name"));
                c.setSex(rs.getInt("sex"));
                c.setTel(rs.getString("tel"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a new customer to the database
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer_Tbl (account, password, name, sex, tel, del) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getAccount());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getName());
            stmt.setInt(4, customer.getSex());
            stmt.setString(5, customer.getTel());
            stmt.executeUpdate();
        }
    }

    // Retrieve a customer by ID
    public Customer getCustomerById(int id) {
        Customer customer = null;
        String sql = "SELECT id, account, password, name, sex, tel FROM Customer_Tbl WHERE id = ? AND del = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setId(rs.getInt("id"));
                    customer.setAccount(rs.getString("account"));
                    customer.setPassword(rs.getString("password"));
                    customer.setName(rs.getString("name"));
                    customer.setSex(rs.getInt("sex"));
                    customer.setTel(rs.getString("tel"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    // Update customer information
    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customer_Tbl SET account = ?, password = ?, name = ?, sex = ?, tel = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getAccount());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getName());
            stmt.setInt(4, customer.getSex());
            stmt.setString(5, customer.getTel());
            stmt.setInt(6, customer.getId());
            stmt.executeUpdate();
        }
    }

    // Delete a customer by ID (soft delete)
    public void deleteCustomer(int id) throws SQLException {
        String sql = "UPDATE Customer_Tbl SET del = 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}