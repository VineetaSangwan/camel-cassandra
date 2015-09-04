/**************************************************************************************
 http://code.google.com/a/apache-extras.org/p/camel-extra

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.


 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 02110-1301, USA.

 http://www.gnu.org/licenses/gpl-2.0-standalone.html
 ***************************************************************************************/

package com.mmt.dpt.cassandra.component.main;

import static com.mmt.dpt.cassandra.component.common.CassandraComponentConstants.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import com.mmt.dpt.cassandra.component.common.CustomSerializerCassandra;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Composite;
import com.netflix.astyanax.serializers.CompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;


/**
 * Couchbase producer generates various type of operations. PUT, GET, and DELETE are currently supported
 */

public class CassandraProducer extends DefaultProducer {

	private CassandraEndpoint endpoint;
	

	public CassandraProducer(CassandraEndpoint endpoint) throws Exception {
		super(endpoint);
		this.endpoint = endpoint;

	}

	public void process(Exchange exchange) throws Exception {

		Map<String, Object> headers = exchange.getIn().getHeaders();

		String id = (headers.containsKey(ROW_KEY))
				? exchange.getIn().getHeader(ROW_KEY, String.class)
						: endpoint.getId();
		String columnFamilyName = exchange.getIn().getHeader(COLUMN_FAMILY, String.class);
		if(columnFamilyName==null || columnFamilyName.trim().equals("")){
			throw new CassandraException("Column family can't be null or empty!!!", exchange);
		}
			
		String rowKeySerializerName=exchange.getIn().getHeader(ROW_KEY_SERIALIZER, String.class);
		if(rowKeySerializerName==null || rowKeySerializerName.trim().equals("")){
			throw new CassandraException("Row key serializer can't be null or empty!!!", exchange);
		}
		
		String columnKeySerializerName=exchange.getIn().getHeader(COLUMN_KEY_SERIALIZER,String.class);
		if(columnKeySerializerName==null || columnKeySerializerName.trim().equals("")){
			throw new CassandraException("Column key serializer can't be null or empty!!!", exchange);
		}
		String columnValueSerializer=exchange.getIn().getHeader(VALUE_SERIALIZER,String.class);
		if(columnValueSerializer==null || columnValueSerializer.trim().equals("")){
			throw new CassandraException("Column value serializer can't be null or empty!!!", exchange);
		}
		
		if (endpoint.getOperationType().equals(GET_BY_ROW_KEY)) {
			log.info("Type of operation: "+GET_BY_ROW_KEY);
			Object obj = exchange.getIn().getBody();
			exchange.getOut().setBody(getFromDBByKey(id, columnFamilyName, rowKeySerializerName, columnKeySerializerName,columnValueSerializer, exchange));  
		}else if (endpoint.getOperationType().equals(GET_BY_RANGE_KEY)){
			log.info("Type of operation: "+GET_BY_ROW_KEY);
			Object obj = exchange.getIn().getBody();
			Object startKey=exchange.getIn().getHeader(START_KEY,Object.class);
			Object endKey=exchange.getIn().getHeader(END_KEY);
			exchange.getOut().setBody(getFromDBByRangeKey(id, columnFamilyName, rowKeySerializerName, columnKeySerializerName,columnValueSerializer, exchange,startKey,endKey));  
		}

				//cleanup the cache headers
		exchange.getIn().removeHeader(ROW_KEY);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<Object, Object> getFromDBByKey(String rowKey, String columnFamilyName,String rowKeySerializerName,String columnKeySerializerName,String columnValueSerializer,  Exchange exchange) throws CassandraException{
		Map<String, Map<Object, Object>> resultMap=new HashMap<String, Map<Object,Object>>();
 		Keyspace keyspace = endpoint.getKeyspace();
		MutationBatch m = keyspace.prepareMutationBatch();
		ColumnFamily ColumnFamily = new ColumnFamily(columnFamilyName,CustomSerializerCassandra.getSerializer(rowKeySerializerName) , CustomSerializerCassandra.getSerializer(columnKeySerializerName));
		OperationResult result=null;
		try {
			result = keyspace.prepareQuery(ColumnFamily).getKey(rowKey).execute();
		} catch (ConnectionException e) {
			log.error(e.getMessage());
			throw new CassandraException(e.getMessage(), exchange);
		}
		ColumnList columns = (ColumnList)result.getResult();
		Iterator<Column> colIterator = columns.iterator();
		Map<Object, Object> columnMap =new LinkedHashMap<Object, Object>();
		while(colIterator.hasNext()){
			Column newCol=null;
			if(colIterator!=null )
				newCol = (Column) colIterator.next();
			columnMap.put(newCol.getName(), newCol.getValue(CustomSerializerCassandra.getSerializer(columnValueSerializer)));

		}
		return columnMap;
	}
	
	private Map<Object, Object> getFromDBByRangeKey(String rowKey, String columnFamilyName,String rowKeySerializerName,String columnKeySerializerName,String columnValueSerializer,  Exchange exchange, Object startKey, Object endKey) throws CassandraException{
		Map<String, Map<Object, Object>> resultMap=new HashMap<String, Map<Object,Object>>();
 		Keyspace keyspace = endpoint.getKeyspace();
		MutationBatch m = keyspace.prepareMutationBatch();
		ColumnFamily ColumnFamily = new ColumnFamily(columnFamilyName,CustomSerializerCassandra.getSerializer(rowKeySerializerName) , CustomSerializerCassandra.getSerializer(columnKeySerializerName));
		OperationResult result=null;
		try {
			result = keyspace.prepareQuery(ColumnFamily).getKey(rowKey).withColumnRange(startKey, endKey, false,20 ).execute();
		} catch (ConnectionException e) {
			log.error(e.getMessage());
			throw new CassandraException(e.getMessage(), exchange);
		}
		ColumnList columns = (ColumnList)result.getResult();
		Iterator<Column<Composite>> colIterator = columns.iterator();
		Map<Object, Object> columnMap =new LinkedHashMap<Object, Object>();
		while(colIterator.hasNext()){
			Column newCol=null;
			if(colIterator!=null )
				newCol = (Column) colIterator.next();
			columnMap.put(newCol.getName(), newCol.getValue(CustomSerializerCassandra.getSerializer(columnValueSerializer)));

		}
		return columnMap;
		
	}


}
