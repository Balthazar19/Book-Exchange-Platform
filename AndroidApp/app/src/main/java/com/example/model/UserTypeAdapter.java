package com.example.model;

import com.google.gson.*;
import java.lang.reflect.Type;

public class UserTypeAdapter implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Determine the type based on the presence of phoneNum
        if (jsonObject.has("phoneNum") && !jsonObject.get("phoneNum").isJsonNull()) {
            // This is an Admin, because phoneNum is present and not null
            return context.deserialize(json, Admin.class);
        } else {
            // This is a Client, phoneNum is either absent or null
            return context.deserialize(json, Client.class);
        }
    }
}