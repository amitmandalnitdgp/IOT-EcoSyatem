# IOT-EcoSyatem
The system contains 2-things (nodes), 1 gateway (with computation power) and cloud storage.
Node-1: NodeTempHumid running on Wemos-D1-R2, which reads temperature and humidity from a DTH22 and gets the soil moisture from moisture sensor. these data are then sent to gateway using its inbuilt WiFi module. 
Node-2: NodeTempHumidArduino running on Arduino Uno R3, which reads temperature and humidity from a DTH11 and sends it to gateway using an ESP8266 module. 
The Raspberry Pi B3+ serves as gateway which receives sensor readings of Node-1 & Node-2 and computes the average of DHT11 & DHT22 readings and send it to Hadoop.
Storage is a HBase database.
