package com.intersisi.absensi.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("api_token")
    @Expose
    private String apiToken;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("bagian_id")
    @Expose
    private String bagianId;
    @SerializedName("nama_bagian")
    @Expose
    private String namaBagian;
    @SerializedName("inisial")
    @Expose
    private String inisial;
    @SerializedName("kelompok_jam_id")
    @Expose
    private String kelompokJamId;
    @SerializedName("shift")
    @Expose
    private String shift;

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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBagianId() {
        return bagianId;
    }

    public void setBagianId(String bagianId) {
        this.bagianId = bagianId;
    }

    public String getNamaBagian() {
        return namaBagian;
    }

    public void setNamaBagian(String namaBagian) {
        this.namaBagian = namaBagian;
    }

    public String getInisial() {
        return inisial;
    }

    public void setInisial(String inisial) {
        this.inisial = inisial;
    }

    public String getKelompokJamId() {
        return kelompokJamId;
    }

    public void setKelompokJamId(String kelompokJamId) {
        this.kelompokJamId = kelompokJamId;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
