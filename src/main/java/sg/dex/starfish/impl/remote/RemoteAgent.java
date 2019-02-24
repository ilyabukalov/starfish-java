package sg.dex.starfish.impl.remote;

import java.net.MalformedURLException;
import java.net.URL;

import sg.dex.starfish.AAgent;
import sg.dex.starfish.Asset;
import sg.dex.starfish.Ocean;
import sg.dex.starfish.util.DID;
import sg.dex.starfish.util.TODOException;

/**
 * Class implementing a remote storage agent using the Storage API
 * @author Mike
 *
 */
public class RemoteAgent extends AAgent {

	/**
	 * Creates a RemoteAgent with the specified Ocean connection and DID
	 * @param ocean Ocean connection to use
	 * @param did DID for this agent
	 */
	protected RemoteAgent(Ocean ocean, DID did) {
		super(ocean, did);
	}

	@Override
	public void registerAsset(Asset a) {
		throw new TODOException();
	}

	@Override
	public Asset getAsset(String id) {
		throw new TODOException();
	}

	@Override
	public Asset uploadAsset(Asset a) {
		throw new TODOException();
	}

	/**
	 * Gets a URL string for accessing the specified asset ID
	 * @param id The asset ID to address 
	 * @return The URL for the asset as a String
	 */
	public String getAssetURL(String id) {
		throw new TODOException();
	}

	public URL getURL(RemoteAsset remoteAsset) {
		String storageEndpoint=getStorageEndpoint();
		if (storageEndpoint==null) throw new IllegalStateException("No storage endpoint available for agent");
		try {
			return new URL(storageEndpoint+"/"+remoteAsset.getAssetID());
		}
		catch (MalformedURLException e) {
			throw new Error("Failed to get asset URL",e);
		}
	}

	/**
	 * Gets the storage endpoint for this agent
	 * @return The storage endpoint for this agent e.g. "https://www.myagent.com/api/v1/storage"
	 */
	public String getStorageEndpoint() {
		return getEndpoint("Ocean.Storage");
	}
	


}
