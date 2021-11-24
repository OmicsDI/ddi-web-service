Data Discovery Index web-service
=========================

A RESTful web service providing access to the European Bioinformatics Institute Data Discovery Index.

In-dev prototype deployed here: (soon). 

#### End points

Detailed and functional documentation will be available thanks to [Swagger](https://github.com/swagger-api) once the
web-service is deployed. As a summary, these are the available end-points for the web service:

* `stast`: statistics about the data in the DDI project. 
* `datatset`: information about datasets, such as search, terms frequency etc.



Currently kubernetes setup is in dev branch 

CI/CD process for Omicsdi Docker deployement on kubernetes :- 


CI/CD process is followed for OmicsDI development using docker on kubernetes.

In OmicsDI, we separated our web services into multiple different services, including api web service, profile service, frontend web service, server side rendering web service, etc. 

We also have multiple different environments for production as well as testing. 

So, to make easier to apply new changes as well as separate the development and deployment, we have implemented our Omicsdi continuous delivery service. 

Source code of this service is located at https://github.com/OmicsDI/ddi-continuous-delivery. 


The flow of this service is depicted as below:

![Image of Omicsdi CI/CD](https://github.com/OmicsDI/ddi-web-service/blob/master/OmicsDI-CD.jpg)

As you can see in the flow chart above, every time the user merges a pull request from one branch to another, a web hook will be triggered to call our Continuous Delivery web service. 

The web service will validate the request and then will submit a build job into our job queue (Redis). 

A CD worker will then pick up the job, clone/checkout the source code from Github with the branch described from the job information, build a Docker image based on the Dockerfile in the source code root, upload the generated image into Dockerhub and then finally, redeploy the container. 

That is an overview of our CD, now we're going deep into each of those steps to see how it works and how to configure it.

Continuous Delivery Workers

Once the build job has been submitted into our Job queue, then worker will pick up the job. 

Depending on the job metadata, it will clone/checkout the git repository with the branch that is the target branch in the pull request. 

After checked out the target branch, this worker will then build docker image based on the Dockerfile that located in the git source root using the docker agent of the host Kubernetes itself (https://github.com/OmicsDI/ddi-continuous-delivery/blob/master/k8s/ddi.continuous.delivery.worker.yaml#L51). 

The build tag name will be using the format of omicsdi/<project name>:omicsdi.<branch name>.<build id>.

After the docker image is built, the generated docker image will be uploaded into Dockerhub using our omicsdi deployment account, which described in Kubernetes secret as well. 

Once deployed, it moves to the last phase of Container Redeployment.

Container Redeployment

Now, depending on the project, you may or may not need to redeploy your changes. 

Dataflow for instance, don't need to apply changes straight away because the next time it spin up a container, it will automatically pull new images from Dockerhub.

But for web service like ddi-web-service, we have to tell Kubernetes to redeploy the container. 

So, If your project need to redeploy, define the k8s folder with the Kubernetes manifest file name's format as follow: <name>.<environment>.yaml (example: https://github.com/OmicsDI/ddi-web-service/tree/dev/k8s)

If your project don't need to redeploy, do not define the k8s folder

Now, if you want to redeploy the container, you have to change the image tag described in the Kubernetes manifest, otherwise, 

Kubernetes will not recognise that something has changed and will not redeploy the container (https://github.com/kubernetes/kubernetes/issues/33664). 

So, in order to workaround this issue, we have defined the default tag name, which is omicsdi.dev.01. 

It acts as a placeholder and will be replaced each time the container is being redeployed (https://github.com/OmicsDI/ddi-web-service/blob/dev/k8s/ddi.webservice.dev.yaml#L19). 

Deploying our Continuous Delivery services stack

Normally, the CD will work with all namespaces in our Kubernetes cluster, so, I would prefer to deploy it into the default's namespace, for better organising our services. 

The following steps will help you to deploy our CD into your Kubernetes cluster:

1.  Clone the Continuous Delivery project

Kubernetes
```
git clone git@github.com:OmicsDI/ddi-continuous-delivery.git
cd ddi-continuous-delivery
```

2. Build the Docker images (Skip if you haven't changed anything in the code)

Kubernetes
```
docker build -t omicsdi/ddi-continuous-delivery:latest .
docker build -t omicsdi/ddi-continuous-delivery-worker:latest -f Dockerfile.worker .
```

3. Update your DNS

Kubernetes
```
File: ddi.continuous.delivery.ingress.yaml
Replace: host: cd.omicsdi.org -> host: <your host name or IP>
```
  
4. Deploy the services:

Kubernetes
```
kubectl apply -f k8s/ddi.redis.yaml
kubectl apply -f k8s
```






