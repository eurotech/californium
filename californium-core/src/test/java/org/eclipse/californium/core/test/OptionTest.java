/*******************************************************************************
 * Copyright (c) 2014 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Martin Lanter - architect and re-implementation
 *    Dominique Im Obersteg - parsers and initial implementation
 *    Daniel Pauli - parsers and initial implementation
 *    Kai Hudalla - logging
 ******************************************************************************/
package org.eclipse.californium.core.test;

import static org.junit.Assert.assertArrayEquals;
import junit.framework.Assert;

import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.OptionNumberRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This test tests the class Option. We test that the conversion of String,
 * integer and long values to byte arrays work properly.
 */
public class OptionTest {

	@Before
	public void setupServer() {
		System.out.println("\nStart "+getClass().getSimpleName());
	}
	
	@After
	public void shutdownServer() {
		System.out.println("End "+getClass().getSimpleName());
	}
	
	@Test
	public void testSetValue() {
		Option option = new Option();

		option.setValue(new byte[4]);
		assertArrayEquals(option.getValue(), new byte[4]);
		
		option.setValue(new byte[] {69, -104, 35, 55, -104, 116, 35, -104});
		assertArrayEquals(option.getValue(), new byte[] {69, -104, 35, 55, -104, 116, 35, -104});
	}
	
	@Test
	public void testSetStringValue() {
		Option option = new Option();
		
		option.setStringValue("");
		assertArrayEquals(option.getValue(), new byte[0]);

		option.setStringValue("Californium");
		assertArrayEquals(option.getValue(), "Californium".getBytes());
	}
	
	@Test
	public void testSetIntegerValue() {
		Option option = new Option();

		option.setIntegerValue(0);
		assertArrayEquals(option.getValue(), new byte[0]);
		
		option.setIntegerValue(11);
		assertArrayEquals(option.getValue(), new byte[] {11});
		
		option.setIntegerValue(255);
		assertArrayEquals(option.getValue(), new byte[] { (byte) 255 });
		
		option.setIntegerValue(256);
		assertArrayEquals(option.getValue(), new byte[] {1, 0});
		
		option.setIntegerValue(18273);
		assertArrayEquals(option.getValue(), new byte[] {71, 97});
		
		option.setIntegerValue(1<<16);
		assertArrayEquals(option.getValue(), new byte[] {1, 0, 0});
		
		option.setIntegerValue(23984773);
		assertArrayEquals(option.getValue(), new byte[] {1, 109, (byte) 250, (byte) 133});
		
		option.setIntegerValue(0xFFFFFFFF);
		assertArrayEquals(option.getValue(), new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
	}
	
	@Test
	public void testSetLongValue() {
		Option option = new Option();

		option.setLongValue(0);
		assertArrayEquals(option.getValue(), new byte[0]);
		
		option.setLongValue(11);
		assertArrayEquals(option.getValue(), new byte[] {11});
		
		option.setLongValue(255);
		assertArrayEquals(option.getValue(), new byte[] { (byte) 255 });
		
		option.setLongValue(256);
		assertArrayEquals(option.getValue(), new byte[] {1, 0});
		
		option.setLongValue(18273);
		assertArrayEquals(option.getValue(), new byte[] {71, 97});
		
		option.setLongValue(1<<16);
		assertArrayEquals(option.getValue(), new byte[] {1, 0, 0});
		
		option.setLongValue(23984773);
		assertArrayEquals(option.getValue(), new byte[] {1, 109, (byte) 250, (byte) 133});

		option.setLongValue(0xFFFFFFFFL);
		assertArrayEquals(option.getValue(), new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
		
		option.setLongValue(0x9823749837239845L);
		assertArrayEquals(option.getValue(), new byte[] {-104, 35, 116, -104, 55, 35, -104, 69});
		
		option.setLongValue(0xFFFFFFFFFFFFFFFFL);
		assertArrayEquals(option.getValue(), new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
	}
	
	@Test
	public void testArbitraryOptions() {
		OptionSet options = new OptionSet();
		options.addETag(new byte[] {1, 2, 3});
		options.addLocationPath("abc");
		options.addOption(new Option(7));
		options.addOption(new Option(43));
		options.addOption(new Option(33));
		options.addOption(new Option(17));

		// Check that options are in the set
		Assert.assertTrue(options.hasOption(OptionNumberRegistry.ETAG));
		Assert.assertTrue(options.hasOption(OptionNumberRegistry.LOCATION_PATH));
		Assert.assertTrue(options.hasOption(7));
		Assert.assertTrue(options.hasOption(17));
		Assert.assertTrue(options.hasOption(33));
		Assert.assertTrue(options.hasOption(43));
		
		// Check that others are not
		Assert.assertFalse(options.hasOption(19));
		Assert.assertFalse(options.hasOption(53));
		
		// Check that we can remove options
		options.clearETags();
		Assert.assertFalse(options.hasOption(OptionNumberRegistry.ETAG));
	}
	
	@Test
	public void testToString() {
		OptionSet options = new OptionSet();
		options.addETag(new byte[] {1, 2, 3});
		options.addETag(new byte[] {(byte)0xBE, (byte)0xEF});
		options.addLocationPath("abc");
		options.setURIPath("/this/is/a/test");
		
		Assert.assertEquals("{\"ETag\":[0x010203,0xbeef], \"Location-Path\":\"abc\", \"Uri-Path\":[\"this\",\"is\",\"a\",\"test\"]}", options.toString());

		options.setMaxAge(77);
		
		Assert.assertEquals("{\"ETag\":[0x010203,0xbeef], \"Location-Path\":\"abc\", \"Uri-Path\":[\"this\",\"is\",\"a\",\"test\"], \"Max-Age\":77}", options.toString());
	}
}
