package com.intersisi.absensi.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JadwalHariIni {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("bagian_id")
    @Expose
    private String bagianId;
    @SerializedName("jam_kerja_id")
    @Expose
    private String jamKerjaId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("tgl")
    @Expose
    private String tgl;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("inisial")
    @Expose
    private String inisial;
    @SerializedName("mulai_masuk")
    @Expose
    private String mulaiMasuk;
    @SerializedName("masuk")
    @Expose
    private String masuk;
    @SerializedName("sampai_masuk")
    @Expose
    private String sampaiMasuk;
    @SerializedName("mulai_pulang")
    @Expose
    private String mulaiPulang;
    @SerializedName("pulang")
    @Expose
    private String pulang;
    @SerializedName("sampai_pulang")
    @Expose
    private String sampaiPulang;
    @SerializedName("kelompok_jam_id")
    @Expose
    private String kelompokJamId;
    @SerializedName("jumlah")
    @Expose
    private Integer jumlah;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getBagianId() {
        return bagianId;
    }

    public void setBagianId(String bagianId) {
        this.bagianId = bagianId;
    }

    public String getJamKerjaId() {
        return jamKerjaId;
    }

    public void setJamKerjaId(String jamKerjaId) {
        this.jamKerjaId = jamKerjaId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getInisial() {
        return inisial;
    }

    public void setInisial(String inisial) {
        this.inisial = inisial;
    }

    public String getMulaiMasuk() {
        return mulaiMasuk;
    }

    public void setMulaiMasuk(String mulaiMasuk) {
        this.mulaiMasuk = mulaiMasuk;
    }

    public String getMasuk() {
        return masuk;
    }

    public void setMasuk(String masuk) {
        this.masuk = masuk;
    }

    public String getSampaiMasuk() {
        return sampaiMasuk;
    }

    public void setSampaiMasuk(String sampaiMasuk) {
        this.sampaiMasuk = sampaiMasuk;
    }

    public String getMulaiPulang() {
        return mulaiPulang;
    }

    public void setMulaiPulang(String mulaiPulang) {
        this.mulaiPulang = mulaiPulang;
    }

    public String getPulang() {
        return pulang;
    }

    public void setPulang(String pulang) {
        this.pulang = pulang;
    }

    public String getSampaiPulang() {
        return sampaiPulang;
    }

    public void setSampaiPulang(String sampaiPulang) {
        this.sampaiPulang = sampaiPulang;
    }

    public String getKelompokJamId() {
        return kelompokJamId;
    }

    public void setKelompokJamId(String kelompokJamId) {
        this.kelompokJamId = kelompokJamId;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }

}
