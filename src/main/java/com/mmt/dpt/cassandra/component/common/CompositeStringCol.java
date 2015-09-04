package com.mmt.dpt.cassandra.component.common;

import com.netflix.astyanax.annotations.Component;

public class CompositeStringCol 

{

	public CompositeStringCol(){
		
	}
	private @Component(ordinal=0) String column1;

	private @Component(ordinal=1) String column2;


}

