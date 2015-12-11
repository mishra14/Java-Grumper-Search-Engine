package edu.upenn.cis455.project.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.upenn.cis455.project.dynamoDA.DynamoIndexerDA;
import edu.upenn.cis455.project.storage.Postings;

// TODO: Auto-generated Javadoc
/**
 * The Class Reduce.
 */
public class Reduce extends Reducer<Text, Text, Text, Text>
{
	
	/** The log. */
	private Log log = LogFactory.getLog(Reduce.class);
	
	/** The tf. */
	private static HashMap<String, Integer> tf = null;
	
	/** The Constant bucketSize. */
	private final static int bucketSize = 119866;
	
	/** The Constant MAX_LIST. */
	private final static int MAX_LIST = 2000;
	
	/** The df. */
	private int df;
	
	/** The Constant tablename. */
	private static final String tablename = "Unigram";
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException {
		//setFieldsFromKey(key);
		df = computeDF(values);
		
		String postingsList = createPostingsList();  
		//context.write(key, new Text(postingsList));
		
//		ArrayList<Postings> postingsList = createPostings();
		DynamoIndexerDA dynamo = new DynamoIndexerDA(tablename);
		dynamo.save(key.toString(), postingsList);
		//dynamo.saveIndexWithBackOff	(key.toString(), postingsList, context);
//		dynamo.saveMultipleIndex(key.toString(), postingsList, context);
    }
	
	/**
	 * Compute df.
	 *
	 * @param docIDs the doc i ds
	 * @return the int
	 */
	private int computeDF( Iterable<Text> docIDs){
		 Set<String> docIDset = new HashSet<>();
		 tf = new HashMap<>();
		 log.info("DOC IDS: " + docIDs.toString());
		 for (Text id : docIDs){
			 String docID = id.toString();
			 docIDset.add(docID);
			 //add to tf dictionary
			 if (tf.containsKey(docID)){
				  System.err.println("Compute Postings : Doc ID in tf" + docID.toString());
				  int count = tf.get(docID);
				  tf.put(docID, count + 1);
				  
			  }
			  else {
				  System.err.println("Compute Postings : Doc ID new" + docID.toString());
				  tf.put(docID, 1);
			  }			 
		  }
		 
		  return docIDset.size();
	  }
	  
	  
	  /**
  	 * Sort postings.
  	 *
  	 * @return the array list
  	 */
  	private ArrayList<Postings> sortPostings(){
			 
		  ArrayList<Postings> postingsList = new ArrayList<Postings>();
		  for(String docID: tf.keySet()){
			  float idf = (float) Math.log(bucketSize /df);// replace later with bucketsize
			  float tfidf = tf.get(docID) * idf ;
			  Postings newPostings = new Postings(docID, tfidf, idf);
			  postingsList.add(newPostings);
		  }
		  Collections.sort(postingsList);
		  return postingsList; 
	  }
	  
  	/**
  	 * Creates the postings list.
  	 *
  	 * @return the string
  	 */
  	private String createPostingsList(){
		  StringBuilder postings = new StringBuilder();
		  ArrayList<Postings> postingsList = sortPostings();
		  int size = postingsList.size() - 1;
		  int i = 0;
		  for(Postings posting : postingsList){
			  if (i < size )
				  postings.append(posting.getPosting()+ " " +
					  posting.getTfidf()+ " "+ posting.getIdf() +"\t");
			  else 
				  postings.append(posting.getPosting()+ " " +
						  posting.getTfidf()+ " "+ posting.getIdf());
			  i++;
			  if ( i > MAX_LIST)
				  break;
		  }
		  return postings.toString();
	  }
	  
	 
}