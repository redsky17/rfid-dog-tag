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

#define MAX_TAGS 10
#define TAG_LEN 10

//Res info={0,0};
//unsigned char SCMD1[] = {0x31, 0x03, 0x01};   //start new round
//unsigned char SCMD2[] = {0x31, 0x03, 0x02};   //get next tag
//unsigned char FIRMWARE[] = { 0x10, 0x03, 0x01} ; // get firmware version
//unsigned char HARDWARWE[] = { 0x10, 0x03, 0x00}; // get hardware version
//unsigned char INVENTORY[] = { 0x43, 0x04, 0x01, 0xcd}; // get rssi inventory
int incoming=0;       //incoming from user
int bytesread = 0; 
char tag[TAG_LEN];    // the tag that is read by the RFID reader
char pairedTags[MAX_TAGS][TAG_LEN]; // for simplicity's sake, I'm making this a fixed size array for up to 10 approved tags
boolean isTagApproved[MAX_TAGS]; // this is a parallel array for pairedTags for checking the permissions of the tag

int numTags = 0;

#define RX_Pin 2
#define TX_Pin 3
#define Enable 6

#define DOOR_PIN 4

#define DOOR_LOCK_TIMEOUT 10000 // 10 seconds to re-lock the door

SoftwareSerial rfid(RX_Pin, TX_Pin);
 
void setup()
{
  rfid.begin(2400); // reader MUST be at 2400 baud
  Serial.begin(9600);
  pinMode(Enable, OUTPUT);
  digitalWrite(Enable, LOW); // enable reader
  pinMode(DOOR_PIN, OUTPUT); // enable door lock
  digitalWrite(DOOR_PIN, HIGH); // lock the door initially
  
  ble_begin();  // enable ble shield
}

boolean readingTag = false;
boolean isDoorOpen = false;
char pairingTag[10];

unsigned long unlockTime;

static byte buf_len = 0;

void ble_write_string(char *bytes, uint8_t len)
{
  if (buf_len + len > 20)
  {
    for (int j = 0; j < 15000; j++)
      ble_do_events();
    
    buf_len = 0;
  }
  
  for (int j = 0; j < len; j++)
  {
    ble_write(bytes[j]);
    buf_len++;
  }
    
  if (buf_len == 20)
  {
    for (int j = 0; j < 15000; j++)
      ble_do_events();
    
    buf_len = 0;
  }  
}


void loop()
{
  // rfid stuff
  if(rfid.available() > 0) {          // if data available from reader 
    if((incoming = rfid.read()) == 10) {   // check for header 
      bytesread = 0; 
      while(bytesread<TAG_LEN) {              // read 10 digit code 
        if( rfid.available() > 0) { 
          incoming = rfid.read(); 
          if((incoming == 10)||(incoming == 13)) { // if header or stop bytes before the 10 digit reading 
            break;                       // stop reading 
          } 
          tag[bytesread] = incoming;         // add the digit 
          
          bytesread++;                   // ready to read next digit  
        } 
      } 
      if(bytesread == TAG_LEN) {              // if 10 digit read is complete 
        Serial.print("TAG code is: ");   // possibly a good TAG 
        Serial.println(tag);            // print the TAG code 
      }
     
      int index = -1;
      if ((index = find_index(pairedTags, numTags, tag)) != -1) // we found the tag
      {
         if (isTagApproved[index])
         {
           // TODO: unlock the door!
           // Do something with millis to make it lock again after like 5 seconds
           digitalWrite(DOOR_PIN, LOW);
           unlockTime = millis();
           isDoorOpen = true;
         }
      }
      
      bytesread = 0; 
      digitalWrite(Enable, HIGH);                  // deactivate the RFID reader for a moment so it will not flood
      delay(1500);                            // wait for a bit 
      digitalWrite(Enable, LOW);                   // Activate the RFID reader
      
      if (isDoorOpen && millis() - unlockTime > DOOR_LOCK_TIMEOUT)  //  && TODO something with millis
      {
         // TODO: send lock signal to door lock
         digitalWrite(DOOR_PIN, HIGH);
         isDoorOpen = false;
      }
    } 
  } 
  
  // bluetooth stuff
  while (ble_available())
  {
      byte cmd;
      cmd = ble_read();
      
      if (readingTag)  // send the tag data back to the app
      {
        ble_write_string(pairingTag, TAG_LEN);
        readingTag = false;
      }
      
      Serial.println("BLE Received: " + cmd);
      
      switch(cmd)
      {
        case 'R':   // query tag
            readingTag = true;
            memcpy(pairingTag, tag, sizeof(pairingTag));
            break;
        case 'P':  // accept and pair the tag
            if (find_index(pairedTags, numTags, pairingTag) == -1 && numTags < 10) // we didn't find the tag, add it
            {
                for (int i = 0; i < TAG_LEN; i++)
                {
                  pairedTags[numTags][i] = pairingTag[i];
                }
                
                isTagApproved[numTags] = false;
                
                numTags++;
                
                // flash the rfid to show confirmation
                digitalWrite(Enable, HIGH);
                delay(250);
                digitalWrite(Enable, LOW);
                delay(250);
                digitalWrite(Enable, HIGH);
                delay(250);
                digitalWrite(Enable, LOW);
            }
            break;
        case 'T':  // We are 'toggling' the state of a tag.
             char tagRead[TAG_LEN];
             // get the tag in question
             while(bytesread<TAG_LEN) {              // read 10 digit code
               if (ble_available() > 0) 
               {
                 tagRead[bytesread++] = ble_read();
               }
             }
            
             int index = -1;
             // find it's index
             if ((index = find_index(pairedTags, numTags, tagRead)) != -1) // we found the tag
             { 
                // if it exists, set the corresponding isTagApproved value to whatever the value sent over BT is
                isTagApproved[index] = ble_read() > 0 ? true : false;
             }             
             else  // add it since the app trusts it
             {
                for (int i = 0; i < TAG_LEN; i++)
                {
                  pairedTags[numTags][i] = tagRead[i];
                }
                isTagApproved[numTags] = ble_read() > 0 ? true : false;
                
                numTags++;
             } 
             
                             // flash the rfid to show confirmation
                digitalWrite(Enable, HIGH);
                delay(250);
                digitalWrite(Enable, LOW);
                delay(250);
                digitalWrite(Enable, HIGH);
                delay(250);
                digitalWrite(Enable, LOW);
             
             break;
            
      }
      
      ble_do_events();
      buf_len = 0;
      
      return; // only do this task in this loop
  }
  
  ble_do_events();
  buf_len = 0;
}

int find_index(char a[][TAG_LEN], int num_elements, char b[])
{
   int i;
   for (i=0; i<num_elements; i++)
   {
     bool found = true;
     for (int j = 0; j < TAG_LEN; j++)
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
