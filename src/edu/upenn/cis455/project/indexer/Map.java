package edu.upenn.cis455.project.indexer;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.upenn.cis455.project.bean.DocumentRecord;

public class Map extends Mapper<NullWritable, BytesWritable, Text, Text> {
    
	private final Text url = new Text();
	@Override
    public void map(NullWritable key, BytesWritable value, Context context) 
    		throws IOException, InterruptedException {
	    Text word = new Text();
	    DocumentRecord doc = getDocument(value);
	    
	    String line = doc.getDocumentString();
	    url.set(doc.getDocumentId().trim());
	
	    String actualContent = getHtmlText(line);
	    StringTokenizer tokenizer = new StringTokenizer(actualContent, " ,.?\"");
	    while (tokenizer.hasMoreTokens()) {
	        word.set(tokenizer.nextToken().toLowerCase().replaceAll("[^A-Za-z0-9 ]", ""));
	        context.write(word, url);
        }
    }
	
	
	
	public String getHtmlText(String html)
	{
		Document doc = Jsoup.parse(html.replaceAll("(?i)<br[^>]*>", "<pre>\n</pre>"));
		String textContent = doc.select("body").text();
		return textContent;
	}
	
	private DocumentRecord getDocument(BytesWritable value) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		DocumentRecord doc=null;
		try {
			doc = mapper.readValue(new String(value.getBytes()), DocumentRecord.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
}
