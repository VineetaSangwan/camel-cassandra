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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.cassandra.thrift.Cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AstyanaxTypeFactory;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.AstyanaxContext.Builder;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.mmt.dpt.cassandra.component.common.CassandraComponentConstants.*;


/**
 * Represents a Couchbase endpoint that can query Views with a Poll strategy and/or produce various type of operations.
 */

@UriEndpoint(scheme = "cassandra" )
public class CassandraEndpoint extends ScheduledPollEndpoint {

	private Keyspace keyspace;
	private String clusterName;
	private String keyspaceName;
	private String connectionPoolName;
	private int port;
	private String seeds;
	@UriParam
	private int connectTimeout=DEFAULT_TIME_OUT;
	@UriParam
	private String operationType;
	

	public CassandraEndpoint() {
		// TODO Auto-generated constructor stub
	}

	public CassandraEndpoint(String uri, String remaining, CassandraComponent component) throws URISyntaxException {
		super(uri, component);
		String[] remainings=remaining.split(":"); 
		try{
			clusterName = remainings[0];
			keyspaceName = remainings[1];
			connectionPoolName= remainings[2];
			seeds=remainings[3];
			port=Integer.parseInt(remainings[4]);
		}catch(Exception exception){
			throw new IllegalArgumentException(CASSANDRA_URI_ERROR);
		}
		
		init();

	}


	public CassandraEndpoint(String endpointUri) {
		super(endpointUri);
	}


	public Producer createProducer() throws Exception {
		return new CassandraProducer(this);//TODO set cassandra client
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		throw new UnsupportedOperationException("Not yet implemented");
		//Consumer not implemented
	}





	public void init()
	{
		Builder builder1 = new AstyanaxContext.Builder();
		Builder builder2 = builder1.forCluster(clusterName);
		Builder builder3 = builder2.forKeyspace(keyspaceName);
		ConnectionPoolConfigurationImpl connectionPoolConfig = new ConnectionPoolConfigurationImpl(connectionPoolName);
		connectionPoolConfig.setPort(port);
		connectionPoolConfig.setSeeds(seeds);
		connectionPoolConfig.setConnectTimeout(connectTimeout);

		Builder builder4 = builder3.withConnectionPoolConfiguration(connectionPoolConfig);
		Builder builder6 = builder4.withAstyanaxConfiguration(new AstyanaxConfigurationImpl());
		CountingConnectionPoolMonitor countConnPoolMonitor = new CountingConnectionPoolMonitor();
		Builder builder5 = builder4.withConnectionPoolMonitor(countConnPoolMonitor);
		AstyanaxTypeFactory<Cassandra.Client> astyanaxFactory = ThriftFamilyFactory.getInstance();
		AstyanaxContext<Keyspace> context = (AstyanaxContext<Keyspace>) builder5.buildKeyspace(astyanaxFactory);

		context.start();
		keyspace = context.getEntity();
	}

	private Keyspace getCassandraKeySpace() throws IOException, URISyntaxException {
		return keyspace;
	}

	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Keyspace getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getKeyspaceName() {
		return keyspaceName;
	}

	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	public String getConnectionPoolName() {
		return connectionPoolName;
	}

	public void setConnectionPoolName(String connectionPoolName) {
		this.connectionPoolName = connectionPoolName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSeeds() {
		return seeds;
	}

	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

}
