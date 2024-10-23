resource "azurerm_container_registry" "container_registry_backend" {
  name                = replace(var.application, "_", "")
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  sku                 = "Standard"
  admin_enabled       = true
}

output "admin_username" {
  value = azurerm_container_registry.container_registry_backend.admin_username
}

output "admin_password" {
  value = azurerm_container_registry.container_registry_backend.admin_password
}

output "login_server" {
  value = azurerm_container_registry.container_registry_backend.login_server
}

