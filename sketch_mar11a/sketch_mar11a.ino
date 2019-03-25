#include <SoftwareSerial.h>
#define PI 3.1415926535897932384626433832795

// TODO: find a way to write debug data by writing to Serial monitor;
// the pollAngle function is probably incorrect somewhere
SoftwareSerial StwSerial(4,5);
void setup() {
  
  // put your setup code here, to run once:
  StwSerial.begin(19200);
  Serial.begin(9600);
  StwSerial.write(128); // Start
  StwSerial.write(130); // Control
  StwSerial.write(132); // Full

  int i=0;
  int j=32768; // radius for driving straight
  
  //byte drive_straight_cmd[] = {137, (byte)(i>>8), (byte)200, (byte)(j>>8), (byte)j};
  //twSerial.write(drive_straight_cmd, 5);  
////------------------ working correctly
    //drive straight 200mm/s
//    StwSerial.write(137);
//    StwSerial.write((byte)(i>>8));
//    StwSerial.write((byte)200);
//    StwSerial.write((byte)(j>>8));
//    StwSerial.write((byte)j);
//    
//  
//  delay(3000);
//  
//  //byte stop_command[] = {137,0,0,0,0};
//  //StwSerial.write(stop_command, 5); // Stop
//  // stop driving
//  StwSerial.write(137);
//  StwSerial.write((byte)(i>>8));
//  StwSerial.write((byte)(i));
//  StwSerial.write((byte)(j>>8));
//  StwSerial.write((byte)j);
  
////------------------ correctly working code ends here
  double currangle = 0; //1st request
  pollAngle();
  Serial.println(currangle);
  while(currangle < 90) {
      //char turn_clockw_cmd[] = {137, 0, -56, -1, -1};
      //StwSerial.write(turn_clockw_cmd, 5); // Turn clock-wise
      int k = 1;
      StwSerial.write(137);
      StwSerial.write((byte)(i>>8));
      StwSerial.write((byte)100);
      StwSerial.write((byte)(k>>8));
      StwSerial.write((byte)k);
      //delay(100); // polling interval
      
      currangle += pollAngle();
      Serial.println(currangle);
  }
    //StwSerial.write(stop_command, 5); // Stop
  StwSerial.write(137);
  StwSerial.write((byte)(i>>8));
  StwSerial.write((byte)(i));
  StwSerial.write((byte)(j>>8));
  StwSerial.write((byte)j);
}
void loop() {
  // put your main code here, to run repeatedly:
}

/*
Fetches packet 2 data from Sensors (6 bytes total) 
and reads only the bytes related to the Angle data (5th and 6th bytes)
*/
double pollAngle(){
  //char packet2_cmd[] = {142, 2};
  //StwSerial.write(packet2_cmd, 2);
  StwSerial.write(142);
  StwSerial.write(2);
 // byte data[6];
  int difference = 0;
  while(true) {
    if (StwSerial.available()) { // possibly an error here
      for(int i = 0; i < 4; ++i){
            //Serial.print("inside for = ");
        StwSerial.read();
       // Serial.println();
    }
    difference = StwSerial.read() | StwSerial.read() << 8; 
    break;
    }
  }
  return (360 * difference) / (258 * 3.14159);
}

double kostyl(){}


