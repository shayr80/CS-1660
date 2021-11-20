import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SearchIndices {
	
	public static String searchTerm;
	
    public static class SearchIndicesMapper extends Mapper<Object, Text, Text, Text> {
    	
    	// Get search term from configuration
    	@Override
    	public void setup(Context context) throws IOException, InterruptedException {
    		searchTerm = context.getConfiguration().get("searchTerm");
    	}
    	
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    		// Split line with search term into the term and the fileNames with their corresponding counts
        	String[] termAndFiles = value.toString().split("\t");
    		String termToSearch = termAndFiles[0];
    		String fileNamesAndCounts = termAndFiles[1];
    		
    		// require that the term exists and matches the term inputed by the user
    		if(termToSearch.equals(searchTerm)) {
    			context.write(new Text(termToSearch), new Text(fileNamesAndCounts));
    		}
        }
    }

    public static class SearchIndicesReducer extends Reducer<Text, Text, Text, Text> {
    	
        public void reduce(Text key, Text value, Context context) throws IOException, InterruptedException {
    		// require term matches the term inputed by the user
        	if(key.toString().equals(searchTerm)) {
    			context.write(key, value);
    		}
    	}
    }

    public static void main(String[] args) throws Exception {
    	// Require that user passes in a term, an input path, and an output path
        if(args.length != 3) {
        	System.err.println("Usage: SearchIndices <term to search> <input path> <output path>");
        	System.exit(-1);
        }
        
        // Configuring job, setting "searchTerm" value to command line arg term passed in by user
        Configuration c = new Configuration();
        c.set("searchTerm", args[0]);
        Job job = Job.getInstance(c, "Search For Term: " + args[0]);
        job.setJar("SearchIndices.jar");
        
        // Only need 1 reducer since all we are doing is checking a single term
        job.setNumReduceTasks(1);
        
        // Setting input and output paths to command line args
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        
        // Setting mapper and reducer classes
        job.setMapperClass(SearchIndicesMapper.class);
        job.setReducerClass(SearchIndicesReducer.class);
        
        // Setting output classes
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        // Waiting for job to complete, then exit
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}