package com.walmartlabs.productgenome.rulegenerator.algos;

import java.util.Scanner;

public class TestClass {

	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		while(true) {
			if(sc.hasNextInt()) {
				System.out.println("Int : " + sc.nextInt());
			}
			else {
				System.out.println("Error. Exiting !!");
				break;
			}
		}
	}
}
