# Mini Search Engine Project

## **Prerequisites**

### Install Docker:
[Docker](https://www.docker.com/products/docker-desktop)

### Install X11 Framework:
Windows: I used [VcXsrv Windows X Server](https://sourceforge.net/projects/vcxsrv/), but [Xming](https://sourceforge.net/projects/xming/) should also work

MacOS: Install [XQuartz](https://www.xquartz.org/)

## **Running the program**

### Launch your X Server
Windows: Open XLaunch and start an xserver (default settings should be fine)

MacOS: Open XQuartz and start an xserver

### Pull Docker Image:
Open a Command Prompt or Terminal and enter

'docker pull shay4545/mini-search-engine'

### Run Program:
In your Command Prompt or Terminal, enter

'docker run -e DISPLAY=<YOUR_IP>:0.0 shay4545/mini-search-engine'

where <YOUR_IP> is the local IP address of your computer. 


In Windows, this can be found by entering

'ipconfig'

in the Command Prompt!

![ipconfig](https://user-images.githubusercontent.com/71043322/139515114-f02a3718-a06a-405d-816e-9f3f3d7b4c1c.PNG)

