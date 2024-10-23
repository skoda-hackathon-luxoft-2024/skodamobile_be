data "azurerm_resource_group" "hackathon_subscription" {
  name = "SkodaWPH_POC"
}

output "azure_subscription_id" {
  value = data.azurerm_resource_group.hackathon_subscription.id
}

module "definitions" {
  source = "./definitions"
  resource_group_name = data.azurerm_resource_group.hackathon_subscription.name
  resource_group_id = data.azurerm_resource_group.hackathon_subscription.id
  resource_group_location = data.azurerm_resource_group.hackathon_subscription.location

  environment = "hackathon"
  project = "skoda_hackathon_2022"
  application = "skoda_hackathon_2022"
}