import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Top5Terms {
	
	// Each mapper will get the top 5 most frequent terms from its corresponding input split
	public static class Top5TermsMapper extends Mapper<Object, Text, Text, LongWritable> {
		private TreeMap<Long, String> termFrequencies = new TreeMap<Long, String>();
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// Split line into term and count
			String[] termAndCount = value.toString().split("\t");
			String term = termAndCount[0];
			String count = termAndCount[1];
			
			long frequency = Long.parseLong(count);
			
			// Adding frequency and term to treemap
			termFrequencies.put(frequency, term);
			
			// Removing term with lowest frequency from treemap if there are more than 5 entries. Since the count is being used as the key, 
			// this will be the first entry of the treemap as it automatically sorts the entries in natural order of the keys
			if(termFrequencies.size() > 5) {
				termFrequencies.remove(termFrequencies.firstKey());
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// After the mapper is finished, the treemap will contain the top-5 most frequent terms from its input split, so now the terms can be written
			for(Map.Entry<Long, String> entry : termFrequencies.entrySet()) {
				long count = entry.getKey();
				String term = entry.getValue();
				context.write(new Text(term), new LongWritable(count));
			}
		}
	}
	
	// The single reducer will receive the top-5 terms from each mapper's input split and reduce it to the global top-5 terms from the entire input
	public static class Top5TermsReducer extends Reducer<Text, LongWritable, LongWritable, Text> {
		private TreeMap<Long, String> termFrequencies = new TreeMap<Long, String>();
		
		@Override
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			String term = key.toString();
			long count = 0;
			
			// Getting the frequency for each term passed to reducer
			for(LongWritable value : values) {
				count = value.get();
			}
			
			// putting each frequency and term in treemap
			termFrequencies.put(count, term);
			
			// Removing term with lowest frequency if treemap contains more than 5 entries
			if(termFrequencies.size() > 5) {
				termFrequencies.remove(termFrequencies.firstKey());
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// After reducer is finished, treemap will contain final top-5 terms and frequencies, which can then be written to output
			for(Map.Entry<Long, String> entry : termFrequencies.entrySet()) {
				long count = entry.getKey();
				String term = entry.getValue();
				context.write(new LongWritable(count), new Text(term));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
    	// Require that user passes in an input path and an output path
        if(args.length != 2) {
        	System.err.println("Usage: Top5Terms <input path> <output path>");
        	System.exit(-1);
        }
        
        // Configuring job
        Job job = new Job();
        job.setJobName("Top 5 Terms HW 5");
        job.setJarByClass(Top5Terms.class);
        
        // Need to have only 1 reducer to calculate final top-5 terms
        job.setNumReduceTasks(1);
        
        // Setting input and output paths to command line args
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        // Setting mapper and reducer classes
        job.setMapperClass(Top5TermsMapper.class);
        job.setReducerClass(Top5TermsReducer.class);
        
        // Setting mapper output key and value classes, since they are different from reducer output key and value classes
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        
        // Setting output classes
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);
        
        // Waiting for job to complete, then exit
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}