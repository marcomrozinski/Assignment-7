package dk.dtu.compute.se.pisd.roborally.gameselection.model;

import uk.co.blackpepper.bowman.annotation.LinkedResource;
import uk.co.blackpepper.bowman.annotation.RemoteResource;
import uk.co.blackpepper.bowman.annotation.ResourceId;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RemoteResource("/user")
public class User {


    private URI id;

    private long uid;


    private String name;

    // ...

    public List<Player> players = new ArrayList<>();

    public User() {};

    public User(String name) {
        this.name = name;
    }


    @ResourceId
    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
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

    @LinkedResource
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        // IMPORTANT: Don't try to add more information here,
        //            since this will invoke a REST request!
        return "User{ id=" + getId() + " }";
    }

}
