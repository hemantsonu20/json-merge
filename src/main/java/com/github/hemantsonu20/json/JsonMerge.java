package com.github.hemantsonu20.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

/**
 * This class provides methods to merge two json of any nested level into a single json.
 */
public class JsonMerge {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Method to merge two json objects into single json object.
     *
     * <p>It merges two json of any nested level into a single json following below logic.</p>
     * <ul>
     *   <li>When keys are different, both keys with there values will be copied at same level.</li>
     *   <li>
     *     <p>When keys are same at some level, following table denotes what value will be used.</p>
     *     <table border="1" summary="">
     *       <thead>
     *         <tr>
     *           <th align="left">Src / Target</th>
     *           <th align="left">JSON Value</th>
     *           <th align="left">JSON Array</th>
     *           <th align="left">JSON Object</th>
     *         </tr>
     *       </thead>
     *       <tbody>
     *         <tr>
     *           <td align="left">JSON Value<sup>1</sup></td>
     *           <td align="left">Src</td>
     *           <td align="left">Src</td>
     *           <td align="left">Src</td>
     *         </tr>
     *         <tr>
     *           <td align="left">JSON Array</td>
     *           <td align="left">Src<sup>2</sup></td>
     *           <td align="left">Merge</td>
     *           <td align="left">Src</td>
     *         </tr>
     *         <tr>
     *           <td align="left">JSON Object</td>
     *           <td align="left">Src</td>
     *           <td align="left">Src</td>
     *           <td align="left">Merge<sup>3</sup></td>
     *         </tr>
     *       </tbody>
     *     </table>
     *     <ul>
     *       <li><sup><strong>1</strong></sup> Json Value denotes boolean, number or string value in json.</li>
     *       <li><sup><strong>2</strong></sup> Src denotes <code>Src</code> value will be copied.</li>
     *       <li><sup><strong>3</strong></sup> Merge denotes both <code>Src</code> and <code>Target</code> values will be merged.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <h2>Examples</h2>
     * <h3>Example 1</h3>
     * <p><strong>Source Json</strong></p>
     * <pre>{@code
     * {
     *   "name": "json-merge-src"
     * }
     * }</pre>
     * <p><strong>Target Json</strong></p>
     * <pre>{@code
     * {
     *   "name": "json-merge-target"
     * }
     * }</pre>
     * <p><strong>Output</strong></p>
     * <pre>{@code
     * {
     *   "name": "json-merge-src"
     * }
     * }</pre>
     * <h3>Example 2</h3>
     * <p><strong>Source Json</strong></p>
     * <pre>{@code
     * {
     *   "level1": {
     *     "key1": "SrcValue1"
     *   }
     * }
     * }</pre>
     * <p><strong>Target Json</strong></p>
     * <pre>{@code
     * {
     *   "level1": {
     *     "key1": "targetValue1",
     *     "level2": {
     *       "key2": "value2"
     *     }
     *   }
     * }
     * }</pre>
     * <p><strong>Output</strong></p>
     * <pre>{@code
     * {
     *   "level1": {
     *     "key1": "SrcValue1",
     *     "level2": {
     *       "key2": "value2"
     *     }
     *   }
     * }
     * }</pre>
     *
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
