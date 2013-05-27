package com.googlecode.n_orm.cache.read.guava;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.cache.*;
import com.googlecode.n_orm.cf.ColumnFamily;
import com.googlecode.n_orm.storeapi.MetaInformation;

public class GuavaCache implements ICache {

	private static int MAX_SIZE = 10;
	private static long TTL = 2;
	private final Cache<String,  Map<String, byte[]>> cache;

	public GuavaCache() {
		cache = CacheBuilder.newBuilder().maximumSize(MAX_SIZE)
				.expireAfterWrite(TTL, TimeUnit.SECONDS).build();

	}

	public void delete(MetaInformation meta, String table, String key)
			throws CacheException {
		Collection<ColumnFamily<?>> cfs = meta.getElement().getColumnFamilies();
		String familyName=cfs.getClass().getName();
		String myKey = table.concat(key).concat(familyName);
		cache.invalidate(myKey);

	}

	public void insertFamilyData(MetaInformation meta, String table,
			String key, String family, Map<String, byte[]> familyData)
			throws CacheException {
		String myKey=table.concat(key).concat(family);
		cache.put(myKey, Collections.unmodifiableMap(new TreeMap<String,byte[]>(familyData)));
	}

	public Map<String, byte[]> getFamilyData(MetaInformation meta,
			String table, String key,String family) throws CacheException {
		String myKey=table.concat(key).concat(family);
				return cache.getIfPresent(myKey);
		
	}

	public long size() throws CacheException {
		return cache.size();
		
	}

	public void reset() throws CacheException {
		cache.invalidateAll();
	}

	public long getMaximunSize() throws CacheException {
		return MAX_SIZE;

	}
	public void setMaximunSize(int size) throws CacheException {
		this.MAX_SIZE=size;

	}
	public long getTTL() throws CacheException {
		return TTL;
	}
	public void setTTL(long TTL) throws CacheException {
		this.TTL=TTL;

	}
	public boolean existsData(MetaInformation meta,
			String table, String key,String family) throws CacheException{
		if(this.getFamilyData(meta, table, key, family)!=null){
			return true;
		}
		else{
			return false;
		}
		
	}

}
