package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.*;
import java.lang.reflect.Type;

public class UserAdapter implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        User user = new User();
        user.setUid(jsonObject.get("uid").getAsLong());
        user.setName(jsonObject.get("name").getAsString());
        user.setPassword(jsonObject.get("password").getAsString());

        return user;
    }
}

