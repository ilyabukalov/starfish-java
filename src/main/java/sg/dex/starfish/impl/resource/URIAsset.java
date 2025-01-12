package sg.dex.starfish.impl.resource;

import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import sg.dex.starfish.DataAsset;
import sg.dex.starfish.constant.Constant;
import sg.dex.starfish.exception.AuthorizationException;
import sg.dex.starfish.exception.GenericException;
import sg.dex.starfish.exception.RemoteException;
import sg.dex.starfish.exception.StorageException;
import sg.dex.starfish.impl.AAsset;
import sg.dex.starfish.util.DID;
import sg.dex.starfish.util.HTTP;
import sg.dex.starfish.util.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * A specialised asset class that references data at a given URI.
 * <p>
 * It is assumed that asset content can be accessed with a HTTP GET to the given URI.
 */
public class URIAsset extends AAsset implements DataAsset {

    private final URI uri;

    protected URIAsset(String meta, URI uri) {
        super(meta);
        this.uri = uri;
    }

    /**
     * Creates a HTTP asset using the given URI and metadata
     *
     * @param uri of the resource
     * @return RemoteHttpAsset instance created using given params with default metadata this include DATE_CREATED,TYPE,CONTENT_TYPE
     */
    public static URIAsset create(URI uri, String metaString) {
        return new URIAsset(metaString, uri);
    }

    /**
     * Creates a HTTP asset using the given URI.
     *
     * @param uri of the resource
     * @return RemoteHttpAsset instance created using given params with default metadata this include DATE_CREATED,TYPE,CONTENT_TYPE
     */
    public static URIAsset create(URI uri) {
        return create(uri, (Map<String, Object>) null);
    }

    /**
     * Creates a HTTP asset using the given URI string.
     *
     * @param uri      of the resource
     * @param metaData metadata associated with the asset.This metadata will be be added in addition to default
     *                 metadata i.e DATE_CREATED,TYPE,CONTENT_TYPE.If same key,value is provided then the
     *                 default value will be overridden.
     * @return RemoteHttpAsset instance created using given params with given metadata.
     */
    public static URIAsset create(URI uri, Map<String, Object> metaData) {
        return create(uri, JSON.toPrettyString(buildMetaData(metaData, uri)));
    }

    /**
     * This method is to build the metadata of the Resource Asset
     *
     * @param metaData metadata associated with the asset.This metadata will be be added in addition to default
     *                 metadata i.e DATE_CREATED,TYPE,CONTENT_TYPE.If same key,value is provided then the
     *                 default value will be overridden.
     * @return String buildMetadata
     */
    private static Map<String, Object> buildMetaData(Map<String, Object> metaData, URI uri) {

        Map<String, Object> ob = new HashMap<>();
        ob.put(Constant.DATE_CREATED, Instant.now().toString());
        ob.put(Constant.TYPE, Constant.DATA_SET);
        ob.put(Constant.CONTENT_TYPE, "application/octet-stream");


        if (metaData != null) {

            for (Map.Entry<String, Object> me : metaData.entrySet()) {
                ob.put(me.getKey(), me.getValue());
            }
        }
        return ob;
    }


    /**
     * Gets raw data corresponding to this Asset
     *
     * @return An input stream allowing consumption of the asset data
     * @throws AuthorizationException if requestor does not have access permission
     * @throws StorageException       if unable to load the Asset
     */
    @Override
    public InputStream getContentStream() {
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = null;
        try {
            response = HTTP.execute(httpget);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 404) {
                throw new RemoteException("Asset ID not found at: " + uri);
            }
            if (statusCode == 200) {
                InputStream inputStream = HTTP.getContent(response);
                return inputStream;
            } else {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            throw new RemoteException(" Getting Remote Asset content failed: ", e);
        }

    }

    /**
     * Gets RemoteAsset size
     *
     * @return size of the RemoteAsset
     */
    @Override
    public long getContentSize() {
        try {
            return getContentStream().available();
        } catch (IOException e) {
            throw new GenericException(
                    "Exception occurred  for asset id :" + getAssetID() + " while finding getting the Content size :",
                    e);
        }
    }

    @Override
    public DID getDID() {
        throw new UnsupportedOperationException("Can't get DID for asset of type " + this.getClass());
    }

    public URI getSource() {
        return uri;
    }

    @Override
    public DataAsset updateMeta(String newMeta) {
        return create(uri, newMeta);
    }

}
