echo "installing service..."
mvn clean install
echo "Setting environmental variables..."
export $(cat .env | xargs)
echo "Starting service..."
java -jar target/weatherApp-*.jar