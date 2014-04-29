#include <SoftwareSerial.h>

#include <SPI.h>
#include <boards.h>
#include <ble_shield.h>
#include <services.h>
#include "Boards.h"
//struct Res{
//  unsigned char Tag_num;                   //number of tags detected
//  unsigned char Rcard[12];        //EPC of each tag
//};
//Res info={0,0};
//unsigned char SCMD1[] = {0x31, 0x03, 0x01};   //start new round
//unsigned char SCMD2[] = {0x31, 0x03, 0x02};   //get next tag
//unsigned char FIRMWARE[] = { 0x10, 0x03, 0x01} ; // get firmware version
//unsigned char HARDWARWE[] = { 0x10, 0x03, 0x00}; // get hardware version
//unsigned char INVENTORY[] = { 0x43, 0x04, 0x01, 0xcd}; // get rssi inventory
int incoming=0;       //incoming from user
int bytesread = 0; 
char tag[10];    // the tag that is read by the RFID reader
char pairedTags[10][10]; // for simplicity's sake, I'm making this a fixed size array for up to 10 approved tags
boolean isTagApproved[10]; // this is a parallel array for pairedTags for checking the permissions of the tag

int numTags = 0;

#define RX_Pin 2
#define TX_Pin 3
#define Enable 6

SoftwareSerial rfid(RX_Pin, TX_Pin);
 
void setup()
{
  rfid.begin(2400); // reader MUST be at 2400 baud
  Serial.begin(9600);
  pinMode(Enable, OUTPUT);
  digitalWrite(Enable, LOW); // enable reader
  
  ble_begin();  // enable ble shield
}

boolean readingTag = false;
char pairingTag[10];

void loop()
{
  // rfid stuff
  if(rfid.available() > 0) {          // if data available from reader 
    if((incoming = rfid.read()) == 10) {   // check for header 
      bytesread = 0; 
      while(bytesread<10) {              // read 10 digit code 
        if( rfid.available() > 0) { 
          incoming = rfid.read(); 
          if((incoming == 10)||(incoming == 13)) { // if header or stop bytes before the 10 digit reading 
            break;                       // stop reading 
          } 
          tag[bytesread] = incoming;         // add the digit           
          bytesread++;                   // ready to read next digit  
        } 
      } 
      if(bytesread == 10) {              // if 10 digit read is complete 
        Serial.print("TAG code is: ");   // possibly a good TAG 
        Serial.println(tag);            // print the TAG code 
      } 
      bytesread = 0; 
      digitalWrite(Enable, HIGH);                  // deactivate the RFID reader for a moment so it will not flood
      delay(1500);                            // wait for a bit 
      digitalWrite(Enable, LOW);                   // Activate the RFID reader
    } 
  } 
  
  // bluetooth stuff
  if (ble_available() > 0)
  {
      byte cmd;
      cmd = ble_read();
      
      if (readingTag)  // send the tag data back to the app
      {
        // send tag data back to android app 
      }
      
      Serial.write("BLE Received: " + cmd);
      
      switch(cmd)
      {
        case 'R':   // query tag
            readingTag = true;
            memcpy(pairingTag, tag, sizeof(pairingTag));
            break;
        case 'P':  // accept and pair the tag
            if (
            
      }
  }
}

int find_index(char a[][], int num_elements, char b[] value)
{
   int i;
   for (i=0; i<num_elements; i++)
   {
     bool found = true;
     for (int j = 0; j < 10; j++)
     {
	 if (a[i][j] != b[j])
	 {
            found = false;
	 }
     }
     if (found)
     	return i;  /* it was found */
   }
   return(-1);  /* if it was not found */
}
