echo "installing service..."
mvn clean install
echo "Starting service..."
java -jar target/weatherApp-*.jar
