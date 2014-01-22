HardwareSPI spi(1);

uint8 writeBuf[4];
uint8 readBuf[4];

//void transferBytes

void setup() {
  
  pinMode(9, OUTPUT);
  digitalWrite(9, HIGH);
      
  spi.begin(SPI_4_5MHZ, MSBFIRST, SPI_MODE_0);
  
}

void loop() {
  
  writeBuf[0] = 0x20;
  writeBuf[1] = 0x00;
  writeBuf[2] = 0x00;
  writeBuf[3] = 0x00;
    
  digitalWrite(9, LOW);
  delay(1);
  
  readBuf[0] = spi.transfer(writeBuf[0]);
  delay(1);
  //SerialUSB.println(readBuf[0]);
  readBuf[1] = spi.transfer(writeBuf[1]);
  delay(1);
  //SerialUSB.println(readBuf[1]);
  readBuf[2] = spi.transfer(writeBuf[2]);
  delay(1);
  //SerialUSB.println(readBuf[2]);
  readBuf[3] = spi.transfer(writeBuf[3]);
  delay(1);
  //SerialUSB.println(readBuf[3]);
  
  digitalWrite(9, HIGH);
  
  uint8 test = readBuf[0] & 0b00001100;
  if (test == 0b00000100) {
    uint16 temp0 = (uint16) readBuf[0];
    uint16 temp1 = (uint16) readBuf[1];
  
    uint16 unsignedData = (readBuf[2] >> 2);
    unsignedData += (temp1 << 6);
    unsignedData += (temp0 << 14);
    int16 signedData = (int16) unsignedData;
    SerialUSB.println(signedData);
  
  } else {
    // not sensor data; could be a R/W error message
  }
  
  delay(100);
}
