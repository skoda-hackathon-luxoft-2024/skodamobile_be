# resource "azurerm_storage_account" "state" {
#   name                     = "${var.application}state"
#   resource_group_name      = var.resource_group_name
#   location                 = var.resource_group_location
#   account_tier             = "Standard"
#   account_replication_type = "LRS"
#   allow_nested_items_to_be_public = false

#   tags = {
#     Environment = var.environment
#     Project = var.project
#     Application = "${var.application}"
#   }
# }

# resource "azurerm_storage_container" "example" {
#   name                  = "${var.application}statecontainer"
#   storage_account_name  = azurerm_storage_account.state.name
#   container_access_type = "private"
# }