terraform {
  required_version = ">= 1.2.1"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=3.0.0"
    }
  }

  backend "azurerm" {
    resource_group_name  = "SkodaWPH_POC"
    storage_account_name = "skodahackathon2022"
    container_name       = "state"
    key                  = "backend/terraform.tfstate"
  }
}

provider "azurerm" {
  features {}
}