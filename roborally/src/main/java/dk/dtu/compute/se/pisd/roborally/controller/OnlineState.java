package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.User;

public class OnlineState {
    private static final OnlineState instance = new OnlineState();

    private User currentUser;

    private OnlineState() {}

    public static OnlineState getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
