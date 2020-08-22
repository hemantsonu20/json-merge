package com.github.hemantsonu20.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

public class JsonMerge {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * @param srcJsonStr    source json string
     * @param targetJsonStr target json string
     * @return merged json as a string
     */
    public static String merge(String srcJsonStr, String targetJsonStr) {

        try {
            JsonNode srcNode = OBJECT_MAPPER.readTree(srcJsonStr);
            JsonNode targetNode = OBJECT_MAPPER.readTree(targetJsonStr);
            JsonNode result = merge(srcNode, targetNode);
            return OBJECT_MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new JsonMergeException("Unable to merge json", e);
        }
    }

    public static JsonNode merge(JsonNode srcNode, JsonNode targetNode) {

        // if both nodes are object node, merged object node is returned
        if (srcNode.isObject() && targetNode.isObject()) {
            return merge((ObjectNode) srcNode, (ObjectNode) targetNode);
        }

        // if both nodes are array node, merged array node is returned
        if (srcNode.isArray() && targetNode.isArray()) {
            return merge((ArrayNode) srcNode, (ArrayNode) targetNode);
        }

        // special case when src node is null
        if (srcNode.isNull()) {
            return targetNode;
        }

        return srcNode;
    }

    public static ObjectNode merge(ObjectNode srcNode, ObjectNode targetNode) {

        ObjectNode result = OBJECT_MAPPER.createObjectNode();

        Iterator<Map.Entry<String, JsonNode>> srcItr = srcNode.fields();
        while (srcItr.hasNext()) {

            Map.Entry<String, JsonNode> entry = srcItr.next();

            // check key in src json exists in target json or not at same level
            if (targetNode.has(entry.getKey())) {
                result.set(entry.getKey(), merge(entry.getValue(), targetNode.get(entry.getKey())));
            } else {
                // if key in src json doesn't exist in target json, just copy the same in result
                result.set(entry.getKey(), entry.getValue());
            }
        }

        // copy fields from target json into result which were missing in src json
        Iterator<Map.Entry<String, JsonNode>> targetItr = targetNode.fields();
        while (targetItr.hasNext()) {
            Map.Entry<String, JsonNode> entry = targetItr.next();
            if (!result.has(entry.getKey())) {
                result.set(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static ArrayNode merge(ArrayNode srcNode, ArrayNode targetNode) {
        ArrayNode result = OBJECT_MAPPER.createArrayNode();
        return result.addAll(srcNode).addAll(targetNode);
    }
}
