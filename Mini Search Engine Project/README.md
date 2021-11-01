# Mini Search Engine Project
This project was developed for CS 1660 - Intro to Cloud Computing (Fall 2021) at the University of Pittsburgh

## **Plan to Connect to GCP**

### Authentication
I will follow the instructions for [Getting started with authentication](https://cloud.google.com/docs/authentication/getting-started#cloud-console)<br/><br/>

After getting the .json file with credentials, I will add an instruction to my Dockerfile to set the .json file as an environment variable called GOOGLE_APPLICATION_CREDENTIALS and rebuild my docker image<br/><br/>

### Contacting GCP from Client Application
- I will create a dataproc cluster in GCP
- I will import the Google Cloud Client Libraries necessary for submitting hadoop jobs to the cluster into my client application
- I will write code to calculate inverted indices, search for terms, and calculate top-n frequent terms from inputted files
- I will add code to my client application to check if data files are in staging bucket, and add them if they don't already exist
- I will add code to my client application to submit hadoop job(s) to GCP when applicable inputs are given and buttons are pressed
- I will add code to my client application to parse the output of these hadoop jobs to display readable results to the user

# Running the Client Side Application

## **Prerequisites**

### Install Docker:
Windows & MacOS: [Docker](https://www.docker.com/products/docker-desktop)<br/><br/>

### Install X11 Framework:
Windows: I used [VcXsrv Windows X Server](https://sourceforge.net/projects/vcxsrv/), but [Xming](https://sourceforge.net/projects/xming/) should also work

MacOS: Install [XQuartz](https://www.xquartz.org/)<br/><br/>

## **Running the Application**

### Launch your X Server
Windows: Open XLaunch and start an xserver (default settings should be fine)

MacOS: Open XQuartz and start an xserver. Additional steps may be necessary to configure the application for Mac users. See [here](https://gist.github.com/cschiewek/246a244ba23da8b9f0e7b11a68bf3285)<br/><br/>

### Pull Docker Image:
Open a Command Prompt or Terminal and enter
```
docker pull shay4545/mini-search-engine
```

### Run:
In your Command Prompt or Terminal, enter
```
docker run -e DISPLAY=<YOUR_IP>:0.0 shay4545/mini-search-engine
```

where <YOUR_IP> is the local IP address of your computer.<br/><br/>

In Windows, this can be found by entering the following in the Command Prompt:
```
ipconfig
```

![ipconfig](https://user-images.githubusercontent.com/71043322/139515114-f02a3718-a06a-405d-816e-9f3f3d7b4c1c.PNG)

