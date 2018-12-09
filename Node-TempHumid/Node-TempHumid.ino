#include <ESP8266WiFi.h> // Wifi Library
#include "DHT.h" // Temperature and Humidity Sensor Library

#define DHTPIN D5     // what digital pin we're connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321
DHT dht(DHTPIN, DHTTYPE); // Initializing the DTH22 Sensor with the PIN No
int sensor_pin = A0; //PIN for Moisture Sensor
int moisture ; //variable to store the soil moisture value
bool connected = false;

const char* ssid     = "Vodafone-33498108"; // SSID of the Network
const char* password = "vl2z5liskvbikl8"; // Password for the network

const char* host = "192.168.1.7"; // IP of the system where we will send our data
    const int httpPort = 4445;
void setup() {
  Serial.begin(115200); // set baud rate
  dht.begin();  //initialize dht22 sensor module

//Connecting with Network...
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());


}

void loop() {
  delay(5000);
  
///////////////////////////////Reading Sensors/////////////////////////////////////////////
  //Reading Temperature and Humidity
  String sensdata = "";
  float humid = dht.readHumidity();
  float tc = dht.readTemperature();  // Read temperature as Celsius (the default)
  float tf = dht.readTemperature(true);  // Read temperature as Fahrenheit (isFahrenheit = true)
  // Check if any reads failed and exit early (to try again).
  if (isnan(humid) || isnan(tc) || isnan(tf)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  //Reading Soil Moisture Sensor Data
  moisture= analogRead(sensor_pin);
  moisture = map(moisture,550,0,0,100);
  
  sensdata = sensdata+humid+"\t"+tc+"\t"+tf+"\t"+moisture;  //Creating a String for sensor reading

///////////////////////////////Sending The values to gateway/////////////////////////////////////////////
  
    Serial.print("connecting to ");
    Serial.println(host);
    WiFiClient client;

      if (!client.connect(host, httpPort)) {
        Serial.println("data server connection failed");
        return;
      }else{
        Serial.println("data server connected");
        connected = true;
      }
  Serial.println("Sending sensor reading: "+sensdata);
  client.print(sensdata);
}
