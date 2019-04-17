package sg.dex.squid;

import com.oceanprotocol.squid.exceptions.EthereumException;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import sg.dex.starfish.Ocean;
import sg.dex.starfish.connection_check.AssumingConnection;
import sg.dex.starfish.connection_check.ConnectionChecker;
import sg.dex.starfish.developer_usecase.RemoteAgentConfig;
import sg.dex.starfish.exception.AuthorizationException;
import sg.dex.starfish.impl.squid.SquidAccount;
import sg.dex.starfish.impl.squid.SquidAgent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SquidIntegrationTests {
	private static Ocean ocean = null;
	private static SquidAgent squid = null;
	private static SquidAccount publisherAccount = null;
	private static SquidAccount purchaserAccount = null;

    @ClassRule
    public static AssumingConnection assumingConnection =
            new AssumingConnection(new ConnectionChecker(RemoteAgentConfig.getBargeUrl()));

	@Test
	public void bConfigureSquidAgent() {
		System.out.println("=== bConfigureSquidAgent ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
			try {
				squid = SquidBuilder.create(ocean);
				System.out.println("Accounts:");
				for (com.oceanprotocol.squid.models.Account account : squid.list()) {
					System.out.println(account);
				}
			} catch (Exception e) {
				System.out.println("unable to build squid: " + e);
			}
		}
	}

	@Test
	public void cGetPublisherAccount() {
		System.out.println("=== cGetPublisherAccount ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
			String publisherAddress = squid.getConfigString("account.parity.address");
			String publisherPassword = squid.getConfigString("account.parity.password");
			System.out.println("publisherAddress: " + publisherAddress);
			publisherAccount = SquidAccount.create(publisherAddress, publisherPassword, squid);
			try {
				System.out.println("publisherAddress: " + publisherAddress + " balance: " + publisherAccount.balance());
			} catch (EthereumException e) {
				System.out.println("publisherAddress: " + publisherAddress + " UNABLE to get balance");
			}
			try {
				publisherAccount.unlock();
				// try {
				// 	publisherAccount.requestTokens(20);
				// } catch (AuthorizationException e) {
				// 	System.out.println("unable to request publisher tokens: " + e);
				// }
			} catch (AuthorizationException e) {
				System.out.println("unable to unlock publisher account: " + e);
			}
		}
	}

	@Test
	public void dCreateAsset() {
		System.out.println("=== dCreateAsset ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
		}
	}

	@Test
	public void eRegisterAsset() {
		System.out.println("=== eRegisterAsset ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
		}
	}

	@Test
	public void fGetPurchaserAccount() {
		System.out.println("=== fGetPurchaserAccount ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
			String purchaserAddress = squid.getConfigString("account.parity.address2");
			String purchaserPassword = squid.getConfigString("account.parity.password2");
			purchaserAccount = SquidAccount.create(purchaserAddress, purchaserPassword, squid);
			try {
				System.out.println("purchaserAddress: " + purchaserAddress + " balance: " + purchaserAccount.balance());
			} catch (EthereumException e) {
				System.out.println("purchaserAddress: " + purchaserAddress + " UNABLE to get balance");
			}
			try {
				purchaserAccount.unlock();
				// try {
				// 	purchaserAccount.requestTokens(10);
				// } catch (AuthorizationException e) {
				// 	System.out.println("unable to request purchaser tokens: " + e);
				// }
			} catch (AuthorizationException e) {
				System.out.println("unable to unlock purchaser account: " + e);
			}
		}
	}

	@Test
	public void gSearchListings() {
		System.out.println("=== gSearchListings ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
		}
	}

	@Test
	public void hPurchaseAsset() {
		System.out.println("=== hPurchaseAsset ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
		}
	}

	@Test
	public void iDownloadAsset() {
		System.out.println("=== iDownloadAsset ===");
		if (ocean == null)  {
			System.out.println("WARNING: barge not running");
		} else {
		}
	}

}
