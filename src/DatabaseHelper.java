import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // Sesuaikan URL, USER, dan PASS dengan konfigurasi PostgreSQL lokal Anda
    private static final String URL = "jdbc:postgresql://localhost:5432/db_pico_dummy";
    private static final String USER = "postgres";
    private static final String PASS = "Fano1922"; // Isi password user postgres Anda

    // Method reusable untuk membuka koneksi (sesuai materi JDBC)
    private static Connection conn() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // CREATE: Menambah stand baru ke database
    public static void create(Stand stand) throws Exception {
        String sql = "INSERT INTO stand (no_kios, nama_stand, lokasi, status, nama_penjual, username_penjual) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stand.getNoKios());
            ps.setString(2, stand.getNamaStand());
            ps.setString(3, stand.getLokasi());
            ps.setString(4, stand.getStatus());
            ps.setString(5, stand.getNamaPenjual());
            ps.setString(6, stand.getUsernamePenjual());
            ps.executeUpdate();
        }
    }

    // READ: Mengambil seluruh data stand (mendukung filter pencarian opsional)
    public static List<Stand> getAll(String keyword) throws Exception {
        List<Stand> list = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        String sql = hasKeyword
                ? "SELECT id_stand, no_kios, nama_stand, lokasi, status, nama_penjual, username_penjual "
                        + "FROM stand WHERE nama_stand ILIKE ? OR CAST(no_kios AS TEXT) ILIKE ? "
                        + "OR nama_penjual ILIKE ? OR lokasi ILIKE ? ORDER BY no_kios ASC"
                : "SELECT id_stand, no_kios, nama_stand, lokasi, status, nama_penjual, username_penjual "
                        + "FROM stand ORDER BY no_kios ASC";

        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement(sql)) {
            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
                ps.setString(4, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Stand(
                            rs.getInt("id_stand"),
                            rs.getInt("no_kios"),
                            rs.getString("nama_stand"),
                            rs.getString("lokasi"),
                            rs.getString("status"),
                            rs.getString("nama_penjual"),
                            rs.getString("username_penjual")));
                }
            }
        }
        return list;
    }

    // UPDATE: Memperbarui data stand berdasarkan id_stand
    public static void update(Stand stand) throws Exception {
        String sql = "UPDATE stand SET no_kios = ?, nama_stand = ?, lokasi = ?, status = ?, "
                + "nama_penjual = ?, username_penjual = ? WHERE id_stand = ?";
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, stand.getNoKios());
            ps.setString(2, stand.getNamaStand());
            ps.setString(3, stand.getLokasi());
            ps.setString(4, stand.getStatus());
            ps.setString(5, stand.getNamaPenjual());
            ps.setString(6, stand.getUsernamePenjual());
            ps.setInt(7, stand.getIdStand());
            ps.executeUpdate();
        }
    }

    // DELETE: Menghapus data stand berdasarkan id_stand
    public static void delete(int idStand) throws Exception {
        String sql = "DELETE FROM stand WHERE id_stand = ?";
        try (Connection c = conn();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idStand);
            ps.executeUpdate();
        }
    }
}
