## HW 5 - "Top-5" Algorithm on the Cloud

The files in this folder are for HW 5 of CS 1660 - Intro to Cloud Computing, Fall 2021
<br/><br/>
I opted to run my algorithm on a Dataproc cluster on GCP since I was familiar with it from the term project. The files included above are as follows:
- ExecutionOnCluster.PNG : Image of the top-5 terms job executing on the dataproc cluster.
- ExecutionOnCluster2.PNG : Image of the top-5 terms job executing on the dataproc cluster. A second image was needed to capture the rest of the job output.
- GCP Account.PNG : Image of my cloud account (including name)
- Top5Terms.java : MapReduce code I ran to get the top 5 most frequent terms from the data files provided.
- WordCount.java : MapReduce code I ran to get the count of each word from the data files provided. The output of this job was used as the input for the top-5 terms job.
- top5Terms : File containing the output of the top-5 terms job executed on the Dataproc cluster. The output is in the format: frequency  term
