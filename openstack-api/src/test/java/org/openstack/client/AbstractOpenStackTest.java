package org.openstack.client;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;

import org.openstack.client.utils.RandomUtil;
import org.openstack.utils.Md5Hasher;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;

public class AbstractOpenStackTest {
	protected OpenstackTestContext context;
	protected RandomUtil random = new RandomUtil();

	@BeforeMethod
	public void beforeMethod() {
		context = OpenstackTestContext.buildFromProperties();
	}

	protected void skipUntilBugFixed(int bugNumber) {
		throw new SkipException("Skipping because of bug #" + bugNumber);
	}

	protected void assertStreamsTheSame(InputStream actual, InputStream expected) throws DigestException, IOException {
		byte[] actualHash = new Md5Hasher().hash(actual);
		byte[] expectedHash = new Md5Hasher().hash(expected);

		assertEquals(actualHash, expectedHash);
	}
}
