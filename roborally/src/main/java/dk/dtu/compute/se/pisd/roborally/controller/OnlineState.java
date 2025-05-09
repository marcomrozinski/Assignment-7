package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.User;

/**
 * Singleton holding the current authenticated user for the application.
 * <p>
 * Provides a global access point to the {@link User} who is signed in,
 * so that other controllers and views can retrieve the user context.
 * </p>
 */
public class OnlineState {

    /** The single instance of OnlineState. */
    private static final OnlineState instance = new OnlineState();

    /** The currently signed-in user, or null if no user is authenticated. */
    private User currentUser;

    /** Private constructor to prevent external instantiation. */
    private OnlineState() { }

    /**
     * Returns the singleton instance of OnlineState.
     *
     * @return the shared OnlineState instance
     */
    public static OnlineState getInstance() {
        return instance;
    }

    /**
     * Retrieves the user who is currently signed in.
     *
     * @return the current {@link User}, or null if none is signed in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the user as the currently authenticated user.
     *
     * @param user the {@link User} to mark as signed in
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
