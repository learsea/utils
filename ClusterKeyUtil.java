/**将key-value写入redis集群所有节点的工具类**/
package com.sibu.agencyquick.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPool;
import redis.clients.util.ClusterNodeInformation;
import redis.clients.util.JedisClusterCRC16;

@Component
public class ClusterKeyUtil {

	@Autowired
	private JedisCluster jedisCluster;

	private Map<String, List<String>> keysMap = null;
	private Random random = new Random();

	private int REDIS_SLOTS_NUM = 0;
	private Set<Integer> slots = null;

	@PostConstruct
	private void fillSlotsSet() {
		slots = new HashSet<Integer>();
		keysMap = new HashMap<String, List<String>>();
		JedisPool pool = jedisCluster.getClusterNodes().values().iterator().next();
		Jedis jedis = pool.getResource();
		String localNodes = jedis.clusterNodes();
		for (String nodeInfo : localNodes.split("\n")) {
			ClusterNodeInformation clusterNodeInfo = JedisClusterInfoCache.nodeInfoParser.parse(nodeInfo,
					new HostAndPort(jedis.getClient().getHost(), jedis.getClient().getPort()));
			List<Integer> slotsList = clusterNodeInfo.getAvailableSlots();
			if (slotsList.size() > 0) {
				slots.add(slotsList.get(0));
			}
		}
		REDIS_SLOTS_NUM = slots.size();
	}

	private void genKey(final String key) {
		Set<Integer> slotsCopy = new HashSet<Integer>();
		slotsCopy.addAll(slots);
		List<String> newKeyList = new ArrayList<String>();
		for (int i = 0; newKeyList.size() < REDIS_SLOTS_NUM; i++) {
			String newKey = key + i;
			int result = JedisClusterCRC16.getSlot(newKey);
			if (slotsCopy.contains(result)) {
				slotsCopy.remove(result);
				newKeyList.add(newKey);
			}
		}
		keysMap.put(key, newKeyList);
	}

	/**
	 * 获取原始key对应的所有集群key，set时需要调用，遍历设置
	 */
	public List<String> getAllKeys(final String key) {
		List<String> newKeyList = keysMap.get(key);
		if (newKeyList == null) {
			genKey(key);
			newKeyList = keysMap.get(key);
		}
		return newKeyList;
	}

	/**
	 * 获取原始key对应的集群随机节点的key
	 */
	public String getRandomKey(final String key) {
		List<String> newKeyList = keysMap.get(key);
		if (newKeyList == null) {
			genKey(key);
			newKeyList = keysMap.get(key);
		}
		return newKeyList.get(random.nextInt(REDIS_SLOTS_NUM));
	}

	public void set(String key, Object o) {
		jedisCluster.set(key, JSON.toJSONString(o));
	}

	public void hset(String key, String field, Object o) {
		jedisCluster.hset(key, field, JSON.toJSONString(o));
	}

	public <T> T hget(String key, String field, Class<T> c) {
		String result = jedisCluster.hget(key, field);
		if (result == null) {
			return null;
		}
		return JSON.parseObject(result, c);
	}

	public void setex(String key, int seconds, Object o) {
		jedisCluster.setex(key, seconds, JSON.toJSONString(o));
	}

	public <T> T get(String key, Class<T> c) {
		String result = jedisCluster.get(key);
		if (result == null) {
			return null;
		}
		return JSON.parseObject(result, c);
	}
}
