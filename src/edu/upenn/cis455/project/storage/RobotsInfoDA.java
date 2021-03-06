package edu.upenn.cis455.project.storage;

import com.sleepycat.persist.PrimaryIndex;

import edu.upenn.cis455.project.bean.RobotsInfo;

public class RobotsInfoDA{
	
	public static RobotsInfo getInfo(String domain)
	{
		
		RobotsInfo info = null;
		if (DBWrapper.getStore() != null)
		{
			PrimaryIndex<String, RobotsInfo> userPrimaryIndex = DBWrapper.getStore()
					.getPrimaryIndex(String.class, RobotsInfo.class);
			if (userPrimaryIndex != null)
			{
				info = userPrimaryIndex.get(domain);
			}
		}
		
		return info;
	}
	
	
	public static void putInfo(RobotsInfo info)
	{
		if (DBWrapper.getStore() != null)
		{
			PrimaryIndex<String, RobotsInfo> userPrimaryIndex = DBWrapper.getStore()
					.getPrimaryIndex(String.class, RobotsInfo.class);
			if (userPrimaryIndex != null)
			{
				userPrimaryIndex.put(info);
			}
		}
	}
	
	public static boolean contains(String domain){
		if(getInfo(domain)!=null)
			return true;
		
		return false;
	}

}
