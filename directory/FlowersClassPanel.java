
package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

class FlowerClassPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public FlowerClassPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("植物类别管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for search field
        JButton searchBtn = new JButton("查询");
        JButton resetBtn = new JButton("重置");
        JButton addBtn = new JButton("新增");
        JButton deleteBtn = new JButton("删除");

        // Set font size for buttons
        Font buttonFont = new Font("宋体", Font.BOLD, 18);
        searchBtn.setFont(buttonFont);
        resetBtn.setFont(buttonFont);
        addBtn.setFont(buttonFont);
        deleteBtn.setFont(buttonFont);

        toolBar.add(new JLabel("名称:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(deleteBtn);
        add(toolBar, BorderLayout.NORTH);

        String[] columnNames = {"ID", "名称", "图片", "单价", "库存数量", "描述", "状态"};
        model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for table cells
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 20)); // Increased font size for table headers
        table.getColumnModel().getColumn(2).setCellRenderer(new ImageRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData("");

        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, name, price, quantity, description, state FROM FlowerClass_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND name LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String imagePath = getImagePath(name);
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        name,
                        new ImageIcon(img),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("description"),
                        rs.getInt("state") == 1 ? "上架" : "下架"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField descField = new JTextField();
        JButton chooseBtn = new JButton("选择图片");
        JLabel imagePathLabel = new JLabel();

        chooseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                imagePathLabel.setText(file.getAbsolutePath());
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("名称:"));
        panel.add(nameField);
        panel.add(new JLabel("单价:"));
        panel.add(priceField);
        panel.add(new JLabel("库存数量:"));
        panel.add(qtyField);
        panel.add(new JLabel("描述:"));
        panel.add(descField);
        panel.add(new JLabel("图片路径:"));
        panel.add(imagePathLabel);
        panel.add(new JLabel());
        panel.add(chooseBtn);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增植物类别", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO FlowerClass_Tbl (name, price, quantity, description, state) VALUES (?, ?, ?, ?, 1)")) {
                String name = nameField.getText();
                ps.setString(1, name);
                ps.setDouble(2, Double.parseDouble(priceField.getText()));
                ps.setInt(3, Integer.parseInt(qtyField.getText()));
                ps.setString(4, descField.getText());
                ps.executeUpdate();

                if (!imagePathLabel.getText().isEmpty()) {
                    File src = new File(imagePathLabel.getText());
                    File dest = new File("src/images/" + name + ".jpg");
                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                loadData("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "添加失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE FlowerClass_Tbl SET del = 1 WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "删除成功");
            loadData("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
        }
    }

    private String getImagePath(String flowerType) {
        return switch (flowerType) {
            case "玫瑰" -> "src/images/03.jpg";
            case "百合" -> "src/images/04.jpg";
            case "康乃馨" -> "src/images/05.jpg";
            case "向日葵" -> "src/images/06.jpg";
            default -> "src/images/" + flowerType + ".jpg";
        };
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon icon) {
                JLabel label = new JLabel(icon);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}

class FlowerInfoPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public FlowerInfoPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("植物信息管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for search field
        JButton searchBtn = new JButton("查询");
        JButton resetBtn = new JButton("重置");
        JButton addBtn = new JButton("新增");
        JButton editBtn = new JButton("编辑");
        JButton deleteBtn = new JButton("删除");

        // Set font size for buttons
        Font buttonFont = new Font("宋体", Font.BOLD, 18);
        searchBtn.setFont(buttonFont);
        resetBtn.setFont(buttonFont);
        addBtn.setFont(buttonFont);
        editBtn.setFont(buttonFont);
        deleteBtn.setFont(buttonFont);

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "类别ID", "库存数量", "备注"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for table cells
        table.setRowHeight(30); // Adjusted row height
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 20)); // Increased font size for table headers
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, flowerClassID, stockQuantity, note FROM Flower_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND note LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("flowerClassID"),
                        rs.getInt("stockQuantity"),
                        rs.getString("note")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerClassField = new JTextField();
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField stockQuantityField = new JTextField();
        stockQuantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField();
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("类别ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("库存数量:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增植物信息", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Flower_Tbl (flowerClassID, stockQuantity, note, del) VALUES (?, ?, ?, 0)")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(stockQuantityField.getText()));
                ps.setString(3, noteField.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerClassField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField stockQuantityField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
        stockQuantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 3)));
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("类别ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("库存数量:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑植物信息", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Flower_Tbl SET flowerClassID = ?, stockQuantity = ?, note = ? WHERE id = ?")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(stockQuantityField.getText()));
                ps.setString(3, noteField.getText());
                ps.setInt(4, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "编辑成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的植物信息吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE Flower_Tbl SET del = 1 WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }
}

class FlowerStockPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public FlowerStockPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("植物库存管理", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for search field
        JButton searchBtn = new JButton("查询");
        JButton resetBtn = new JButton("重置");
        JButton addBtn = new JButton("新增");
        JButton editBtn = new JButton("编辑");
        JButton deleteBtn = new JButton("删除");

        // Set font size for buttons
        Font buttonFont = new Font("宋体", Font.BOLD, 18);
        searchBtn.setFont(buttonFont);
        resetBtn.setFont(buttonFont);
        addBtn.setFont(buttonFont);
        editBtn.setFont(buttonFont);
        deleteBtn.setFont(buttonFont);

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"库存ID", "花卉ID", "库存数量", "备注"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size for table cells
        table.setRowHeight(30); // Adjusted row height
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 20)); // Increased font size for table headers
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, flowerClassID, stockQuantity, note FROM Flower_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND note LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("flowerClassID"),
                        rs.getInt("stockQuantity"),
                        rs.getString("note")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerClassField = new JTextField();
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField stockQuantityField = new JTextField();
        stockQuantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField();
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("花卉ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("库存数量:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增库存记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Flower_Tbl (flowerClassID, stockQuantity, note, del) VALUES (?, ?, ?, 0)")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(stockQuantityField.getText()));
                ps.setString(3, noteField.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerClassField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField stockQuantityField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
        stockQuantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 3)));
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("花卉ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("库存数量:"));
        panel.add(stockQuantityField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑库存记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Flower_Tbl SET flowerClassID = ?, stockQuantity = ?, note = ? WHERE id = ?")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(stockQuantityField.getText()));
                ps.setString(3, noteField.getText());
                ps.setInt(4, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "编辑成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的库存记录吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE Flower_Tbl SET del = 1 WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }
}

class InboundRecordPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public InboundRecordPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("植物入库记录", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton resetBtn = new JButton("重置");
        resetBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton addBtn = new JButton("新增");
        addBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton editBtn = new JButton("编辑");
        editBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "花卉类别ID", "数量", "操作员ID", "入库时间", "状态", "备注"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        table.setRowHeight(30); // Adjusted row height for larger text
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 18)); // Increased header font size
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, flowerClassID, quantity, operatorID, inboundDate, status, note FROM FlowerInbound_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND note LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("flowerClassID"),
                        rs.getInt("quantity"),
                        rs.getInt("operatorID"),
                        rs.getTimestamp("inboundDate"),
                        rs.getInt("status") == 0 ? "待验收" : "已验收",
                        rs.getString("note")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField flowerClassField = new JTextField();
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField operatorField = new JTextField();
        operatorField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField();
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待验收", "已验收"});
        statusCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("花卉类别ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("数量:"));
        panel.add(quantityField);
        panel.add(new JLabel("操作员ID:"));
        panel.add(operatorField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);
        panel.add(new JLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增入库记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO FlowerInbound_Tbl (flowerClassID, quantity, operatorID, inboundDate, status, note, del) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, 0)")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(quantityField.getText()));
                ps.setInt(3, Integer.parseInt(operatorField.getText()));
                ps.setInt(4, statusCombo.getSelectedIndex());
                ps.setString(5, noteField.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField flowerClassField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
        flowerClassField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField quantityField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
        quantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField operatorField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 3)));
        operatorField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField noteField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 6)));
        noteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待验收", "已验收"});
        statusCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        statusCombo.setSelectedItem(model.getValueAt(selectedRow, 5));

        panel.add(new JLabel("花卉类别ID:"));
        panel.add(flowerClassField);
        panel.add(new JLabel("数量:"));
        panel.add(quantityField);
        panel.add(new JLabel("操作员ID:"));
        panel.add(operatorField);
        panel.add(new JLabel("备注:"));
        panel.add(noteField);
        panel.add(new JLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑入库记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE FlowerInbound_Tbl SET flowerClassID = ?, quantity = ?, operatorID = ?, status = ?, note = ? WHERE id = ?")) {

                ps.setInt(1, Integer.parseInt(flowerClassField.getText()));
                ps.setInt(2, Integer.parseInt(quantityField.getText()));
                ps.setInt(3, Integer.parseInt(operatorField.getText()));
                ps.setInt(4, statusCombo.getSelectedIndex());
                ps.setString(5, noteField.getText());
                ps.setInt(6, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "编辑成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的入库记录吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE FlowerInbound_Tbl SET del = 1 WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }
}
class AcceptInboundPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public AcceptInboundPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("待验收入库记录", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton resetBtn = new JButton("重置");
        resetBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton addBtn = new JButton("新增");
        addBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton editBtn = new JButton("编辑");
        editBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"入库ID", "花卉名称", "数量", "操作人", "入库时间"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        table.setRowHeight(30); // Adjusted row height for larger text
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 18)); // Increased header font size
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "{call GetPendingInbounds}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("flowerName"),
                        rs.getInt("quantity"),
                        rs.getString("operator"),
                        rs.getTimestamp("inboundDate")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerNameField = new JTextField();
        flowerNameField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField operatorField = new JTextField();
        operatorField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("花卉名称:"));
        panel.add(flowerNameField);
        panel.add(new JLabel("数量:"));
        panel.add(quantityField);
        panel.add(new JLabel("操作人:"));
        panel.add(operatorField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增待验收入库记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO FlowerInbound_Tbl (flowerName, quantity, operator, inboundDate, status, del) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0, 0)")) {

                ps.setString(1, flowerNameField.getText());
                ps.setInt(2, Integer.parseInt(quantityField.getText()));
                ps.setString(3, operatorField.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField flowerNameField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
        flowerNameField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField quantityField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
        quantityField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField operatorField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 3)));
        operatorField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("花卉名称:"));
        panel.add(flowerNameField);
        panel.add(new JLabel("数量:"));
        panel.add(quantityField);
        panel.add(new JLabel("操作人:"));
        panel.add(operatorField);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑待验收入库记录", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE FlowerInbound_Tbl SET flowerName = ?, quantity = ?, operator = ? WHERE id = ?")) {

                ps.setString(1, flowerNameField.getText());
                ps.setInt(2, Integer.parseInt(quantityField.getText()));
                ps.setString(3, operatorField.getText());
                ps.setInt(4, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "编辑成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的待验收入库记录吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE FlowerInbound_Tbl SET del = 1 WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }
}
class OrderPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public OrderPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("订单信息查看", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton resetBtn = new JButton("重置");
        resetBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton addBtn = new JButton("新增");
        addBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton editBtn = new JButton("编辑");
        editBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "客户ID", "下单时间", "配送日期", "金额", "备注", "状态"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        table.setRowHeight(30); // Adjusted row height for larger text
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 18)); // Increased header font size
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data
        loadData("");

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, customerID, orderDate, deliveryDate, totalPrice, orderNote, status FROM Order_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND orderNote LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("customerID"),
                        rs.getTimestamp("orderDate"),
                        rs.getTimestamp("deliveryDate"),
                        rs.getDouble("totalPrice"),
                        rs.getString("orderNote"),
                        rs.getInt("status") == 0 ? "未完成" : "已完成"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField customerField = new JTextField();
        customerField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField deliveryDateField = new JTextField();
        deliveryDateField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField totalPriceField = new JTextField();
        totalPriceField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField orderNoteField = new JTextField();
        orderNoteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"未完成", "已完成"});
        statusCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size

        panel.add(new JLabel("客户ID:"));
        panel.add(customerField);
        panel.add(new JLabel("配送日期:"));
        panel.add(deliveryDateField);
        panel.add(new JLabel("金额:"));
        panel.add(totalPriceField);
        panel.add(new JLabel("备注:"));
        panel.add(orderNoteField);
        panel.add(new JLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增订单", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Order_Tbl (customerID, deliveryDate, totalPrice, orderNote, status, del) VALUES (?, ?, ?, ?, ?, 0)")) {

                ps.setInt(1, Integer.parseInt(customerField.getText()));
                ps.setTimestamp(2, Timestamp.valueOf(deliveryDateField.getText()));
                ps.setDouble(3, Double.parseDouble(totalPriceField.getText()));
                ps.setString(4, orderNoteField.getText());
                ps.setInt(5, statusCombo.getSelectedIndex());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField customerField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 1)));
        customerField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField deliveryDateField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 3)));
        deliveryDateField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField totalPriceField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 4)));
        totalPriceField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JTextField orderNoteField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 5)));
        orderNoteField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"未完成", "已完成"});
        statusCombo.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        statusCombo.setSelectedItem(model.getValueAt(selectedRow, 6));

        panel.add(new JLabel("客户ID:"));
        panel.add(customerField);
        panel.add(new JLabel("配送日期:"));
        panel.add(deliveryDateField);
        panel.add(new JLabel("金额:"));
        panel.add(totalPriceField);
        panel.add(new JLabel("备注:"));
        panel.add(orderNoteField);
        panel.add(new JLabel("状态:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "编辑订单", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Order_Tbl SET customerID = ?, deliveryDate = ?, totalPrice = ?, orderNote = ?, status = ? WHERE id = ?")) {

                ps.setInt(1, Integer.parseInt(customerField.getText()));
                ps.setTimestamp(2, Timestamp.valueOf(deliveryDateField.getText()));
                ps.setDouble(3, Double.parseDouble(totalPriceField.getText()));
                ps.setString(4, orderNoteField.getText());
                ps.setInt(5, statusCombo.getSelectedIndex());
                ps.setInt(6, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "编辑成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
            }
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的行");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的订单吗?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE Order_Tbl SET del = 1 WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData(searchField.getText().trim());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
            }
        }
    }
}


    class DeliveryPanel extends JPanel {
    private JPanel deliveryListPanel;
    private JTextField searchField;

    public DeliveryPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("配送记录", SwingConstants.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 24)); // Increased font size
        add(title, BorderLayout.NORTH);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        searchField = new JTextField(20);
        searchField.setFont(new Font("宋体", Font.PLAIN, 18)); // Increased font size
        JButton searchBtn = new JButton("查询");
        searchBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton resetBtn = new JButton("重置");
        resetBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton addBtn = new JButton("新增");
        addBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton editBtn = new JButton("编辑");
        editBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setFont(new Font("宋体", Font.BOLD, 18)); // Increased font size

        toolBar.add(new JLabel("搜索:"));
        toolBar.add(searchField);
        toolBar.add(searchBtn);
        toolBar.add(resetBtn);
        toolBar.addSeparator();
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);

        add(toolBar, BorderLayout.NORTH);

        // Delivery list panel
        deliveryListPanel = new JPanel();
        deliveryListPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Two panels per row
        add(new JScrollPane(deliveryListPanel), BorderLayout.CENTER);

        // Button actions
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteData());

        // Load data
        loadData("");
    }

    private void loadData(String keyword) {
        deliveryListPanel.removeAll();
        String sql = "SELECT id, orderID, address, courierID, deliveryDate, status, note FROM Delivery_Tbl WHERE del = 0";
        if (!keyword.isEmpty()) {
            sql += " AND address LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            int imageIndex = 11; // Start with image 11.jpg
            while (rs.next()) {
                deliveryListPanel.add(createDeliveryPanel(rs, imageIndex));
                imageIndex = (imageIndex == 13) ? 11 : imageIndex + 1; // Cycle through 11, 12, 13
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据加载失败: " + e.getMessage());
        }

        deliveryListPanel.revalidate();
        deliveryListPanel.repaint();
    }

    private JPanel createDeliveryPanel(ResultSet rs, int imageIndex) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setPreferredSize(new Dimension(300, 200)); // Adjust size for each panel

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon("src/images/" + imageIndex + ".jpg"));
        imageLabel.setPreferredSize(new Dimension(300, 120));
        panel.add(imageLabel, BorderLayout.NORTH);

        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        detailsPanel.add(new JLabel("订单ID: " + rs.getInt("orderID")));
        detailsPanel.add(new JLabel("地址: " + rs.getString("address")));
        detailsPanel.add(new JLabel("派送人ID: " + rs.getInt("courierID")));
        detailsPanel.add(new JLabel("配送时间: " + rs.getTimestamp("deliveryDate")));
        detailsPanel.add(new JLabel("状态: " + (rs.getInt("status") == 0 ? "待配送" : "已完成")));
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

        private void showAddDialog() {
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField orderField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField courierField = new JTextField();
            JTextField deliveryDateField = new JTextField();
            JTextField noteField = new JTextField();
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待配送", "已完成"});

            panel.add(new JLabel("订单ID:"));
            panel.add(orderField);
            panel.add(new JLabel("地址:"));
            panel.add(addressField);
            panel.add(new JLabel("派送人ID:"));
            panel.add(courierField);
            panel.add(new JLabel("配送时间 (格式: yyyy-MM-dd HH:mm:ss):"));
            panel.add(deliveryDateField);
            panel.add(new JLabel("备注:"));
            panel.add(noteField);
            panel.add(new JLabel("状态:"));
            panel.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(this, panel, "新增配送记录", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validate inputs
                    if (orderField.getText().isEmpty() || addressField.getText().isEmpty() ||
                            courierField.getText().isEmpty() || deliveryDateField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "所有字段均为必填项！");
                        return;
                    }

                    int orderId = Integer.parseInt(orderField.getText());
                    int courierId = Integer.parseInt(courierField.getText());
                    Timestamp deliveryDate = Timestamp.valueOf(deliveryDateField.getText());

                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(
                                 "INSERT INTO Delivery_Tbl (orderID, address, courierID, deliveryDate, status, note, del) VALUES (?, ?, ?, ?, ?, ?, 0)")) {

                        ps.setInt(1, orderId);
                        ps.setString(2, addressField.getText());
                        ps.setInt(3, courierId);
                        ps.setTimestamp(4, deliveryDate);
                        ps.setInt(5, statusCombo.getSelectedIndex());
                        ps.setString(6, noteField.getText());

                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(this, "新增成功！");
                        loadData(searchField.getText().trim());
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "订单ID或派送人ID必须为数字！");
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "配送时间格式无效，请使用 yyyy-MM-dd HH:mm:ss！");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "新增失败: " + e.getMessage());
                }
            }
        }
    private void showEditDialog() {
        String input = JOptionPane.showInputDialog(this, "请输入要编辑的配送记录ID:");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入有效的配送记录ID！");
            return;
        }

        try {
            int id = Integer.parseInt(input);
            String sql = "SELECT orderID, address, courierID, deliveryDate, status, note FROM Delivery_Tbl WHERE id = ? AND del = 0";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "配送记录不存在！");
                        return;
                    }

                    JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
                    JTextField orderField = new JTextField(rs.getString("orderID"));
                    JTextField addressField = new JTextField(rs.getString("address"));
                    JTextField courierField = new JTextField(rs.getString("courierID"));
                    JTextField deliveryDateField = new JTextField(rs.getString("deliveryDate"));
                    JTextField noteField = new JTextField(rs.getString("note"));
                    JComboBox<String> statusCombo = new JComboBox<>(new String[]{"待配送", "已完成"});
                    statusCombo.setSelectedIndex(rs.getInt("status"));

                    panel.add(new JLabel("订单ID:"));
                    panel.add(orderField);
                    panel.add(new JLabel("地址:"));
                    panel.add(addressField);
                    panel.add(new JLabel("派送人ID:"));
                    panel.add(courierField);
                    panel.add(new JLabel("配送时间:"));
                    panel.add(deliveryDateField);
                    panel.add(new JLabel("备注:"));
                    panel.add(noteField);
                    panel.add(new JLabel("状态:"));
                    panel.add(statusCombo);

                    int result = JOptionPane.showConfirmDialog(this, panel, "编辑配送记录", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String updateSql = "UPDATE Delivery_Tbl SET orderID = ?, address = ?, courierID = ?, deliveryDate = ?, status = ?, note = ? WHERE id = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setString(1, orderField.getText());
                            updatePs.setString(2, addressField.getText());
                            updatePs.setString(3, courierField.getText());
                            updatePs.setTimestamp(4, Timestamp.valueOf(deliveryDateField.getText()));
                            updatePs.setInt(5, statusCombo.getSelectedIndex());
                            updatePs.setString(6, noteField.getText());
                            updatePs.setInt(7, id);

                            updatePs.executeUpdate();
                            JOptionPane.showMessageDialog(this, "编辑成功！");
                            loadData(searchField.getText().trim());
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字ID！");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "编辑失败: " + e.getMessage());
        }
    }

    private void deleteData() {
        String input = JOptionPane.showInputDialog(this, "请输入要删除的配送记录ID:");
        if (input == null || input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入有效的配送记录ID！");
            return;
        }

        try {
            int id = Integer.parseInt(input);
            int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的配送记录吗?", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE Delivery_Tbl SET del = 1 WHERE id = ?")) {

                    ps.setInt(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                    loadData(searchField.getText().trim());
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字ID！");
        }
    }
}