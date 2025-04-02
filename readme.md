# PODS-2025: Project-2-Phase-1

## Steps to Run the Services

### Way 1: Running Locally After Downloading from GitHub

#### Clone the Repository
```
https://github.com/surajmaji10/PODS-Project-2-Phase-1-WIP/tree/localhost-
```

#### Run the Script for Local Execution
(Preferably run in three terminal windows, press `Ctrl+C` to kill each service)

```bash
# Start "account-service"
./local.sh -1

# Start "marketplace-service"
./local.sh -2

# Start "wallet-service"
./local.sh -3
```

#### Access the Services
The containers will be running locally at:
- [http://localhost:8080/](http://localhost:8080/)
- [http://localhost:8081/](http://localhost:8081/)
- [http://localhost:8082/](http://localhost:8082/)

---

## Running Test Cases
Run the test cases located in the `test-cases` folder:

```bash
python test_case_1.py
...
```

---

## Thanks
**Author:** Akash Maji  
**Email:** [akashmaji@iisc.ac.in](mailto:akashmaji@iisc.ac.in)

