# some variables we use
PORT = 80
CONTAINER_REGISTRY = skodahackathon2022.azurecr.io
PLATFORM = linux/amd64,linux/arm64
DOCKER_BUILDER = hackathon_builder
AZURE_RESOURCE_GROUP = SkodaWPH_POC
AZURE_TERRAFORM_STORAGE_ACCOUNT = skodahackathon2022
AZURE_TERRAFORM_STORAGE_CONTAINER = state

# .DEFAULT_GOAL :=deploy
# .PHONY : docker.create_builder docker.stop_builder

docker.create_builder:
	docker buildx create --name $(DOCKER_BUILDER)
	docker buildx use hackathon_builder

docker.stop_builder:
	docker buildx rm --builder $(DOCKER_BUILDER)
	docker buildx stop

deploy.frontend::
	$(eval VER := $(shell git log -1 --pretty=format:"%H"))
	docker buildx use $(DOCKER_BUILDER)
	cd frontend && \
		docker buildx build -t "$(CONTAINER_REGISTRY)/frontend:$(VER)" --platform linux/amd64,linux/arm64 --push .
		cd deployment && \
		terraform init && \
		TF_VAR_container_registry=$(CONTAINER_REGISTRY) TF_VAR_port=$(PORT) TF_VAR_image=$(CONTAINER_REGISTRY)/frontend:$(VER) terraform apply -auto-approve
	docker buildx stop --name $(DOCKER_BUILDER)

deploy.backend:
	$(eval VER := $(shell git log -1 --pretty=format:"%H"))
	docker buildx use $(DOCKER_BUILDER)
	cd backend && \
		docker buildx build -t "$(CONTAINER_REGISTRY)/backend:$(VER)" --platform linux/amd64,linux/arm64 --push . && \
		cd deployment && \
		terraform init && \
		TF_VAR_container_registry=$(CONTAINER_REGISTRY) TF_VAR_port=$(PORT) TF_VAR_image=$(CONTAINER_REGISTRY)/backend:$(VER) terraform apply -auto-approve
	docker buildx stop --name $(DOCKER_BUILDER)

deploy: deploy.backend deploy.frontend

deploy.delete:
	cd backend/deployment && terraform destroy -auto-approve
	cd frontend/deployment && terraform destroy -auto-approve
	cd infrastructure && terraform destroy -auto-approve

terraform.init:
	cd backend/deployment && terraform init
	cd frontend/deployment && terraform init
	cd infrastructure && terraform init

terraform.prepare:
	az storage account create --resource-group $(AZURE_RESOURCE_GROUP) --name $(AZURE_TERRAFORM_STORAGE_ACCOUNT) --sku Standard_LRS --allow-blob-public-access false
	az storage container create --name $(AZURE_TERRAFORM_STORAGE_CONTAINER) --account-name $(AZURE_TERRAFORM_STORAGE_ACCOUNT)

terraform.cleanup:
	az storage container delete --name $(AZURE_TERRAFORM_STORAGE_CONTAINER) --account-name $(AZURE_TERRAFORM_STORAGE_ACCOUNT)
	az storage account delete --resource-group $(AZURE_RESOURCE_GROUP) --name $(AZURE_TERRAFORM_STORAGE_ACCOUNT)