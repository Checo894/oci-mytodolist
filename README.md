# oci-react-samples
A repository for full stack Cloud Native applications with a React JS frontend and various backends (Java, Python, DotNet, and so on) on the Oracle Cloud Infrastructure.

![image](https://user-images.githubusercontent.com/7783295/116454396-cbfb7a00-a814-11eb-8196-ba2113858e8b.png)
  

## MyToDo React JS
The `mtdrworkshop` repository hosts the materiald (code, scripts and instructions) for building and deploying Cloud Native Application using a Java/Helidon backend


### Requirements
The lab executes scripts that require the following software to run properly: (These are already installed on and included with the OCI Cloud Shell)
* oci-cli
* python 2.7^
* terraform
* kubectl
* mvn (maven) 

## Local Build and Deployment

Follow these steps to clone, build, and run the project locally using Docker:

#### 1. Clone the repository  
```bash
git clone <your-repository-url>
cd <your-project-folder>
```
#### 2. Build the Docker image
Make sure you're in the root directory of the project where the Dockerfile is located.
```bash
cd oci-mytodolist\MtdrSpring\backend 
```
execute
```bash
docker build --platform linux/amd64 -t agileimage:0.1 .
```
#### 3. Run the Docker container
```bash
docker run --name agileContainer -p 8080:8080 -d agileimage:0.1

```
####  4. Access the application
Open your browser and go to: http://localhost:8080

#### Notes:

You should see the container named agileContainer running in Docker Desktop, with port 8080 mapped to 8080.
The container includes the necessary Oracle Wallet files for secure connection to the Oracle Cloud Database.
Ensure the Oracle Cloud Database instance is running.

## Expect more ...


