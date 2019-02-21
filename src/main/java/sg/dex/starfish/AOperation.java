package sg.dex.starfish;

import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Abstract base class representing invokable operation sin th eOCean ecosystem
 * 
 * @author Mike
 *
 */
public abstract class AOperation extends AAsset implements Operation {

	protected AOperation(String meta) {
		super(meta);
	}

	@Override
	public boolean isDataAsset() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, JSONObject> getParamSpec() {
		JSONObject meta=getMetadata();
		return (Map<String, JSONObject>) meta.get("params");
	}
	
}
