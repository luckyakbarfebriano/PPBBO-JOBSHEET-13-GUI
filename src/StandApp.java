import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StandApp extends JFrame {
    // Palet warna sesuai tampilan PICO Panel Admin
    private static final Color SIDEBAR_BG = new Color(30, 43, 69);
    private static final Color CONTENT_BG = new Color(245, 247, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ORANGE_ACCENT = new Color(242, 133, 24);
    private static final Color BLUE_ACTION = new Color(37, 99, 235);
    private static final Color RED_ACTION = new Color(220, 38, 38);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

    // Komponen Form
    private JTextField txtNamaStand;
    private JTextField txtNoKios;
    private JTextField txtLokasi;
    private JTextField txtNamaPenjual;
    private JTextField txtUsernamePenjual;
    private JComboBox<String> cmbStatus;
    private JTextField txtCari;

    // Tombol CRUD
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnReset;
    private JButton btnEdit;
    private JButton btnView;
    private JButton btnRefresh;

    // Label Statistik
    private JLabel lblTotalStand;
    private JLabel lblTotalBuka;
    private JLabel lblTotalPenjual;

    // Tabel
    private DefaultTableModel model;
    private JTable tabel;

    // Menyimpan ID stand yang sedang diedit
    private int selectedStandId = -1;

    public StandApp() {
        setTitle("PICO - Panel Admin | Stand & Penjual");
        setSize(1080, 680);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        initListeners();
        loadData("");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 16, 20, 16));

        JLabel lblBrand = new JLabel("PICO");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubBrand = new JLabel("Panel Admin");
        lblSubBrand.setForeground(new Color(148, 163, 184));
        lblSubBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblBrand);
        sidebar.add(lblSubBrand);
        sidebar.add(Box.createVerticalStrut(28));

        String[] menus = {"Dashboard", "Pengguna", "Stand", "Kategori", "Menu", "Pesanan", "Laporan"};
        for (String menu : menus) {
            boolean isActive = menu.equals("Stand");
            JLabel item = new JLabel("  " + menu);
            item.setOpaque(true);
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
            if (isActive) {
                item.setBackground(ORANGE_ACCENT);
                item.setForeground(Color.WHITE);
            } else {
                item.setBackground(SIDEBAR_BG);
                item.setForeground(new Color(203, 213, 225));
            }
            item.setBorder(new EmptyBorder(8, 10, 8, 10));
            sidebar.add(item);
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnKeluar = new JButton("Keluar");
        btnKeluar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKeluar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnKeluar.setBackground(new Color(45, 62, 95));
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setFocusPainted(false);
        btnKeluar.setBorderPainted(false);
        btnKeluar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKeluar.addActionListener(e -> dispose());
        sidebar.add(btnKeluar);

        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setBackground(CONTENT_BG);
        main.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Bagian Atas: Header + Statistik + Pencarian
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Stand & Penjual");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("Atur stand kantin, akun penjual, dan status buka tutupnya.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(TEXT_MUTED);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(lblTitle);
        topSection.add(Box.createVerticalStrut(4));
        topSection.add(lblDesc);
        topSection.add(Box.createVerticalStrut(12));

        // Bar Statistik + Pencarian
        JPanel statsAndSearch = new JPanel(new BorderLayout(12, 0));
        statsAndSearch.setOpaque(false);
        statsAndSearch.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsAndSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgePanel.setOpaque(false);
        lblTotalStand = createBadge("0 stand", Color.WHITE, TEXT_DARK);
        lblTotalBuka = createBadge("0 buka", new Color(220, 252, 231), new Color(22, 101, 52));
        lblTotalPenjual = createBadge("0 sudah ada penjual", Color.WHITE, TEXT_DARK);
        badgePanel.add(lblTotalStand);
        badgePanel.add(lblTotalBuka);
        badgePanel.add(lblTotalPenjual);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);
        txtCari = new JTextField(18);
        txtCari.setPreferredSize(new Dimension(190, 32));
        txtCari.setToolTipText("Cari nama stand, no kios, lokasi, atau penjual");
        JButton btnCari = createStyledButton("Cari", SIDEBAR_BG, Color.WHITE);
        btnCari.setPreferredSize(new Dimension(70, 32));
        btnCari.addActionListener(e -> loadData(txtCari.getText()));
        txtCari.addActionListener(e -> loadData(txtCari.getText()));

        searchPanel.add(new JLabel("Cari stand:"));
        searchPanel.add(txtCari);
        searchPanel.add(btnCari);

        statsAndSearch.add(badgePanel, BorderLayout.WEST);
        statsAndSearch.add(searchPanel, BorderLayout.EAST);
        topSection.add(statsAndSearch);

        main.add(topSection, BorderLayout.NORTH);

        // Bagian Tengah: Card Form + Tabel
        JPanel centerSection = new JPanel(new BorderLayout(0, 14));
        centerSection.setOpaque(false);
        centerSection.add(buildFormCard(), BorderLayout.NORTH);
        centerSection.add(buildTableCard(), BorderLayout.CENTER);

        main.add(centerSection, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblCardTitle = new JLabel("+ Tambah / Kelola Stand Baru");
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCardTitle.setForeground(ORANGE_ACCENT);
        card.add(lblCardTitle, BorderLayout.NORTH);

        // Grid input 2 baris x 3 kolom
        JPanel fieldsGrid = new JPanel(new GridLayout(2, 3, 14, 10));
        fieldsGrid.setOpaque(false);

        txtNamaStand = new JTextField();
        txtNoKios = new JTextField();
        txtLokasi = new JTextField();
        txtNamaPenjual = new JTextField();
        txtUsernamePenjual = new JTextField();
        cmbStatus = new JComboBox<>(new String[]{"Aktif", "Tutup"});

        fieldsGrid.add(wrapField("Nama Stand *", txtNamaStand));
        fieldsGrid.add(wrapField("No. Kios * (angka)", txtNoKios));
        fieldsGrid.add(wrapField("Lokasi (opsional)", txtLokasi));
        fieldsGrid.add(wrapField("Nama Penjual (opsional)", txtNamaPenjual));
        fieldsGrid.add(wrapField("Username Penjual (cth: @bu_ima)", txtUsernamePenjual));
        fieldsGrid.add(wrapField("Status", cmbStatus));

        card.add(fieldsGrid, BorderLayout.CENTER);

        // Bar Tombol CRUD
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);

        btnCreate = createStyledButton("Simpan Stand", ORANGE_ACCENT, Color.WHITE);
        btnUpdate = createStyledButton("Update", BLUE_ACTION, Color.WHITE);
        btnDelete = createStyledButton("Delete", RED_ACTION, Color.WHITE);
        btnReset = createStyledButton("Reset Form", new Color(226, 232, 240), TEXT_DARK);

        // Sesuai materi: tombol Update dan Delete awalnya nonaktif sebelum Edit diklik
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        btnBar.add(btnCreate);
        btnBar.add(btnUpdate);
        btnBar.add(btnDelete);
        btnBar.add(btnReset);

        card.add(btnBar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Daftar Stand Kantin");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(TEXT_DARK);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableActions.setOpaque(false);

        btnEdit = createStyledButton("Edit", ORANGE_ACCENT, Color.WHITE);
        btnView = createStyledButton("View", SIDEBAR_BG, Color.WHITE);
        btnRefresh = createStyledButton("Refresh", new Color(226, 232, 240), TEXT_DARK);

        tableActions.add(btnEdit);
        tableActions.add(btnView);
        tableActions.add(btnRefresh);

        headerBar.add(lblTableTitle, BorderLayout.WEST);
        headerBar.add(tableActions, BorderLayout.EAST);
        card.add(headerBar, BorderLayout.NORTH);

        // DefaultTableModel non-editable (sesuai materi Slide 13)
        String[] kolom = {"ID", "Kios", "Stand", "Lokasi", "Status", "Penjual", "Username"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        tabel.setRowHeight(30);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabel.getTableHeader().setBackground(new Color(248, 250, 252));
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setGridColor(BORDER_LIGHT);

        // Atur lebar kolom agar rapi
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(55);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Warna teks kolom Status (Aktif hijau, Tutup merah)
        tabel.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String status = String.valueOf(value);
                    c.setForeground("Aktif".equalsIgnoreCase(status)
                            ? new Color(22, 163, 74)
                            : new Color(220, 38, 38));
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel wrapField(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        field.setPreferredSize(new Dimension(160, 30));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill;
                if (!isEnabled()) {
                    fill = new Color(226, 232, 240);
                } else if (getModel().isPressed()) {
                    fill = bg.darker();
                } else if (getModel().isRollover()) {
                    fill = new Color(
                            Math.max(0, bg.getRed() - 18),
                            Math.max(0, bg.getGreen() - 18),
                            Math.max(0, bg.getBlue() - 18)
                    );
                } else {
                    fill = bg;
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                setForeground(b ? fg : new Color(148, 163, 184));
            }
        };
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setRolloverEnabled(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(115, 32));
        return btn;
    }

    private void initListeners() {
        // EVENT CREATE (Simpan Stand)
        btnCreate.addActionListener(e -> {
            Stand stand = readAndValidateForm(0);
            if (stand == null) return;

            try {
                DatabaseHelper.create(stand);
                JOptionPane.showMessageDialog(this, "Stand baru berhasil ditambahkan!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                loadData(txtCari.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan stand:\n" + ex.getMessage(),
                        "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENT EDIT (Binding data baris terpilih ke form input, sesuai Slide 11 & 15)
        btnEdit.addActionListener(e -> {
            int row = tabel.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data stand di tabel terlebih dahulu!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedStandId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtNoKios.setText(model.getValueAt(row, 1).toString());
            txtNamaStand.setText(model.getValueAt(row, 2).toString());

            String lokasi = model.getValueAt(row, 3).toString();
            txtLokasi.setText(lokasi.equals("-") ? "" : lokasi);

            cmbStatus.setSelectedItem(model.getValueAt(row, 4).toString());

            String penjual = model.getValueAt(row, 5).toString();
            txtNamaPenjual.setText(penjual.equals("-") ? "" : penjual);

            String username = model.getValueAt(row, 6).toString();
            txtUsernamePenjual.setText(username.equals("-") ? "" : username);

            btnCreate.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });

        // EVENT UPDATE (sesuai Slide 16 + simpan ke database)
        btnUpdate.addActionListener(e -> {
            if (selectedStandId == -1) return;
            Stand stand = readAndValidateForm(selectedStandId);
            if (stand == null) return;

            try {
                DatabaseHelper.update(stand);
                JOptionPane.showMessageDialog(this, "Data stand berhasil diperbarui!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                loadData(txtCari.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui stand:\n" + ex.getMessage(),
                        "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENT DELETE
        btnDelete.addActionListener(e -> {
            if (selectedStandId == -1) return;
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Yakin ingin menghapus stand \"" + txtNamaStand.getText().trim() + "\"?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                DatabaseHelper.delete(selectedStandId);
                JOptionPane.showMessageDialog(this, "Data stand berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                loadData(txtCari.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus stand:\n" + ex.getMessage(),
                        "Error Database", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENT VIEW (Menampilkan detail baris yang diklik, sesuai Slide 14)
        btnView.addActionListener(e -> {
            int row = tabel.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data stand di tabel terlebih dahulu!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String detail = "ID Stand   : " + model.getValueAt(row, 0)
                    + "\nNo. Kios   : " + model.getValueAt(row, 1)
                    + "\nNama Stand : " + model.getValueAt(row, 2)
                    + "\nLokasi     : " + model.getValueAt(row, 3)
                    + "\nStatus     : " + model.getValueAt(row, 4)
                    + "\nPenjual    : " + model.getValueAt(row, 5)
                    + "\nUsername   : " + model.getValueAt(row, 6);

            JOptionPane.showMessageDialog(this, detail, "Detail Stand", JOptionPane.INFORMATION_MESSAGE);
        });

        // EVENT RESET & REFRESH
        btnReset.addActionListener(e -> resetForm());
        btnRefresh.addActionListener(e -> {
            txtCari.setText("");
            loadData("");
        });
    }

    // Validator input form (mengikuti pola validator sederhana di Slide 12)
    private Stand readAndValidateForm(int idStand) {
        String namaStand = txtNamaStand.getText().trim();
        String noKiosStr = txtNoKios.getText().trim();
        String lokasi = txtLokasi.getText().trim();
        String namaPenjual = txtNamaPenjual.getText().trim();
        String usernamePenjual = txtUsernamePenjual.getText().trim();
        String status = String.valueOf(cmbStatus.getSelectedItem());

        if (namaStand.isEmpty() || noKiosStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Stand dan No. Kios harus diisi!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int noKios;
        try {
            noKios = Integer.parseInt(noKiosStr);
            if (noKios <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "No. Kios harus berupa angka positif!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (lokasi.isEmpty()) lokasi = "-";
        if (namaPenjual.isEmpty()) namaPenjual = "-";
        if (usernamePenjual.isEmpty()) {
            usernamePenjual = "-";
        } else if (!usernamePenjual.startsWith("@")) {
            usernamePenjual = "@" + usernamePenjual;
        }

        return new Stand(idStand, noKios, namaStand, lokasi, status, namaPenjual, usernamePenjual);
    }

    private void resetForm() {
        selectedStandId = -1;
        txtNamaStand.setText("");
        txtNoKios.setText("");
        txtLokasi.setText("");
        txtNamaPenjual.setText("");
        txtUsernamePenjual.setText("");
        cmbStatus.setSelectedIndex(0);
        tabel.clearSelection();

        btnCreate.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        try {
            List<Stand> list = DatabaseHelper.getAll(keyword);
            int totalBuka = 0;
            int totalPenjual = 0;

            for (Stand s : list) {
                model.addRow(new Object[]{
                        s.getIdStand(),
                        s.getNoKios(),
                        s.getNamaStand(),
                        s.getLokasi(),
                        s.getStatus(),
                        s.getNamaPenjual(),
                        s.getUsernamePenjual()
                });
                if ("Aktif".equalsIgnoreCase(s.getStatus())) totalBuka++;
                if (s.getNamaPenjual() != null && !s.getNamaPenjual().equals("-") && !s.getNamaPenjual().isBlank()) {
                    totalPenjual++;
                }
            }

            lblTotalStand.setText(list.size() + " stand");
            lblTotalBuka.setText(totalBuka + " buka");
            lblTotalPenjual.setText(totalPenjual + " sudah ada penjual");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tidak dapat terhubung ke database PostgreSQL (db_pico_dummy).\n"
                    + "Pastikan PostgreSQL aktif, file db_pico_dummy.sql sudah dijalankan,\n"
                    + "dan USER/PASS di DatabaseHelper.java sudah sesuai.\n\nDetail: " + ex.getMessage(),
                    "Koneksi Database Gagal",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new StandApp().setVisible(true));
    }
}
