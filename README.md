### Requirements:
- java 17 !
- docker installed

### To start environment:

execute in terminal

1. mvn clean install 
2. docker-compose up -d

### To stop environment

- docker-compose down -v
- docker image rm porschemdm_application:latest

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


## Calls

1. authenticate IVI device:
```sh
curl --location --request POST 'http://localhost:8080/api/ivi/login/vin_device_id'
```

2. get pairing number on IVI device:
```sh 
curl --location 'http://localhost:8080/api/ivi/number' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aW5fZGV2aWNlX2lkIiwiaWF0IjoxNzI4NzQ1Njk0fQ.Nbl-DIaRY0dAJ3nChGaTmlXoXzjXE_w6_7nnEvY7dcnRho3mqu7eOZ7Wdu8mZKsB7srJroPCstD2L-eEsYAzyw'
```

3. authenticate Mobile device:
```sh
curl --location 'http://localhost:8080/api/mobile/login' \
   --header 'Content-Type: application/json' \
   --data '{
   "username":"username1",
   "password":"password1"
   }'
```

4. on IVI get list of paired Mobile devices

```sh
curl --location 'http://localhost:8080/api/ivi/paired' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aW5fZGV2aWNlX2lkIiwiaWF0IjoxNzI4NzQ1Njk0fQ.Nbl-DIaRY0dAJ3nChGaTmlXoXzjXE_w6_7nnEvY7dcnRho3mqu7eOZ7Wdu8mZKsB7srJroPCstD2L-eEsYAzyw'
```

5. pairing Mobile with IVI:
```sh
curl --location --request POST 'http://localhost:8080/api/mobile/pair?paringNumber=360615' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZTEiLCJpYXQiOjE3Mjg3NDU3ODB9.bA65kNVM5oe0zvwCv3LJcQwIuk_MckYHOFfh37hbYtV4pi_MaI8C9GG6Bt7hVZKfqjtD65Sh2lO_aWA32fB0sA'
```

6. update settings by Mobile or IVI:
```sh
curl --location 'http://localhost:8080/api/settings' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aW5fZGV2aWNlX2lkIiwiaWF0IjoxNzI4NzQ1Njk0fQ.Nbl-DIaRY0dAJ3nChGaTmlXoXzjXE_w6_7nnEvY7dcnRho3mqu7eOZ7Wdu8mZKsB7srJroPCstD2L-eEsYAzyw' \
   --data '{
   "temperature": 1234
   }'
```

7. getting settings:
```sh
curl --location 'http://localhost:8080/api/settings' \
   --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZTEiLCJpYXQiOjE3Mjg3NDU3ODB9.bA65kNVM5oe0zvwCv3LJcQwIuk_MckYHOFfh37hbYtV4pi_MaI8C9GG6Bt7hVZKfqjtD65Sh2lO_aWA32fB0sA'
```


## azure 


docker-compose build   --push
docker buildx build --platform linux/amd64 --push -t hackaton2024.azurecr.io/app/app .
ssh azureuser@20.67.250.76
docker-compose up -d