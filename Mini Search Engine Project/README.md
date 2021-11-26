# Mini Search Engine Project
This project was developed for CS 1660 - Intro to Cloud Computing (Fall 2021) at the University of Pittsburgh

# Running the Client Side Application

## **Prerequisites**

### Assumptions:
- The user is using a Windows machine, or will follow the provided instructions if running a MacOS/Linux machine.
- The user has downloaded the files provided in this repository
- The instructions regarding installation and setup of Docker/X11 are followed, or the user has already installed and configured Docker/X11 correctly. Mac users may need to make adjustments to the instructions regarding setup of X11, as I was not able to verify the setup instructions provided below
- The user is familiar enough with GCP to use a Dataproc cluster, or will follow the instructions provided in the demo video to setup a GCP Dataproc cluster
- The user will update the GCP variables in the source code file "SearchEngineGUI.java" after creating their Dataproc cluster on GCP
- The user knows how to create JAR files with dependencies from the source code, or will follow the instructions provided in the demo video to do so
- The user knows or can locate their IP address (for Windows users, instructions are provided below)

### Install Docker:
Windows & MacOS: [Docker](https://www.docker.com/products/docker-desktop)<br/><br/>

### Install X11 Framework:
Windows: I used [VcXsrv Windows X Server](https://sourceforge.net/projects/vcxsrv/), but [Xming](https://sourceforge.net/projects/xming/) should also work

MacOS: Install [XQuartz](https://www.xquartz.org/)<br/><br/>

## **Running the Application**
Full instructions for how to run this application on your computer and what the application does are provided in the demo video. This does not include the necessary setup of Docker and X11, but instructions for how to do that are provided in this README file

### Set up your Dataproc Cluster in GCP and update the source code
Instructions for how to do this are provided in the demo video in this repository, but I will also give a high-level description here those familiar with GCP.
- Create a new project in your Google Cloud Console, call it something like "mini-search-engine"
- Ensure you have enabled billing for your newly created project
- Create a new Dataproc cluster, the default settings should be fine here
- Navigate to the storage bucket for your newly created cluster and create a folder named "Data" and a folder named "Jar"
- (Optional) Upload the files from the data folder of this repository to the data folder of your GCP bucket. This is also done by the application, so you can skip this step if you want to
- Upload three of the four files in the jar folder of this repository to the Jar folder of your GCP bucket. The ones you want to upload are "InvertedIndex.jar", "SearchIndices.jar", and "TopNTerms.jar"
- Open your downloaded version of "SearchEngineGUI.java" and replace variables listed under the comment "GCP variables" to reflect your newly created project, cluster, and storage bucket names
- Rebuild the SearchEngineGUI.jar to reflect the above changes and replace the downloaded .jar file with your newly created .jar file

### Configure your X Server
Windows: Open XLaunch and start an xserver (default settings should be fine)

MacOS: (These instructions may not be entirely accurate, as I have a Windows machine and was unable to test these commands). See [X11 in docker on macOS](https://gist.github.com/cschiewek/246a244ba23da8b9f0e7b11a68bf3285#gistcomment-3477013)<br/><br/>

### Build Docker Image:
Open a Command Prompt or Terminal, navigate to the folder where you downloaded the contents of this repository, and enter
```
docker build mini-search-engine .
```
Note: If you have already built a docker image using the name "mini-search-engine" you might want to change the name to avoid overwriting your existing docker image

### Run:
Windows: In your Command Prompt, enter
```
docker run -e DISPLAY=<YOUR_IP>:0 mini-search-engine
```

where <YOUR_IP> is the local IP address of your computer.<br/><br/>

In Windows, this can be found by entering the following in the Command Prompt:
```
ipconfig
```

![ipconfig](https://user-images.githubusercontent.com/71043322/139515114-f02a3718-a06a-405d-816e-9f3f3d7b4c1c.PNG)

MacOS: After following the instructions under "Configure your X Server" to configure X11 for MacOS, enter this command in your Terminal
```
docker run -e DISPLAY=host.docker.internal:0 shay4545/mini-search-engine
```
