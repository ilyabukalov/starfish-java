package sg.dex.starfish.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sg.dex.starfish.exception.StarfishValidationException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling JSON objects
 * <p>
 * This class include method to covert json string to map , parse the json
 * string.
 * </p>
 *
 * @author Mike
 * @version 0.5
 */
public class JSON {

    private static String WHITESPACE = "                                                             ";
    private static int WHITESPACE_LENGTH = WHITESPACE.length();

    /**
     * Converts an object to an efficient JSON string representation
     *
     * @param value Object to represent as a JSON String
     * @return JSON string representing the value
     * @throws RuntimeException on failure to create JSON from value
     */
    public static String toString(Object value) {
        ObjectMapper Obj = new ObjectMapper();

        try {

            return Obj.writeValueAsString(value);

        } catch (IOException e) {
            throw new StarfishValidationException("Converts an object" + value.toString() + " to an efficient JSON string failed", e);
        }
    }

    /**
     * Converts an object to a pretty-printed JSON string representation suitable
     * for human consumption
     *
     * @param value Object to represent as a JSON String
     * @return JSON string representing the value
     */
    public static String toPrettyString(Object value) {
        StringBuilder sb = new StringBuilder();
        sb = appendPrettyString(sb, value, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static StringBuilder appendPrettyString(StringBuilder sb, Object o, int indent) {
        if (o instanceof Map) {
            int entryIndent = indent + 2;
            sb.append("{\n");
            Map<String, Object> m = ((Map<String, Object>) o);
            int size = m.size();
            int pos = 0;
            for (Map.Entry<String, Object> me : m.entrySet()) {
                String k = me.getKey();
                sb = appendWhitespaceString(sb, entryIndent);
                sb.append(toString(k));
                sb.append(": ");
                int vIndent = entryIndent + k.length() + 4; // indent for value
                Object v = me.getValue();
                appendPrettyString(sb, v, vIndent);
                pos++;
                if (pos == size) {
                    sb.append('\n'); // final entry
                } else {
                    sb.append(",\n"); // comma for next entry
                }
            }
            sb = appendWhitespaceString(sb, indent);
            sb.append("}");
        } else if (o instanceof List) {
            List<Object> list = (List<Object>) o;
            int size = list.size();
            int entryIndent = indent + 1;
            sb.append("[");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sb.append(",\n");
                    sb = appendWhitespaceString(sb, entryIndent);
                }
                Object v = list.get(i);
                sb = appendPrettyString(sb, v, entryIndent);
            }
            sb.append("]");
        } else {
            sb.append(toString(o));
        }
        return sb;
    }

    /**
     * Appends a whitespace string of the specified length.
     *
     * @param sb    StringBuilder to append the whitespace characters
     * @param count Number of whitespace characters
     * @return Updated StringBuilder
     */
    private static StringBuilder appendWhitespaceString(StringBuilder sb, int count) {
        while (count > WHITESPACE_LENGTH) {
            sb.append(WHITESPACE);
            count -= WHITESPACE_LENGTH;
        }
        sb.append(WHITESPACE, 0, count);
        return sb;
    }

    /**
     * Converts a string assumed to contain a valid JSON object to a (possibly
     * nested) Map. Use in preference to parse(...) if you know the string should
     * contain a map/object.
     *
     * @param jsonString A string containing a valid JSON object
     * @return A map representing the JSON object
     * @throws Exception on JSON parsing error
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject result = (JSONObject) parser.parse(jsonString);
            return new JSONObject(result);
        } catch (ParseException e) {
            throw new StarfishValidationException("Exception in JSON parsing: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a string assumed to contain valid JSON value to an Object
     *
     * @param jsonString A string containing valid JSON
     * @param <T>        A type parameter for the type of object returned.
     * @return T A java object representing the JSON provided
     * @throws Error on JSON parsing error
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T parse(String jsonString) {
        JSONParser parser = new JSONParser();
        try {
            Object result = parser.parse(jsonString);
            return (T) result;
        } catch (ParseException e) {
            throw Utils.sneakyThrow(e);
        }
    }
}
