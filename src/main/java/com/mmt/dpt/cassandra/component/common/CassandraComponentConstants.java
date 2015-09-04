package com.mmt.dpt.cassandra.component.common;

import java.util.Map;
import java.util.Set;

public class CassandraComponentConstants {
	
	public static int DEFAULT_CASSANDRA_PORT=9160;
	public static String CASSANDRA_URI_ERROR="";
	public static String GET_BY_ROW_KEY="get_by_row_key";
	public static String GET_BY_RANGE_KEY="get_by_range_key";
	public static String ROW_KEY="row_key";
	public static String RANGE_VALUES="range_values";
	public static String COLUMN_FAMILY="column_family";
	public static String ROW_KEY_SERIALIZER="row_key_serializer";
	public static String COLUMN_KEY_SERIALIZER="column_key_serializer";
	public static String VALUE_SERIALIZER="column_value_serializer";
	public static String START_KEY="start_key";
	public static String END_KEY="end_key";
	public static String CompositeSerializer="CompositeSerializer";
	public static String CUSTOM_TimeStampBookingIDColumnKeySERIALISER="custom_tsbIDSerializer";
	public static int DEFAULT_TIME_OUT=5000;
	public static int MAX_INTEGER=Integer.MAX_VALUE;
	
	
}
