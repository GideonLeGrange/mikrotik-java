package me.legrange.mikrotik;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ApiConnectionTest extends TestCase {

	private static final String HOSTNAME = "192.168.88.1";
	private static final String ADMIN = "admin";
	private static final String PASSWORD = "plasma";

	public void testConnectTLSStringInt() throws MikrotikApiException, InterruptedException {
		ApiConnection connect = ApiConnection.connectTLS(HOSTNAME, 8729);
		connect.login(ADMIN, PASSWORD);
		List<Map<String, String>> result = connect.execute("/system/resource/print");

		assertEquals(1, result.size());
		assertEquals("MikroTik", result.get(0).get("platform"));
	}

	public void testConnectStringInt() throws MikrotikApiException, InterruptedException {
		ApiConnection connect = ApiConnection.connect(HOSTNAME, 8728);
		connect.login(ADMIN, PASSWORD);
		List<Map<String, String>> result = connect.execute("/system/resource/print");

		assertEquals(1, result.size());
		assertEquals("MikroTik", result.get(0).get("platform"));
	}

}
