package sg.dex.starfish.impl.remote;

import sg.dex.starfish.Listing;
import sg.dex.starfish.Purchase;

import java.util.HashMap;
import java.util.Map;

import static sg.dex.starfish.constant.Constant.LISTING_ID;
import static sg.dex.starfish.constant.Constant.STATUS;

/**
 * This class representing an purchase managed via a remote agent.
 * <p>
 * This class included methods for create , get listing,
 * get status and metadata of the purchase
 * The instance of this class will be present in Ocean ecosystem and be referred by using the purchase ID.
 *
 * @author Ayush
 * @version 0.5
 */
public class RemotePurchase implements Purchase {


    // local map to cache the listing data
    private static Map<String, Object> metaDataCache = null;
    // remote agent reference
    private RemoteAgent remoteAgent;
    // listing id
    private String purchase_id;


    /**
     * To get the reference of existing listing user need to pass the remote Agent
     * and the existing listing id.
     *
     * @param remoteAgent remote Agent
     * @param id          id
     */
    private RemotePurchase(RemoteAgent remoteAgent, String id) {
        this.remoteAgent = remoteAgent;
        this.purchase_id = id;
    }


    /**
     * To get the Reference of Existing Listing
     *
     * @param agent agent on which the purchase instance need to be created
     * @param id    id
     * @return RemotePurchase instance
     */
    public static RemotePurchase create(RemoteAgent agent, String id) {
        RemotePurchase remotePurchase = new RemotePurchase(agent, id);
        initializeCache();
        return remotePurchase;
    }

    /**
     * This method to create the local cache when the Purchase instance is create
     */
    private static void initializeCache() {
        if (null == metaDataCache) {
            metaDataCache = new HashMap<>();
        }

    }

    @Override
    public Listing getListing() {
        // get the Listing from Listing id
        return remoteAgent.getListing(this.getListingId());
    }

    @Override
    public String status() {
        return getMetaData().get(STATUS).toString();
    }

    @Override
    public Map<String, Object> getMetaData() {
        @SuppressWarnings("unchecked")
        Map<String, Object> metaData = metaDataCache.get(purchase_id) == null ?
                remoteAgent.getPurchaseMetaData(purchase_id) : (Map<String, Object>) metaDataCache.get(purchase_id);
        return metaData;
    }

    private String getListingId() {
        return getMetaData().get(LISTING_ID).toString();
    }
}
