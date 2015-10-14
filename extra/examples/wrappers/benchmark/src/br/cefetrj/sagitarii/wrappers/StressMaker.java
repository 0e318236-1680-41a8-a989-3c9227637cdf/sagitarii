package br.cefetrj.sagitarii.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class StressMaker {
	private Set<String> list1;
	private Set<String> list2;
	private List<String> list3;
	
	public StressMaker() {
		list1 = new HashSet<String>();
		list2 = new HashSet<String>();
		list3 = new ArrayList<String>();
	}
	

	public void doStress() {
		System.out.println("start stress");
		for ( long x=0; x < 50000; x++  ) {
			String uuid = UUID.randomUUID().toString();
			list1.add(uuid);
		}
		
		System.out.println("phase 2");
		for ( String s : list1 ) {
			list3.add(s);
		}
		
		try {
			Thread.sleep( 10000 );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		Collections.sort( list3 );
		
		System.out.println("phase 3");
		int indexX = 1, indexY = 1;
		for ( String s : list3 ) {
			indexX++;
			indexY = indexX + 2;
			if ( list1.contains( s ) ) {
				try {
					double xx = Math.PI * Math.abs( Math.cos(23.6) ) ;
					list2.add( s + " " + xx );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("done");
		
	}
	
	public static void main(String[] args) {
		new StressMaker().doStress();
	}

}
