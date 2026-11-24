public class Stand {
    private int idStand;
    private int noKios;
    private String namaStand;
    private String lokasi;
    private String status;
    private String namaPenjual;
    private String usernamePenjual;

    public Stand(int idStand, int noKios, String namaStand, String lokasi,
            String status, String namaPenjual, String usernamePenjual) {
        this.idStand = idStand;
        this.noKios = noKios;
        this.namaStand = namaStand;
        this.lokasi = lokasi;
        this.status = status;
        this.namaPenjual = namaPenjual;
        this.usernamePenjual = usernamePenjual;
    }

    public Stand(int noKios, String namaStand, String lokasi, String status, String namaPenjual,
            String usernamePenjual) {
        this(0, noKios, namaStand, lokasi, status, namaPenjual, usernamePenjual);
    }

    public int getIdStand() {
        return idStand;
    }

    public int getNoKios() {
        return noKios;
    }

    public String getNamaStand() {
        return namaStand;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getStatus() {
        return status;
    }

    public String getNamaPenjual() {
        return namaPenjual;
    }

    public String getUsernamePenjual() {
        return usernamePenjual;
    }
}
