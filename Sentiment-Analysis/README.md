The below instructions outline my attempt to get the sentiment analysis application to work on Google Kubernetes Engine. I could get the application to work correctly on my local machine using docker containers and using kubernetes from my local terminal, but I could not seem to get the application to work properly after uploading everything to GCP. Accordingly, I will describe the steps I took and the errors I ran into when deploying to GKE.

1) I cloned the sentiment analysis application from https://github.com/rinormaloku/k8s-mastery to my local machine.
2) I followed the directions specified in the first 4 entries of https://rinormaloku.com/series/kubernetes-and-everything-else/ to setup the application for my local machine

After testing that the application worked successfully on my local machine, I then created docker containers for each of the services by doing the following:
1) I navigated to the sa-frontend folder in the terminal and built the docker image under shay4545/sentiment-analysis-frontend and pushed it to my Docker hub
2) I navigated to the sa-webapp folder in the terminal and built the docker image under shay4545/sentiment-analysis-web-app and pushed it to my Docker hub
3) I navigated to the sa-logic folder in the terminal and built the docker image under shay4545/sentiment-analysis-logic and pushed it to my Docker hub

After containerizing the services, I followed the rest of the tutorial at https://rinormaloku.com/series/kubernetes-and-everything-else/ to get the application up and running succesfully using kubernetes.

After ensuring the application worked using kubernetes, I then attempted to run the application on GKE by doing the following:
1) I opened my Google Cloud Console and created a new project with ID "sentiment-analysis-329401" and enabled billing, the Container Registry API, and the GKE API.
2) In the Cloud shell, I pulled the Docker images from my Docker hub using commands like "docker pull shay4545/sentiment-analysis-logic"
3) After pulling the images for sa-logic and sa-web-app, I tagged them using commands like "docker tag shay4545/sentiment-analysis-logic gcr.io/sentiment-analysis-329401/sentiment-analysis-logic" in the Cloud shell
4) After tagging the images for sa-logic and sa-web-app, I pushed them to my container registry using commands like "docker push gcr.io/sentiment-analysis-329401/sentiment-analysis-logic" in the Cloud shell

Note that I did not yet add the image shay4545/sentiment-analysis-frontend to my container registry becuase the image will need to be rebuilt using the external IP of the deployed sa-web-app service in order to function correctly.

5) After creating a new standard cluster in GKE and running "gcloud container clusters get-credentials sentiment-analysis-cluster", I created deployments called sa-logic and sa-web-app using the images from my container registry using commands like "kubectl create deployment sa-logic --image=gcr.io/sentiment-analysis-329401/sentiment-analysis-logic"
6) After creating the deployments, I examined the YAML file for sa-web-app and edited it to include: 
        env:
          - name: SA_LOGIC_API_URL
            value: "http://sa-logic"
7) After editing the YAML file, I created a service for sa-logic using the command "kubectl expose deployment sa-logic --port 80 --target-port 5000
8) I then created the service for sa-web-app using the command "kubectl expose deployment sa-web-app --type LoadBalancer --port 80 --target-port 8080
9) Once the service for sa-web-app had been created, I used the command "kubectl get service sa-web-app" to examine the external IP of the service
10) I copied the external IP and edited the App.js file in the sa-frontend folder on my local machine to fetch 'http://[external IP]/sentiment'
11) I rebuilt the sa-frontend build folder using the command 'npm run build' in my local terminal
12) I used the docker build command to rebuild the image as shay4545/sentiment-analysis-frontend:gke
13) I pushed the new docker image to my Docker hub
14) In the Cloud shell, I used the docker pull, tag and push commands to get the new image into my container registry under gcr.io/sentiment-analysis-329401/sentiment-analysis-frontend:gke
15) I typed to following command into the cloud shell to crete a deployment for the new frontend image: "kubectl create deployment sa-frontend --image=gcr.io/sentiment-analysis-329401/sentiment-analysis-frontend:gke"
16) I created the service for the application frontend using the command "kubectl expose deployment sa-frontend --type LoadBalancer --port 80 --target-port 80
17) I used the command "kubectl get service sa-frontend" to get the external IP of the frontend service and opened a new tab and navigated to the external IP
18) I typed a sentence into the sentiment analyser and pressed the button to analyse it.
19) Nothing happened, so I inspected the web page and got the following error message from the console when attempting to send anything to be analysed: "Cross-Origin Request Blocked: The Same Origin Policy disallows reading the remote resource at http://[external IP of web-app service]/sentiment 

I spent hours and tried various methods to resolve this error, but I could not get the application to work correctly on GKE no matter what I seemed to do.
