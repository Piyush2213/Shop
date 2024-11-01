#!/bin/sh

echo "Running the jar file via the bash script"
# Run your application
java --add-opens java.base/java.math=ALL-UNNAMED -jar /ecommerce-0.0.1-SNAPSHOT.jar

# If the command above fails, keep the container running
#while true; do
#  echo "Application failed, keeping container running..."
#  sleep 3600
#done
