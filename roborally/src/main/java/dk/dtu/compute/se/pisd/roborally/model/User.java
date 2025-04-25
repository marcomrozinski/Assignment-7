package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("uid")

    private Long uid;

    @SerializedName("name")

    private String name;

    @SerializedName("password")

    private String password;

    public User() {
        // Required for Gson
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
