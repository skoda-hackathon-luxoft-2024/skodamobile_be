data "azurerm_resource_group" "hackathon_subscription" {
  name = "SkodaWPH_POC"
}

output "azure_subscription_id" {
  value = data.azurerm_resource_group.hackathon_subscription.id
}