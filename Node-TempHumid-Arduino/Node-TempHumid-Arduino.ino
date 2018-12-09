//Libraires
#include <stdlib.h>
#include <DHT.h>

/*------------------------DHT SENSOR------------------------*/
#define DHTPIN 2        // DHT data pin connected to Arduino pin 2
#define DHTTYPE DHT11     // DHT 22 (or AM2302)
DHT dht(DHTPIN, DHTTYPE); // Initialize the DHT sensor
/*----------------------------------------------------------*/

/*-----------------ESP8266 Serial WiFi Module---------------*/
#define SSID "Vodafone-33498108"     // "SSID-WiFiname" 
#define PASS "vl2z5liskvbikl8"       // "password"
#define IP "192.168.1.7"  // localhost http http://192.168.1.4:4445
String msg = ""; //change it with your key...
/*-----------------------------------------------------------*/

//Variables
float temp;
String hum;
String tempC;
String tempF;
int error;
void setup()
{
  Serial.begin(115200); //or use default 115200.
  
  Serial.println("AT");
  delay(5000);
  if(Serial.find("OK")){
    connectWiFi();
  }
}

void loop(){
  //Read temperature and humidity values from DHT sensor:
  
  error=0;
  char buffer[10];
  
  temp = dht.readHumidity();
  hum = dtostrf(temp, 4, 1, buffer); // convert a float to a char array 
  
  temp = dht.readTemperature();
  tempC = dtostrf(temp, 4, 1, buffer); // convert a float to a char array 
  
  temp = dht.readTemperature(true);
  tempF= dtostrf(temp, 4, 1, buffer); // convert a float to a char array 
  
  updateTemp();
  //Resend if transmission is not completed 
  if (!error){
    delay(2000); //Update every 2 second 
  }
}

void updateTemp(){
  String cmd = "AT+CIPSTART=\"TCP\",\"";
  cmd += IP;
  cmd += "\",4446";
  Serial.println(cmd);
  delay(2000);
  if(Serial.find("Error")){
    return;
  }
  msg = "" ;    
  msg += hum;
  msg += "\t"+tempC; 
  msg += "\t"+tempF;
  msg += "\r\n";
  Serial.print("AT+CIPSEND=");
  Serial.println(msg.length());
  if(Serial.find(">")){
    Serial.print(msg);
  }
  else{
    Serial.println("AT+CIPCLOSE");
    //Resend...
    error=1;
  }
}

 
boolean connectWiFi(){
  Serial.println("AT+CWMODE=1");
  delay(2000);
  String cmd="AT+CWJAP=\"";
  cmd+=SSID;
  cmd+="\",\"";
  cmd+=PASS;
  cmd+="\"";
  Serial.println(cmd);
  delay(5000);
  if(Serial.find("OK")){
    return true;
  }else{
    return false;
  }
}
