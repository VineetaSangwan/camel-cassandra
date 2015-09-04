package com.mmt.dpt.cassandra.component.common;

import org.apache.cassandra.thrift.Cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AstyanaxContext.Builder;
import com.netflix.astyanax.AstyanaxTypeFactory;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class KeyspaceFactoryBean<Keyspace>  
{
	private Keyspace keyspace;
	private String clusterName;
	private String keyspaceName;
	private String connectionPoolName = "UHMapperConnectionPool";
	private int portNo;
	private String seeds;
	public String getSeeds() {
		return seeds;
	}
	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}
	public int getPortNo() {
		return portNo;
	}
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}
	public void setConnectionPoolName(String name) {
		this.connectionPoolName = name;
	}
	public String getConnectionPoolName() {
		return this.connectionPoolName;
	}
	public String getKeyspaceName() {
		return this.keyspaceName;
	}
	public void setKeyspaceName(String name) {
		this.keyspaceName = name;
	}
	public void setClusterName(String name) {
		this.clusterName = name;
	}
	public String getClusterName() {
		return this.clusterName;
	}
	public void init() throws Exception
	{
		Builder builder1 = new AstyanaxContext.Builder();
		Builder builder2 = builder1.forCluster(clusterName);
		Builder builder3 = builder2.forKeyspace(keyspaceName);
		ConnectionPoolConfigurationImpl connectionPoolConfig = new ConnectionPoolConfigurationImpl(connectionPoolName);
		connectionPoolConfig.setPort(portNo);
		connectionPoolConfig.setSeeds(seeds);

		Builder builder4 = builder3.withConnectionPoolConfiguration(connectionPoolConfig);
		Builder builder6 = builder4.withAstyanaxConfiguration(new AstyanaxConfigurationImpl());
		CountingConnectionPoolMonitor countConnPoolMonitor = new CountingConnectionPoolMonitor();
		Builder builder5 = builder6.withConnectionPoolMonitor(countConnPoolMonitor);
		AstyanaxTypeFactory<Cassandra.Client> astyanaxFactory = ThriftFamilyFactory.getInstance();
		AstyanaxContext<Keyspace> context = (AstyanaxContext<Keyspace>) builder5.buildKeyspace(astyanaxFactory);
	
		context.start();
		keyspace = context.getEntity();
	}
	public Keyspace getKeyspace() throws Exception 
	{
		if(keyspace == null)
		{
		  init();	
		}
		return keyspace;
	}
}