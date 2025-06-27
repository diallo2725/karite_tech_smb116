package com.melitech.karitetech.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ApiResponseDeserializer<T> implements JsonDeserializer<ApiResponse<T>> {
    private final Type type;

    public ApiResponseDeserializer(Type type) {
        this.type = type;
    }

    @Override
    public ApiResponse<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(jsonObject.get("status").getAsBoolean());
        response.setMessage(jsonObject.has("message") ? jsonObject.get("message").getAsString() : "");

        JsonElement dataElement = jsonObject.get("data");
        T data = context.deserialize(dataElement, type);
        response.setData(data);

        return response;
    }
}
