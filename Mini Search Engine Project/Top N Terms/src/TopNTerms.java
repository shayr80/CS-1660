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

public class TopNTerms {
	
	public static Long N;
	
	// Each mapper will get the top N most frequent terms from its corresponding input split
	public static class TopNTermsMapper extends Mapper<Object, Text, Text, LongWritable> {
		private TreeMap<Long, String> termFrequencies = new TreeMap<Long, String>();
		
		// Get N value from configuration
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			N = Long.parseLong(context.getConfiguration().get("N"));
		}
		
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			// Split line into term and file names with their corresponding counts
			String[] termAndFiles = value.toString().split("\t");
			String[] filesWithCounts = termAndFiles[1].split(" ");
			String term = termAndFiles[0];
			
			// Splitting file names with counts to get count for each file that contains the term, and adding it to the total count for the term
			long totalCount = 0;
			for(String file : filesWithCounts) {
				String count = file.split(":")[1];
				totalCount += Long.parseLong(count);
			}
			
			// Adding frequency and term to treemap
			termFrequencies.put(totalCount, term);
			
			// Removing term with lowest frequency from treemap if there are more than N entries, since the count is being used as the key, 
			// this will be the first entry of the treemap since it automatically sorts the entries in natural order of the keys
			if(termFrequencies.size() > N) {
				termFrequencies.remove(termFrequencies.firstKey());
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// After the mapper is finished, the treemap will contain the top-n most frequent terms from its input split, so now the terms can be written
			for(Map.Entry<Long, String> entry : termFrequencies.entrySet()) {
				long count = entry.getKey();
				String term = entry.getValue();
				context.write(new Text(term), new LongWritable(count));
			}
		}
	}
	
	// The single reducer will receive the top-n terms from each mapper's input split and reduce it to the global top-n terms from the entire input
	public static class TopNTermsReducer extends Reducer<Text, LongWritable, LongWritable, Text> {
		private TreeMap<Long, String> termFrequencies = new TreeMap<Long, String>();
		
		// Get N value from configuration
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			N = Long.parseLong(context.getConfiguration().get("N"));
		}
		
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
			
			// Removing term with lowest frequency if treemap contains more than N entries
			if(termFrequencies.size() > N) {
				termFrequencies.remove(termFrequencies.firstKey());
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			// After reducer is finished, treemap will contain final top-n terms and frequencies, which can then be written to output
			for(Map.Entry<Long, String> entry : termFrequencies.entrySet()) {
				long count = entry.getKey();
				String term = entry.getValue();
				context.write(new LongWritable(count), new Text(term));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
    	// Require that user passes in an N value, an input path, and an output path
        if(args.length != 3) {
        	System.err.println("Usage: TopNTerms <N value> <input path> <output path>");
        	System.exit(-1);
        }
        
        // Configuring job, setting "N" value to command line arg term passed in by user
        Configuration c = new Configuration();
        c.set("N", args[0]);
        Job job = Job.getInstance(c, "Top N Frequent Terms For N = " + args[0]);
        job.setJar("TopNTerms.jar");
        
        // Need to have only 1 reducer to calculate final top-n terms
        job.setNumReduceTasks(1);
        
        // Setting input and output paths to command line args
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        
        // Setting mapper and reducer classes
        job.setMapperClass(TopNTermsMapper.class);
        job.setReducerClass(TopNTermsReducer.class);
        
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