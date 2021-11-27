import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndex {
    public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
    	
    	private Text fileName;
    	private Text currWord;
    	private List<String> commonWords = Arrays.asList("the", "and", "of", "to", "a", "in", "he", "that", "i", "his", "was",
    				"you", "it", "with", "is", "not", "had", "her", "for", "him", "as", "but", "my", "at", "on",
    				"this", "be", "she", "have", "which", "me", "all", "by", "so", "what", "your", "one", "said",
    				"no", "they", "will", "are", "were", "there", "who", "an", "do", "when", "if", "we", "would",
    				"or", "their", "them", "more", "now", "did", "been", "then", "out", "like", "our", "well", "good",
    				"come", "how", "than", "am", "into", "some", "could", "here", "see", "say", "very", "these", "only",
    				"its", "should", "must", "let", "about", "us", "has", "can", "may", "too", "those");
    	
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
    		// Getting file path and splitting by "/" to get each portion of path. 
    		String[] filePath = ((FileSplit) context.getInputSplit()).getPath().getName().split("/");
    
    		String fileNameString;
    		
    		// Ensuring that we don't set off an ArrayOutOfBounds Exception if filePath.length is 0 for some reason
    		if(filePath.length > 0) {
    			// Getting current file name using the part of the file path after the last "/"
    			fileNameString = filePath[filePath.length - 1];
    		} else {
    			fileNameString = "FileNotFound";
    		}
    		
    		fileName = new Text();
    		fileName.set(fileNameString);
    		currWord = new Text();
        	
    		// Getting array of words by replacing each character not in a-zA-Z or whitespace to a whitespace character, 
    		// then setting all text to lowercase and splitting by whitespace
    		// Note replacing with whitespace results in words with apostrophes being separated into two words, while
    		// replacing with empty string results in multiple words being merged into one word on some occasions.
    		
        	String[] words = value.toString().toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
            
        	// Writing each word with the file it comes from
        	for(String word : words) {
        		if(!commonWords.contains(word)) {
        			currWord.set(word);
            		if(!word.isEmpty() && !word.equals(" ") && word.length() > 1) {
                		context.write(currWord, fileName);
            		}
        		}
        	}
        }
    }

    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
    	
    	@Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // Using hashmap to store frequencies for each word
    		HashMap<String, Integer> map = new HashMap<String, Integer>();
            
    		// For each value, add it to the map, or increase its frequency by 1 if it already exists in the map
            for(Text val : values) {
            	if(!map.containsKey(val.toString())) {
            		map.put(val.toString(), 1);
            	} else {
            		map.replace(val.toString(), map.get(val.toString()) + 1);
            	}
            }
            
            // For each file that the word exists in, add a string in the format "filename:wordfrequency "
            StringBuilder fileValList = new StringBuilder();
            for(String file : map.keySet()) {
            	fileValList.append(file + ":" + map.get(file) + " ");
            }
            context.write(key, new Text(fileValList.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
    	// Require that user passes in an input and output path
        if(args.length != 2) {
        	System.err.println("Usage: InvertedIndex <input path> <output path>");
        	System.exit(-1);
        }
        
        // Configuring job
        Job job = new Job();
        job.setJobName("Inverted Indices for Project");
        job.setJar("InvertedIndex.jar");
        
        // Adding recursive setting to allow hadoop to loop through all subfolders of inputted data folder
        // This job will still work when inputting archived files, but it will not be able to detect the names of subfolders unless the input
        // is unzipped, so I opted to input the unzipped folder so that word frequencies could be found for each file within the archive rather than
        // frequencies for the entire archive itself.
        FileInputFormat.setInputDirRecursive(job, true);
        
        // Setting input and output paths to command line args
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        // Setting mapper and reducer classes
        job.setMapperClass(InvertedIndexMapper.class);
        job.setReducerClass(InvertedIndexReducer.class);
        
        // Setting output classes
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        // Waiting for job to complete, then exit
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}