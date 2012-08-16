package org.openstack.client;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;

import org.openstack.crypto.Md5Hash;
import org.openstack.utils.RandomUtil;
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
		Md5Hash actualHash = new Md5Hash.Hasher().hash(actual);
		Md5Hash expectedHash = new Md5Hash.Hasher().hash(expected);

		assertEquals(actualHash, expectedHash);
	}
}
