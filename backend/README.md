### Requirements:
- java 17 !
- docker installed

### To start environment:

execute in terminal

1. mvn clean install 
2. docker-compose up -d

### To stop environment

- docker-compose down -v
- docker image rm backend-application:latest

### After environment started, you can check DB content using: 

http://localhost:8081/db/skoda/

### Credentials:
- USERNAME: user
- PASSWORD: password

### The Application API available by the following link: 

http://localhost:8080/hackathon/swagger-ui/index.html

### Tips

For more comfortable managing all Docker staff you can use portainer.

#### To install it as Docker container:

sudo docker run -d -p 9000:9000 --name portainer --restart always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer

#### After installation will be available by the link:

http://localhost:9000/
