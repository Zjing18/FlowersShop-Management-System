package dao;

import db.DBConnection;
import model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    // Retrieve all orders with optional keyword filtering
    public List<Order> getAllOrders(String keyword) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT id, customerID, orderDate, deliveryDate, totalPrice, orderNote, status FROM Order_Tbl WHERE del = 0";
        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND (orderNote LIKE ? OR customerID LIKE ?)";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (keyword != null && !keyword.isEmpty()) {
                String searchKeyword = "%" + keyword + "%";
                stmt.setString(1, searchKeyword);
                stmt.setString(2, searchKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setCustomerId(rs.getInt("customerID"));
                    order.setOrderDate(rs.getTimestamp("orderDate"));
                    order.setDeliveryDate(rs.getTimestamp("deliveryDate"));
                    order.setTotalPrice(rs.getDouble("totalPrice"));
                    order.setOrderNote(rs.getString("orderNote"));
                    order.setStatus(rs.getInt("status"));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add a new order
    public void addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO Order_Tbl (customerID, orderDate, deliveryDate, totalPrice, orderNote, status, del) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getCustomerId());
            stmt.setTimestamp(2, order.getOrderDate());
            stmt.setTimestamp(3, order.getDeliveryDate());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setString(5, order.getOrderNote());
            stmt.setInt(6, order.getStatus());

            stmt.executeUpdate();
        }
    }

    // Get an order by ID
    public Order getOrderById(int id) {
        String sql = "SELECT id, customerID, orderDate, deliveryDate, totalPrice, orderNote, status FROM Order_Tbl WHERE id = ? AND del = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setCustomerId(rs.getInt("customerID"));
                    order.setOrderDate(rs.getTimestamp("orderDate"));
                    order.setDeliveryDate(rs.getTimestamp("deliveryDate"));
                    order.setTotalPrice(rs.getDouble("totalPrice"));
                    order.setOrderNote(rs.getString("orderNote"));
                    order.setStatus(rs.getInt("status"));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update an existing order
    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE Order_Tbl SET customerID = ?, deliveryDate = ?, totalPrice = ?, orderNote = ?, status = ? WHERE id = ? AND del = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getCustomerId());
            stmt.setTimestamp(2, order.getDeliveryDate());
            stmt.setDouble(3, order.getTotalPrice());
            stmt.setString(4, order.getOrderNote());
            stmt.setInt(5, order.getStatus());
            stmt.setInt(6, order.getId());

            stmt.executeUpdate();
        }
    }

    // Delete an order (mark as deleted)
    public void deleteOrder(int id) throws SQLException {
        String sql = "UPDATE Order_Tbl SET del = 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }
}