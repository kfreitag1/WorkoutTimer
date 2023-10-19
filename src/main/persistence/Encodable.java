package persistence;

import org.json.JSONObject;

public interface Encodable {
    // EFFECTS: Returns this as an encoded object (JSON)
    JSONObject encoded();
}
