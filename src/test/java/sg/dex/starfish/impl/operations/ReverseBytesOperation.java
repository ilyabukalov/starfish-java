package sg.dex.starfish.impl.operations;

import sg.dex.starfish.Asset;
import sg.dex.starfish.exception.TODOException;
import sg.dex.starfish.impl.memory.AMemoryOperation;
import sg.dex.starfish.impl.memory.MemoryAgent;
import sg.dex.starfish.impl.memory.MemoryAsset;

import java.util.Map;

/**
 * Basic implementation of an operation which reverses the byte sequence of a
 * data asset
 *
 * @author Mike
 *
 */
public class ReverseBytesOperation extends AMemoryOperation {

	protected ReverseBytesOperation(String meta, MemoryAgent memoryAgent) {
		super(meta,memoryAgent);
	}

	/**
	 * Returns the AssetID for this ReverseBytesOperation
	 *
	 * @return AssetID for this ReverseBytesOperation
	 */
	@Override
	public String getAssetID() {
		return "reverse-bytes";
	}

	/**
	 * Creates a new instance of ReverseBytesOperation
	 *
	 * @return new instance of ReverseBytesOperation
	 */
	public static ReverseBytesOperation create(String meta) {


		return new ReverseBytesOperation(meta,MemoryAgent.create());
	}
	
	private Asset doCompute(Asset input) {
		byte[] bytes = input.getContent();
		int length = bytes.length;
		for (int i = 0; i < (length / 2); i++) {
			byte temp = bytes[i];
			bytes[i] = bytes[length - i - 1];
			bytes[length - i - 1] = temp;
		}
		Asset result = MemoryAsset.create(bytes);
		return result;
	}

	@Override
	protected Asset compute(Map<String, Asset> params) {
		Asset input=params.get("input");
		if (input==null) throw new IllegalArgumentException("Missing parameter 'input'");
		return doCompute(input);
	}

	@Override
	public Map<String, Object> syncCallToReverse(Map<String, Object> params) {
		return memoryAgent.syncCallToReverse(this,params);
	}

	@Override
	public Map<String,Object> invokeResult(Map<String, Object> params) {
		throw  new TODOException("Need to implement");

	}

}
