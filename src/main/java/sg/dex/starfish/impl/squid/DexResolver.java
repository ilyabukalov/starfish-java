package sg.dex.starfish.impl.squid;

import com.oceanprotocol.common.helpers.EncodingHelper;
import com.oceanprotocol.common.web3.KeeperService;
import com.oceanprotocol.keeper.contracts.DIDRegistry;
import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.api.config.OceanConfigFactory;
import com.oceanprotocol.squid.exceptions.DIDFormatException;
import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import sg.dex.starfish.Resolver;
import sg.dex.starfish.util.DID;
import sg.dex.starfish.util.Hex;
import sg.dex.starfish.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DexResolver implements Resolver {
    private DIDRegistry contract;
    private SquidService squidService;

    /**
     * Create DexResolver
     *
     * @param DIDRegistry contract
     * @param SquidService squidService
     */
    private DexResolver(DIDRegistry contract, SquidService squidService)  {
        this.contract = contract;
        this.squidService = squidService;
    }

    /**
     * Creates a SquidResolverImpl
     *
     * @param String configFile. All information about account credentials will be taken from this file
     * @throws IOException, CipherException
     * @return SquidResolverImpl The newly created SquidResolverImpl
     */
    public static DexResolver create(String configFile) throws IOException, CipherException{
        SquidService squidService = SquidService.create(configFile);
        Properties properties = squidService.getProperties();
        String address = (String)properties.getOrDefault("contract.DIDRegistry.address", "");
        OceanConfig oceanConfig = OceanConfigFactory.getOceanConfig(properties);
        KeeperService keeper = squidService.getKeeperService(oceanConfig);
        DIDRegistry contract = DIDRegistry.load(address, keeper.getWeb3(), keeper.getTxManager(), keeper.getContractGasProvider());
        return new DexResolver(contract, squidService);
    }

    @Override
    public String getDDOString(DID did) {
        com.oceanprotocol.squid.models.DID squidDID = null;
        try {
            squidDID = new com.oceanprotocol.squid.models.DID(did.toString());
        } catch (DIDFormatException e) {
            e.printStackTrace();
            return null;
        }

        String didHash = squidDID.getHash();
        BigInteger blockNumber = BigInteger.valueOf(0);
        try {
            blockNumber = (BigInteger) contract.getBlockNumberUpdated(EncodingHelper.hexStringToBytes(didHash)).send();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
        }

        EthFilter filter = new EthFilter(DefaultBlockParameter.valueOf(blockNumber), DefaultBlockParameter.valueOf(blockNumber), contract.getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(contract.DIDATTRIBUTEREGISTERED_EVENT));
        String didTopic = "0x" + didHash;
        filter.addOptionalTopics(new String[]{didTopic});

        Flowable<DIDRegistry.DIDAttributeRegisteredEventResponse> floable = contract.dIDAttributeRegisteredEventFlowable(filter);
        ArrayList<DIDRegistry.DIDAttributeRegisteredEventResponse> outcome = new ArrayList<>();
        floable.subscribe(log -> {
            outcome.add(log);
        });

        return outcome.size() == 1 ? outcome.get(0)._value : null;
    }

    @Override
    public boolean registerDID(DID did, String ddo) {
        String checksum = "0x0";

        com.oceanprotocol.squid.models.DID didSquid = null;
        try {
            didSquid = new com.oceanprotocol.squid.models.DID(did.toString());
        } catch (DIDFormatException e) {
            e.printStackTrace();
            return false;
        }

        TransactionReceipt receipt = null;
        try {
            receipt = (TransactionReceipt)contract.registerAttribute(
                    EncodingHelper.hexStringToBytes(didSquid.getHash()),
                    EncodingHelper.hexStringToBytes(Hex.toZeroPaddedHexNoPrefix(checksum)),
                    Arrays.asList(squidService.getProvider()), ddo).send();
        } catch (IOException e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
        } catch (CipherException e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
        }

        return receipt.getStatus().equals("0x1");
    }
}