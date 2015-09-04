package com.mmt.dpt.cassandra.component.common;

import com.netflix.astyanax.annotations.Component;

public class TimeStampBookingIDColumnKey
{

	private @Component(ordinal=0) String timestamp;
	private @Component(ordinal=1) String bookingID;
} 