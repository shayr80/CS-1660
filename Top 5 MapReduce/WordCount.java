import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
    public static class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {
    	
    	private Text currWord;
    	private IntWritable count;
    	private List<String> commonWords = Arrays.asList("the", "and", "of", "to", "a", "in", "he", "that", "i", "his", "was",
    				"you", "it", "with", "is", "not", "had", "her", "for", "him", "as", "but", "my", "at", "on",
    				"this", "be", "she", "have", "which", "me", "all", "by", "so", "what", "your", "one", "said",
    				"no", "they", "will", "are", "were", "there", "who", "an", "do", "when", "if", "we", "would",
    				"or", "their", "them", "more", "now", "did", "been", "then", "out", "like", "our", "well", "good",
    				"come", "how", "than", "am", "into", "some", "could", "here", "see", "say", "very", "these", "only",
    				"its", "should", "must", "let", "about", "us", "has", "can", "may", "too", "those", "from");
    	
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    		currWord = new Text();
    		count = new IntWritable(1);
    		
    		// Getting array of words by replacing each character not in a-z or whitespace to an empty character and splitting by any amount of whitespace
    		String[] words = value.toString().toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
            
        	// Writing each word with the file it comes from
        	for(String word : words) {
        		if(!commonWords.contains(word) && !word.isEmpty() && word.length() > 1) {
        			currWord.set(word);
            		context.write(currWord, count);
        		}
        	}
        }
    }

    public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    	
    	@Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
    		int count = 0;
            
    		// For each value (occurrence) of a certain term, increase its count by the stored value (which will always be 1)
            for(IntWritable value : values) {
            	count += value.get();
            }
            
            // Writing total count of each term
            context.write(key, new IntWritable(count));
        }
    }

    public static void main(String[] args) throws Exception {
    	// Require that user passes in an input and output path
        if(args.length != 2) {
        	System.err.println("Usage: WordCount <input path> <output path>");
        	System.exit(-1);
        }
        
        // Configuring job
        Job job = new Job();
        job.setJobName("WordCount HW 5");
        job.setJarByClass(WordCount.class);
        
        // Adding recursive setting to allow hadoop to loop through all subfolders of inputted data folders
        FileInputFormat.setInputDirRecursive(job, true);
        
        // Setting input and output paths to command line args
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        // Setting mapper and reducer classes
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        
        // Setting output classes
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        // Waiting for job to complete, then exit
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}