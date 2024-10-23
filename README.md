# Skoda Hackathon 2022

some text describing the task and appliction 

## Development

This project comes with a `docker-compose stack` it allows to run the application independent from the developer operating system. It shares the respective folders with the docker container so that a hot reload is possible for the frontend. 
backend is to be defined, not sure if thats possible at all with maven.

It will expose the: 

- frontend: http://localhost:3000
- backend: http://localhost:8080

To start the stack you'll need to have docker desktop installed and ready to be used. 

```sh
# starting the stack
docker compose up 

# stopping the stack
docker compose down 
```

## Deployment

The Repo is structured in three parts namely `frontend`, `backend` and `infrastructure`.
All three parts are deployable seperately and are independet except for the data contracts established through the REST API

> **General Note**
> The container images alway have the current `git sha` as a tag. So if you try to deploy changes you will need to have a new hash. Simply add a new commit, you do not need to push though

### terraform installation

I recommend to use https://github.com/tfutils/tfenv it allows you to switch between different versions of terraform, which used to be pain in the a.....

#### Mac

```shell
brew install tfenv
# source {your-shell file here}
tfenv install 1.2.1
tfenv use 1.2.1
```

#### Ubuntu

https://www.terraform.io/cli/install/apt  

```sh
wget -qO - terraform.gpg https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/terraform-archive-keyring.gpg
sudo echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/terraform-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" > /etc/apt/sources.list.d/terraform.list
sudo apt update
sudo apt install terraform
```

#### Bootstrapping

You need to init all terraform stacks.
You'll need be authenticated with Azure and after that just call

```sh
az login
# this will open a browser where you need to login with your DSC email.
# Once you're loggedin successfully you can continue and init the terraform stacks
make terraform.init
```

### Infrastructure

Contains all parts of basic infrastructure, such as blob storage, container registries etc.
It is applied and managed through terraform (version 1.2.1) 

Deploying changes works through 

```sh
cd ./infrastructure
terraform apply
```

### Frontend

**URL** http://skoda-hackathon-2022-frontend.westeurope.azurecontainer.io  

Contains everything frontend related. There is a specific readme in the folder describing the development environment and such
It is being deployed as a container to azure `container_group` the deployment itself is done through terraform but there's a handy make target

```sh
make docker.create_builder
make deploy.frontend
make docker.stop_builder
```

that will ensure to create the container pushing it to the registry and applying terraform with the correct variables.  
  
You will need to be logged in to the container registry upfront starting the deployment process, which can be done via 
```sh
az acr login --name skodahackathon2022
```

You can access the UI at 

### Backend

**URL** http://skoda-hackathon-2022-backend.westeurope.azurecontainer.io/swagger-ui/index.html

Contains everything backend related. Its based on Java and build using Maven.  
It is being deployed as a container to azure `container_group` the deployment itself is done through terraform but there's a handy make target

```sh
make docker.create_builder
make deploy.backend
make docker.stop_builder
```

that will ensure to create the container pushing it to the registry and applying terraform with the correct variables.  
  
You will need to be logged in to the container registry upfront starting the deployment process, which can be done via 

```sh
az acr login --name skodahackathon2022
```
  
## Cleanup

Things to do after the Hackathon.

```
make deploy.delete # delete any deployments and resources
make terraform.cleanup # Cleanup terraform state in storage account
```