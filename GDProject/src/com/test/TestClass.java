package com.test;

import java.math.BigDecimal;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BigDecimal bd = new BigDecimal(2);
		//System.out.println(bd.setScale(2).toString());
		System.out.println((new BigDecimal(19.99).compareTo(new BigDecimal(20)))<0);
	}

}
