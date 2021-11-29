# Mini Search Engine Project
This project was developed for CS 1660 - Intro to Cloud Computing (Fall 2021) at the University of Pittsburgh
<br/><br/>The video files were too large to upload to GitHub so I uploaded them on YouTube and provided links to them below.
<br/><br/>[Demo](https://www.youtube.com/watch?v=eoSqYmUHYbE)
<br/>[Code Walkthrough](https://www.youtube.com/watch?v=aTAUquOjX2k)

# Running the Client Side Application

## **Prerequisites**
Becuase this application relies on a Dataproc cluster in my GCP account to perform Hadoop MapReduce jobs, anyone who wishes to run this application on their own computer must configure the application to contact their own Dataproc cluster on GCP. Instructions on how to do this are provided in detail in the demo video linked above, and higher-level instructions for those familiar with GCP are provided below. 

### Assumptions:
- The user is using a Windows machine, or will follow the provided instructions if running a MacOS/Linux machine
- The words "this repository" refer to the folder containing this README file (Mini Search Engine Project)
- The user has downloaded the files provided in this repository
- The instructions regarding installation and setup of Docker/X11/Eclipse are followed, or the user has already installed and configured Docker/X11/Eclipse correctly.
- The user is familiar enough with GCP to use a Dataproc cluster, or will follow the instructions provided in the demo video to setup a GCP Dataproc cluster and configure the application to contact their cluster
- For Windows, the user knows their IP or will follow the instructions provided below to retrieve their IP address

### Install Docker:
Windows & MacOS: [Docker](https://www.docker.com/products/docker-desktop)<br/>
<br/>You may need to run Docker once before your Command Prompt or Terminal recognizes 'docker' commands 

### Install X11 Framework:
Windows: I used [VcXsrv Windows X Server](https://sourceforge.net/projects/vcxsrv/), but [Xming](https://sourceforge.net/projects/xming/) should also work

MacOS: Install [XQuartz](https://www.xquartz.org/)<br/><br/>

### Install Eclipse IDE:
Windows & MacOS: [Eclipse IDE](https://www.eclipse.org/downloads/)
<br/>Be sure to select the version for java projects/developers after downloading the installer.
<br/>You may also need to install and configure JDK8 on your computer and in eclipse if you run into any issues when trying to compile or export SearchEngineGUI.java, although this may not be necessary. You should also be able to change the settings of the imported maven project in eclipse to use the version of java you have installed.

## **Running the Application**

### Configure your X Server:
Windows: Open XLaunch and start an xserver (default settings should be fine)

MacOS: Open XQuartz, and select Preferences under the XQuartz menu. Go to the security tab and ensure "Allow connections from network clients" is checked. Then restart XQuartz. In a terminal on the host, run 
```
xhost +localhost
```

### Connecting the client to GCP:
Instructions for how to do this are provided in the demo video linked above, but I will also give a high-level description here for those familiar with GCP.
- Create a new project in your Google Cloud Console, call it something like "mini-search-engine"
- Ensure you have enabled billing for your newly created project
- Create a new Dataproc cluster, name it whatever you want, and the default settings should be fine
- Navigate to the storage bucket for your newly created cluster and create a folder named "Data" and a folder named "Jar"
- (Optional) Upload the files from the data folder of this repository to the data folder of your GCP bucket. This is also done by the application when selecting files, so you can skip this step if you want to
- Upload the files in the Jar folder of this repository to the Jar folder of your GCP bucket.
- Open the folder "SearchEngineGUI" from this repository as a Maven project in Eclipse. After that, open the file "SearchEngineGUI.java" and initialize the variables listed under the comment "GCP variables" to reflect your newly created project **id**, cluster name, storage bucket name, and region
- Run the "SearchEngineGUI.java" program once as a java application in eclipse and then close it so that eclipse recongnizes the main() method for the next step
- Export "SearchEngineGUI.java" into a runnable .jar file with packaged dependencies named "SearchEngineGUI" and place the newly created jar file into the Jar folder where you downloaded the repository
- Follow the steps provided under the "Creating a service account" header at [Getting started with authentication](https://cloud.google.com/docs/authentication/getting-started#cloud-console) to set up a service account and create a service account key
- After downloading the .json file from the above step, rename it to "projectJSON" and place it in the folder containing the contents of this repository (Mini Search Engine Project). The project should now be ready to run on your computer by following the below instructions

### Build Docker Image:
Open a Command Prompt or Terminal, navigate to the place where you downloaded this repository, and enter
```
docker build -t mini-search-engine .
```
Note: If you have already built a docker image using the name "mini-search-engine" you might want to change the name to avoid overwriting your existing docker image. Make sure to use the name of the new docker image you built for the below commands if you decided to use a different name than the one provided

### Run:
Windows: In your Command Prompt, enter
```
docker run -e DISPLAY=<YOUR_IP>:0 mini-search-engine
```

where <YOUR_IP> is the local IP address of your computer.<br/><br/>

This can be found by entering the following in the Command Prompt:
```
ipconfig
```

![ipconfig](https://user-images.githubusercontent.com/71043322/139515114-f02a3718-a06a-405d-816e-9f3f3d7b4c1c.PNG)

MacOS: In your Terminal, enter
```
docker run -e DISPLAY=host.docker.internal:0 mini-search-engine
```
