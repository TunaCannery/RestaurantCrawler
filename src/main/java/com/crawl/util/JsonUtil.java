package com.crawl.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Deserialize json string to T
     *
     * @param json the json String
     * @param t    deserialized Class
     * @param <T>  deserialized Class
     * @return deserialized Object
     */

    public static <T> T toObject(String json, TypeReference<T> t) {
        try {
            return objectMapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Deserialize json string to JsonNode
     *
     * @param json the json String
     * @return the deserialized JsonNode
     */
    public static JsonNode toJsonNode(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Serialize object to json String
     *
     * @param o the object you want to serialize
     * @return serialized json string
     */
    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
