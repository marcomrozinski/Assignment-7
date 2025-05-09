package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a user in the RoboRally application.
 * <p>
 * Instances of this class are serialized/deserialized with Gson when
 * communicating user data (signup, signin) with the backend API.
 * </p>
 */
public class User {

    /**
     * The unique identifier of the user.
     */
    @SerializedName("uid")
    private Long uid;

    /**
     * The username chosen by the user.
     */
    @SerializedName("name")
    private String name;

    /**
     * The password of the user.
     */
    @SerializedName("password")
    private String password;

    /**
     * Default constructor required for Gson deserialization.
     */
    public User() {
        // Required for Gson
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user’s ID
     */
    public Long getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier for this user.
     *
     * @param uid the ID to assign to the user
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * Returns the name of this user.
     *
     * @return the user’s name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this user.
     *
     * @param name the name to assign to the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the password of this user.
     *
     * @return the user’s password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this user.
     *
     * @param password the password to assign to the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of this user, including
     * UID, name, and password.
     *
     * @return a string describing this user
     */
    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
