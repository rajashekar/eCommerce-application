# eCommerce Application

This Project is  done as part of Udacity Java developer course. The purpose of the project is to understand the concepts of JWT authentication and authorization so users can only access their data, and that data can only be accessed in a secure way, logging using slf4j (Simple Logging Facade for Java), unit testing, indexing logs in splunk, configuring jenkins for CI/CD. 

## Pre-requisites
- JDK 1.8
- Maven 3.6.1 - https://maven.apache.org/download.cgi
- Docker and docker-compose

## Getting Started

### Clone the repository
```
https://github.com/rajashekar/eCommerce-application.git
cd eCommerce-application
```

### Testing the ecommerce application
Start using Maven
```
mvn spring-boot:run
```

OR 

Run using docker
```
mvn clean package
docker build -t ecommerce-app .
docker run -p 8080:8080 -t ecommerce-app
```

OR 

Run using docker-compose
```
mvn clean deploy
docker-compose up -d 
```

Note 1: `mvn clean deploy` will run build, tests, package, build image and deploy image to registry like docker hub. <br>
Note 2: ~/.m2/settings-security.xml will have master password settings, which is generated using `mvn --encrypt-master-password`
```
<settingsSecurity>
    <master>{rXXXXXX=}</master>
</settingsSecurity>
```
Note 3 : ~/.m2/settings.xml will have registry details to where you want to push for example docker hub. Password is encrypted using `mvn --encrypt-password`
```
   <server>
        <id>registry.hub.docker.com</id>
        <username>dockerlogin</username>
        <password>{rYYYYYYYY=}</password>
        <configuration>
            <email>myemail@gmail.com</email>
        </configuration>
    </server>
```

if you are running on Mac, spotify dockerfile maven plugin might complain like below
```
Request error: GET unix://localhost:80/version: 503, body: <html>
```
Try setting docker_host like below and try `mvn clean deploy` again. 
```
export DOCKER_HOST=unix:///var/run/docker.sock
```

To verify test cases
```
mvn clean test
```
Current coverage is 75%
<img src='img/coverage.png'>


### Deploying all apps using docker-compose
Deploying ecommerce application, splunk, jenkins. 
If you want to do only in local just do `docker-compose up -d`
which will run ecommerce application, jenkins, splunk and splunk forwarder all at once.

If you want to deploy in AWS you can follow below steps
in AWS, take any free tier with docker support
<img src='img/aws_free_tier.png'>

Please make sure you open below ports
<img src='img/aws_ports.png'>

After instance is up, login to that instance, install git and docker
```
sudo yum install git
sudo yum install docker
```
append docker to user group and start docker
```
sudo usermod -a -G docker $USER
sudo service docker start
```

Install docker compose and give permissions
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

Clone this repository
```
git clone https://github.com/rajashekar/eCommerce-application.git
cd eCommerce-application
```

now run
```
docker-compose up -d
```
Note: by removing `-d` you can see all [start up logs of all applications](logs/docker-compose.logs) at console <br>
Example - 
```
ec2-user@ip-172-31-47-128 eCommerce-application]$ docker-compose up -d
Starting ecommerce-application_jenkins_1          ... done
Starting ecommerce-application_ecommerceapp_1     ... done
Starting ecommerce-application_splunkenterprise_1 ... done
Starting ecommerce-application_splunkforwarder_1  ... done
```

Now above command will start 4 applications which are defined in [docker-compose.yml](docker-compose.yml)  <br>
Do `docker ps` to find all container apps which are running. 

<img src='img/dockerps.png'>
- eCommerce application running at 8081
- splunk running at 8000
- splunk forwarder 
- jenkins running at 8080

### Configuring jenkins
Go to http://your-hostname:8080/ to configure jenkins
- Unlock jenkins using `/var/jenkins_home/secrets/initialAdminPassword`
- Create First Admin user
- Install Git hub plugins
<img src='img/jenkins_plugins.png'>
- Generate ssh keys and private key in jenkins credentials
- Add your public key to deploy keys in github repo. 
<img src='img/github_deploykeys.png'>
- To configure maven go to Jenkins => Global Tool Configuration 
<img src='img/jenkins_maven.png'>
- configure new project, provide github and git details
<img src='img/jenkins_configure_git.png'>
- configure auto build triggers when commit happens. Check `Github hook trigger for GITScm polling`
<img src='img/jenkins_build_triggers.png'>
- configure maven settings. Set maven version and goals <br>
You can login to jenkins image using `docker exec -it <jenkins-container-id> bash`<br>
Create settings.xml and settings-security.xml and refer those paths like below.
<img src='img/jenkins_maven_settings.png'>
- To trigger builds on commit add jenkins webhook details in github
<img src='img/github_webhooks.png'>
- Verify builds are triggering after commit
<img src='img/jenkins_autobuild.png'>
- Refer jenkins [build and deployment logs](logs/jenkins.logs).

### Configuring splunk forwarder
Splunk forwarder is monitoring `/logs` folder in `log_volume` which is also shared by `ecommerceapp` service which logging at `/logs/ecommerceapp-application.log` <br>
Note 1 : If both splunk and splunk forwarder are running in same instance using docker compose, use `SPLUNK_FORWARD_SERVER` with value of splunk service name in this case it is `splunkenterprise` which is listening for events on `9997` port. <br>
Note 2 : If you want splunk to be run on a seperate instance, like using AWS splunk enterprise then use `SPLUNK_STANDALONE_URL` with value of AWS splunk enterprise server ip and listening port `9997`. You can use below command to pass `SPLUNK_STANDALONE_URL` and `SPLUNK_PASSWORD` to docker-compose if you do not want to share above details in docker-compose.yml
```
SPLUNK_STANDALONE_URL=<splunk-server-name>:<splunk-receive-port> SPLUNK_PASSWORD=<password> docker-compose up -d
```

### Verifying eCommerce applicaiton 
- Create user 
```
curl -v -H 'Content-type: application/json' ec2-54-92-129-250.compute-1.amazonaws.com:8081/api/user/create -d '{"username": "Raj4", "password": "password", "confirmPassword": "password"}'
*   Trying 54.92.129.250...
* TCP_NODELAY set
* Connected to ec2-54-92-129-250.compute-1.amazonaws.com (54.92.129.250) port 8081 (#0)
> POST /api/user/create HTTP/1.1
> Host: ec2-54-92-129-250.compute-1.amazonaws.com:8081
> User-Agent: curl/7.54.0
> Accept: */*
> Content-type: application/json
> Content-Length: 75
>
* upload completely sent off: 75 out of 75 bytes
< HTTP/1.1 200
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Fri, 04 Oct 2019 00:51:49 GMT
<
* Connection #0 to host ec2-54-92-129-250.compute-1.amazonaws.com left intact
{"id":1,"username":"Raj4"}%
```
- On login got `Authorization` header
```
curl -v -H 'Content-type: application/json' ec2-54-92-129-250.compute-1.amazonaws.com:8081/login -d '{"username": "Raj4", "password": "password"}'
*   Trying 54.92.129.250...
* TCP_NODELAY set
* Connected to ec2-54-92-129-250.compute-1.amazonaws.com (54.92.129.250) port 8081 (#0)
> POST /login HTTP/1.1
> Host: ec2-54-92-129-250.compute-1.amazonaws.com:8081
> User-Agent: curl/7.54.0
> Accept: */*
> Content-type: application/json
> Content-Length: 44
>
* upload completely sent off: 44 out of 44 bytes
< HTTP/1.1 200
< Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSYWo0IiwiZXhwIjoxNTcxMDMxODE4fQ.xG0IYe1hE55eOF5HkycpivsV1G0MTK6y3MN2Sf5j9NyF6WZ4UzfPyg5DzsazdiWsX49MvIfD26OK0dgkOPK_zw
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Length: 0
< Date: Fri, 04 Oct 2019 05:43:38 GMT
<
* Connection #0 to host ec2-54-92-129-250.compute-1.amazonaws.com left intact
```
- Accessing `/api/user/<userid>` without `Authorization` header results in `403`
```
curl -v -H 'Content-type: application/json' ec2-54-92-129-250.compute-1.amazonaws.com:8081/api/user/Raj4 
*   Trying 54.92.129.250...
* TCP_NODELAY set
* Connected to ec2-54-92-129-250.compute-1.amazonaws.com (54.92.129.250) port 8081 (#0)
> GET /api/user/Raj4 HTTP/1.1
> Host: ec2-54-92-129-250.compute-1.amazonaws.com:8081
> User-Agent: curl/7.54.0
> Accept: */*
> Content-type: application/json
>
< HTTP/1.1 403
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Fri, 04 Oct 2019 05:44:50 GMT
<
* Connection #0 to host ec2-54-92-129-250.compute-1.amazonaws.com left intact
{"timestamp":"2019-10-04T05:44:50.260+0000","status":403,"error":"Forbidden","message":"Access Denied","path":"/api/user/Raj4"}%
```
- On giving `Authorization` header, `/api/user/<userid>` is success. 
```
curl -v -H 'Content-type: application/json' ec2-54-92-129-250.compute-1.amazonaws.com:8081/api/user/Raj4 -H 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSYWo0IiwiZXhwIjoxNTcxMDMxODE4fQ.xG0IYe1hE55eOF5HkycpivsV1G0MTK6y3MN2Sf5j9NyF6WZ4UzfPyg5DzsazdiWsX49MvIfD26OK0dgkOPK_zw'
*   Trying 54.92.129.250...
* TCP_NODELAY set
* Connected to ec2-54-92-129-250.compute-1.amazonaws.com (54.92.129.250) port 8081 (#0)
> GET /api/user/Raj4 HTTP/1.1
> Host: ec2-54-92-129-250.compute-1.amazonaws.com:8081
> User-Agent: curl/7.54.0
> Accept: */*
> Content-type: application/json
> Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSYWo0IiwiZXhwIjoxNTcxMDMxODE4fQ.xG0IYe1hE55eOF5HkycpivsV1G0MTK6y3MN2Sf5j9NyF6WZ4UzfPyg5DzsazdiWsX49MvIfD26OK0dgkOPK_zw
>
< HTTP/1.1 200
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Fri, 04 Oct 2019 05:47:05 GMT
<
* Connection #0 to host ec2-54-92-129-250.compute-1.amazonaws.com left intact
{"id":1,"username":"Raj4"}%
```
### Configuring splunk
Go to http://your-hostname:8000/ to configure splunk
- You will be prompted with below page with initial admin password. Change passowrd after login.
<img src='img/splunk_login.png'>
- If you choose aws ec2 with minimal configuration which only gave 8 GB hard disk space, you will end up with below issue. 
For example - In my case, I only have 2.7 GB left. 
```
[root@ip-172-31-47-128 _data]# df -h
Filesystem      Size  Used Avail Use% Mounted on
devtmpfs        483M   60K  483M   1% /dev
tmpfs           493M     0  493M   0% /dev/shm
/dev/xvda1      7.9G  5.1G  2.7G  66% /
```
<img src='img/splunk_space_issue.png'>

- To resolve this <br> 1) login to splunk container using `docker exec -it <splunk-container-id> bash` <br> 2) do `apt-get update` and `apt-get install vim` <br> 3) do `vi ./etc/system/default/server.conf` change `minFreeSpace` from 5000 (i.e 5GiB) to 1000 (i.e 1GiB).

- After above settings, restart splunk which should resolve above issue and on click data summary you should see `splunkforwarder` like below 
<img src='img/splunkforwarder.png'>

- Configure splunk alert
<img src='img/splunk_alerts.png'>

- Configure splunk dashboard
<img src='img/splunk_dashboard.png'>

## Contributing
This repository is done as part of Udacity Java developer. Therefore, most likely will not accept any pull requests.
