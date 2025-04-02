#!/bin/bash

# filepath: /home/akash/Desktop/PODS-P2/local.sh

# Exit immediately if a command exits with a non-zero status
set -e

# Define the folders
FOLDER_2="marketplace-service"
FOLDER_1="account-service"
FOLDER_3="wallet-service"

# Function to clean, build, and run a Spring Boot app
run_folder_2() {
  echo "Cleaning, building, and running the Marketplace app..."
  cd "$FOLDER_1"
  mvn clean
  mvn compile -DskipTests
  trap "kill 0" SIGINT # Ensure the app stops on Ctrl+C
  mvn exec:java -Dexec.mainClass="me.akashmaj.demomarketplaceservice.DemoMarketplaceServiceApplication"
  cd ..
}

run_folder_1() {
  echo "Cleaning, building, and running the app in $FOLDER_2..."
  cd "$FOLDER_1"
  mvn clean
  mvn package -DskipTests
  trap "kill 0" SIGINT # Ensure the app stops on Ctrl+C
  java -jar target/*.jar # Adjust if the JAR file name is specific
  cd ..
}

run_folder_3() {
  echo "Cleaning, building, and running the app in $FOLDER_3..."
  cd "$FOLDER_3"
  mvn clean
  mvn package -DskipTests
  trap "kill 0" SIGINT # Ensure the app stops on Ctrl+C
  java -jar target/*.jar # Adjust if the JAR file name is specific
  cd ..
}

# Parse command-line arguments
if [[ "$1" == "-1" ]]; then
  run_folder_1
elif [[ "$1" == "-2" ]]; then
  run_folder_2
elif [[ "$1" == "-3" ]]; then
  run_folder_3
elif [[ "$1" == "-all" ]]; then
  run_folder_1 &
  run_folder_2 &
  run_folder_3 &
  wait # Wait for all background processes to finish
else
  echo "Usage: $0 {-1|-2|-3|-all}"
  echo "  -1    Run Marketplace app"
  echo "  -2    Run Account Service app"
  echo "  -3    Run Wallet Service app"
  echo "  -all  Run all apps"
  exit 1
fi