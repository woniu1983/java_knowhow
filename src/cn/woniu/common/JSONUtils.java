/**
 * 
 */
package cn.woniu.common;

import org.json.JSONObject;

/**
 * @author woniu
 *
 */
public class JSONUtils {

    public static String optString(JSONObject json, String key) {
        return optString(json, key, null);
    }

    public static String optString(JSONObject json, String key, String fallback) {
        if (json.isNull(key)) {
            return fallback;
        } else {
            return json.optString(key, fallback);
        }

    }
}
