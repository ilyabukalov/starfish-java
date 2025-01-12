package developerTC;

import org.junit.jupiter.api.Test;
import sg.dex.starfish.Resolver;
import sg.dex.starfish.impl.memory.LocalResolverImpl;
import sg.dex.starfish.util.DID;
import sg.dex.starfish.util.JSON;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * As a developer working with Ocean,
 * I need a way to connect to the Ocean Network
 */
public class TestResolver_01 {


    @Test
    public void testOceanConnect() {

        //
        Resolver resolver = new LocalResolverImpl();
        DID surferDID = DID.createRandom();
        Map<String, Object> ddo = new HashMap<>();
        ddo.put("test", "1234");
        resolver.registerDID(surferDID, JSON.toPrettyString(ddo));
        assertEquals(resolver.getDDO(surferDID).get("test").toString(), "1234");


    }

    @Test
    public void testOceanConnectDoubleRegistration() {
        Resolver resolver = new LocalResolverImpl();
        DID surferDID = DID.createRandom();
        Map<String, Object> ddo = new HashMap<>();
        ddo.put("test", "test");
        resolver.registerDID(surferDID, JSON.toPrettyString(ddo));
        assertEquals(resolver.getDDO(surferDID).get("test").toString(), "test");

        ddo.put("test", "testAgain");
        resolver.registerDID(surferDID, JSON.toPrettyString(ddo));
        assertEquals(resolver.getDDO(surferDID).get("test").toString(), "testAgain");


    }

    @Test
    public void testNullDID() {
        Resolver resolver = new LocalResolverImpl();
        Map<String, Object> ddo = new HashMap<>();
        DID surferDID = null;
        ddo.put("test", "test");
        assertThrows(IllegalArgumentException.class, () -> {
            resolver.registerDID(surferDID, JSON.toPrettyString(ddo));
        });

    }

}
