data "azurerm_container_registry" "registry" {
  name                = var.container_registry
  resource_group_name = data.azurerm_resource_group.hackathon_subscription.name
}

resource "azurerm_container_group" "container_instance_frontend" {
  name                = replace(var.application, "-", "")
  resource_group_name = data.azurerm_resource_group.hackathon_subscription.name
  location            = data.azurerm_resource_group.hackathon_subscription.location
  ip_address_type     = "Public"
  dns_name_label      = var.application
  os_type             = "Linux"

  image_registry_credential {
    username = data.azurerm_container_registry.registry.admin_username
    password = data.azurerm_container_registry.registry.admin_password
    server   = data.azurerm_container_registry.registry.login_server
  }

  container {
    name   = "application"
    image  = var.image
    cpu    = "0.1"
    memory = "0.2"

    # commands = ["while true; do sleep 12; echo 'tick'; done;"]

    environment_variables = {
      some = "variable"
    }

    ports {
      port     = var.port
      protocol = "TCP"
    }
  }

  tags = {
    Environment = var.environment
    Project = var.project
    Application = var.application
  }
}

output "container_instance_frontend_ip" {
  value = azurerm_container_group.container_instance_frontend.ip_address
}