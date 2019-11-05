package sg.dex.starfish.impl.squid;

import com.oceanprotocol.common.helpers.EncodingHelper;
import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.api.config.OceanConfigFactory;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.exceptions.DIDFormatException;
import com.oceanprotocol.squid.exceptions.EthereumException;
import com.oceanprotocol.squid.manager.OceanManager;
import com.oceanprotocol.squid.models.DDO;

import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import sg.dex.starfish.Resolver;
import sg.dex.starfish.util.DID;
import sg.dex.starfish.util.Hex;
import sg.dex.starfish.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import io.reactivex.Flowable;
import java.util.Properties;

import com.oceanprotocol.common.web3.KeeperService;
import com.oceanprotocol.keeper.contracts.DIDRegistry;

public class DexResolver implements Resolver {
    private DIDRegistry contract;

    /**
     * Create SquidResolverImpl
     *
     * @param DIDRegistry contract
     */
    private DexResolver(DIDRegistry contract)  {
        this.contract = contract;
    }

    /**
     * Creates a SquidResolverImpl
     *
     * @param String addressFrom: owner of DID record in the ledger. Only this address is allowed to update it
     * @param String password: its password
     * @param String credentialFile: its parity credential file
     * @throws IOException, CipherException
     * @return SquidResolverImpl The newly created SquidResolverImpl
     */
    public static DexResolver create(String addressFrom, String password, String credentialFile) throws IOException, CipherException{
        Properties properties = getProperties();
        String address = (String)properties.getOrDefault("contract.DIDRegistry.address", "");
        OceanConfig oceanConfig = OceanConfigFactory.getOceanConfig(properties);
        oceanConfig.setMainAccountAddress(addressFrom);
        oceanConfig.setMainAccountPassword(password);
        oceanConfig.setMainAccountCredentialsFile(credentialFile);
        KeeperService keeper = SquidService.getKeeperService(oceanConfig);
        DIDRegistry contract = DIDRegistry.load(address, keeper.getWeb3(), keeper.getTxManager(), keeper.getContractGasProvider());
        return new DexResolver(contract);
    }

    @Override
    public String getDDOString(DID did) {
        com.oceanprotocol.squid.models.DID squidDID = null;
        try {
            squidDID = new com.oceanprotocol.squid.models.DID(did.toString());
        } catch (DIDFormatException e) {
            e.printStackTrace();
            throw Utils.sneakyThrow(e);
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
                    Arrays.asList(SquidService.getProvider()), ddo).send();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (CipherException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return receipt.getStatus().equals("0x1");
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        try (InputStream input = DexResolver.class.getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IOException("properties files is missing");
            }

            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
