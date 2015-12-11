package edu.upenn.cis455.project.indexer.bigram;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
 
// TODO: Auto-generated Javadoc
/**
 * The Class ToolMapReduce.
 */
public class ToolMapReduce extends Configured implements Tool {
 
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ToolMapReduce(), args);
        System.exit(res);
    }
 
    /* (non-Javadoc)
     * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
     */
    @Override
    public int run(String[] args) throws Exception {
 
        Job job = Job.getInstance(new Configuration(), ToolMapReduce.class.getCanonicalName());
        job.setJarByClass(ToolMapReduce.class);
 
     
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
 
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
 
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(WholeFileInputFormat.class);
 
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);
 
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
