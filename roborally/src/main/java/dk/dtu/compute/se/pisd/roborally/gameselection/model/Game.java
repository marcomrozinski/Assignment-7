package dk.dtu.compute.se.pisd.roborally.gameselection.model;

import uk.co.blackpepper.bowman.annotation.LinkedResource;
import uk.co.blackpepper.bowman.annotation.RemoteResource;
import uk.co.blackpepper.bowman.annotation.ResourceId;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RemoteResource("/game")
public class Game {


    private URI id;

    private long uid;

    private String name;

    private int minPlayers;

    private int maxPlayers;

    // ...

    private List<Player> players = new ArrayList<>();


    public Game() {};

    public Game(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @ResourceId
    public URI getId() {
        return id;
    }


    public void setId(URI id) {
        this.id = id;
    }

    public long getUid() {
        return this.uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @LinkedResource(optionalLink = true)
    public List<Player> getPlayers() {
        return players;
    }

    public  void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String toString() {
        // IMPORTANT: Don't try to add more information here,
        //            since this will invoke a REST request!
        return "Game{ id=" + getId() + " }";
    }
    private User owner;


    @LinkedResource
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
