terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=3.0.0"
    }
    azuredevops = {
      source = "microsoft/azuredevops"
      version = ">=0.1.0"
    }
  }

  backend "azurerm" {
    resource_group_name  = "SkodaWPH_POC"
    storage_account_name = "skodahackathon2022"
    container_name       = "state"
    key                  = "infrastructure/terraform.tfstate"
  }

}

provider "azurerm" {
  features {}
}
