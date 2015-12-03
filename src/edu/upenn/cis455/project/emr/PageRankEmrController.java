package edu.upenn.cis455.project.emr;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.upenn.cis455.project.bean.DocumentRecord;

public class PageRankEmrController
{

	public static void main(String[] args) throws InterruptedException,
			NoSuchAlgorithmException, JsonProcessingException
	{
		String emrInputPath = "s3://edu.upenn.cis455.project.urls/";
		String emrOutputBucketName = "edu.upenn.cis455.project.pagerank";
		String emrOutputPrefix = "output";
		String clusterLogPath = "s3://edu.upenn.cis455.project.pagerank/log";
		String emrJarPath = "s3://edu.upenn.cis455.project.pagerank/jar/pagerank.jar";
		String emrStepName = "pagerank";
		String clusterId = "j-2I6PAMV3PQ3ET";
		String clusterName = "page rank cluster";
		String ec2AccessKeyName = "test";
		String tableName = "edu.upenn.cis455.project.pagerank";
		String primaryKeyName = "hostName";
		String valueKeyName = "rank";
		EmrController controller = new EmrController(emrInputPath,
				emrOutputBucketName, emrOutputPrefix, emrJarPath, emrStepName,
				clusterId, tableName, "hostName", "rank");
		/*EmrController controller = new EmrController(emrInputPath,
				emrOutputBucketName, emrOutputPrefix, clusterLogPath,
				emrJarPath, emrStepName, clusterName, ec2AccessKeyName,
				tableName, primaryKeyName, valueKeyName);
		controller.createCluster();*/
		controller.setIterative(true);
		int i = 0;
		do
		{
			controller.setDone(true);
			controller.runJob();
			controller.s3ToDynamo(controller.getObjectNamesForBucket());
			System.out.println("Status after iterations " + (++i) + " - "
					+ controller.isDone());
		}
		while (!controller.isDone());
		//controller.terminateCluster();
		List<String> docs = controller
				.getObjectNamesForBucket("edu.upenn.cis455.project.documents");
		System.out.println(docs.size());
		System.out.println("Page rank terminated");
	}

}