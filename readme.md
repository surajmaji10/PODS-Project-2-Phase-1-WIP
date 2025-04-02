# PODS-2025: Project-2-Phase-1

## Steps to run the services

### Way-1: Running locally after downloading from github
Clone: https://github.com/surajmaji10/PODS-Project-2-Phase-1-WIP/tree/localhost-\
Run the script for local execution of services(localhost)\
(Preferably run in three terminal windows, Ctrl+C to kill each)
```bash
# "account-service"
./local.sh -1
# marketplace-service
./local.sh -2
# wallet-service
./local.sh -3
```
(The containers will be running locally at 8080, 8081, 8082)\
http://localhost:8080/\
http://localhost:8081/\
http://localhost:8082/


### Way-2: Running containers using docker
`Prerequisite:` docker installed and set up properly\
Download the three services from https://hub.docker.com/u/akashmaji

```bash
docker pull akashmaji/account-service-akka
docker pull akashmaji/marketplace-service-akka
docker pull akashmaji/wallet-service-akka
```
Run the three services as this:
```bash
docker run -p 8080:8080 --rm --name account-service \
                        --add-host=host.docker.internal:host-gateway \
                        akashmaji/account-service-akka:1.0 &
```
```bash

docker run -p 8081:8080 --rm --name marketplace-service \
                        --add-host=host.docker.internal:host-gateway \
                        akashmaji/marketplace-service-akka:1.0 &
```
```bash
docker run -p 8082:8080 --rm --name wallet-service \
                        --add-host=host.docker.internal:host-gateway \
                        akashmaji/wallet-service-akka:1.0 &
```
(The containers are being port forwarded to 8080, 8081, 8082)\
http://localhost:8080/\
http://localhost:8081/\
http://localhost:8082/

### Way-2: Running containers using docker after manual building
Clone: https://github.com/surajmaji10/PODS-Project-2-Phase-1-WIP/tree/docker-\
Run the script for containerized execution of services(localhost accessible)\
(Preferably run in three terminal windows, Ctrl+C to kill each)
```bash
# "account-service"
./docker.sh -1
# marketplace-service
./docker.sh -2
# wallet-service
./docker.sh -3
```
`Note:` do cleanup using clean.sh
```bash
./clean.sh
```
(The containers will be running locally at 8080, 8081, 8082)\
http://localhost:8080/\
http://localhost:8081/\
http://localhost:8082/

### Running test cases:
Run the test-cases in the `test-cases` folder
```python
python test_case_1.py
....
```

### Thanks
@author: Akash Maji\
@email: akashmaji@iisc.ac.in

