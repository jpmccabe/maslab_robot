#include <stdlib.h>
#include <wirish/wirish.h>

/* features 
 [ ] dagu motor controller
 [X] cytron motor controller
 [X] encoder
 [X] gyro
 [ ] servo
 [X] ultrasonic
 [X] IR
 [X] analog read
 [X] analog write
 [X] digital read
 [X] digital write
 */

#define LED_PIN BOARD_LED_PIN

#define INIT 'I'
#define GET 'G'
#define SET 'S'
#define RESPONSE 'R'
#define END ((char) 0xff)



//===================================
// MAPLE HARDWARE CLASSES
//===================================

HardwareSPI spi1(1);
HardwareSPI spi2(2);


//===================================
// UTILITY FUNCTIONS
//===================================

uint8 serialRead() {
    while (!SerialUSB.available());
    return SerialUSB.read();
}


//===================================
// HELPER CLASSES
//===================================

class Device {
public:
  virtual void sample() = 0;
  virtual void get() = 0;
  virtual void set() = 0;
};

class SettableDevice : public Device {
public:
  void sample() {}
  void get() {}
};

class SampleableDevice : public Device {
public:
  void set() {}
};

// device-specific data that DeviceList needs to reference
uint8 ultrasonicCount;
uint8 encoderCount;

class DeviceList {
private:
  Device** devices;
  uint8 devicesArraySize;
  uint8 count;
public:
  DeviceList() {
    devices = NULL;
  }
  
  void init() {
    if (devices != NULL) {
      for (int i = 0; i < count; i++) {
        delete devices[i];
      }
      free(devices);
    }
    
    devicesArraySize = serialRead();
    devices = (Device**) malloc(sizeof(Device*) * devicesArraySize);
    count = 0;

    ultrasonicCount = 0;
  }

  void sample() {
    for (int i = 0; i < count; i++) {
      devices[i]->sample();
    }
  }

  void get() {
    for (int i = 0; i < count; i++) {
      devices[i]->get();
    }
  }

  void set() {
    while (true) {
      uint8 deviceIndex = serialRead();
      if (deviceIndex == END) {
        return;
      }
      if (deviceIndex >= count) {
        while (serialRead() != END);
        return;
      }
      devices[deviceIndex]->set();
    }
  }

  void add(Device *device) {
    if (count < devicesArraySize) {
      devices[count] = device;
      count++;
    } else {
      // send WTF packet
    }
  }
};




//===================================
// DEVICE IMPLEMENTATIONS
//===================================

#define ANALOG_INPUT_CODE 'A'
#define PWM_OUTPUT_CODE 'P'
#define DIGITAL_INPUT_CODE 'D'
#define DIGITAL_OUTPUT_CODE 'd'

#define CYTRON_CODE 'C'
#define ENCODER_CODE 'N'
#define GYROSCOPE_CODE 'Y'
#define ULTRASONIC_CODE 'U'


//-----------------------------------
// Analog Input
//-----------------------------------

class AnalogInput : public SampleableDevice {
private:
  uint8 pin;
  uint16 val;
public:
  AnalogInput() {
    pin = serialRead();
    pinMode(pin, INPUT_ANALOG);
    val = 0;
  }
  void sample() {
    val = analogRead(pin);
  }
  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = val;
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};


//-----------------------------------
// PWM Output
//-----------------------------------

class PwmOutput : public SettableDevice {
private:
  uint8 pin;
public:
  PwmOutput() {
    pin = serialRead();
    pinMode(pin, PWM);
    pwmWrite(pin, 0);
  }
  void set() {
    uint8 msb = serialRead();
    uint8 lsb = serialRead();
    uint16 dutyCycle = msb;
    dutyCycle = (dutyCycle << 8) + lsb;
    pwmWrite(pin, dutyCycle);
  }
};


//-----------------------------------
// Digital Input
//-----------------------------------

class DigitalInput : public SampleableDevice {
private:
  uint8 pin;
  bool val;
public:
  DigitalInput() {
    pin = serialRead();
    pinMode(pin, INPUT);
    val = false;
  }
  void sample() {
    val = digitalRead(pin);
  }
  void get() {
    SerialUSB.write(val);
  }
};


//-----------------------------------
// Digital Output
//-----------------------------------

class DigitalOutput : public SettableDevice {
private:
  uint8 pin;
public:
  DigitalOutput() {
    pin = serialRead();
    pinMode(pin, OUTPUT);
    digitalWrite(pin, false);
  }
  void set() {
    // note that the uint8 is used here as a boolean
    uint8 value = serialRead();
    digitalWrite(pin, value);
  }
};


//-----------------------------------
// Cytron motor controller
//-----------------------------------

class Cytron : public SettableDevice {
private:    
  uint8 dirPin;
  uint8 pwmPin;
public:
  Cytron() {
    dirPin = serialRead();
    pwmPin = serialRead();
    pinMode(dirPin, OUTPUT);
    pinMode(pwmPin, PWM);
    setSpeed(0);
  }

  void set() {
    uint8 msb = serialRead();
    uint8 lsb = serialRead();
    uint16 speed = msb;
    speed = (speed << 8) + lsb;
    setSpeed(speed);
  }

  void setSpeed(uint16 speed) {
    bool reverse = speed & 0x8000;
    digitalWrite(dirPin, reverse);
    analogWrite(pwmPin, reverse ? 2 * (speed ^ 0xFFFF) : 2 * speed);
  }
};


//-----------------------------------
// Gyroscope
//-----------------------------------

class Gyroscope : public SampleableDevice {
private:
  HardwareSPI* spi;
  uint8 ssPin;
  uint16 val;
  uint8 readBuf[4];
  uint8 writeBuf[4];
  const static int delayTime = 1;

public:
  Gyroscope() {
    uint8 spiPort = serialRead();
    ssPin = serialRead();
    
    pinMode(ssPin, OUTPUT);
    digitalWrite(ssPin, HIGH);
    
    if (spiPort == 1) {
      spi = &spi1;
    } else if (spiPort == 2) {
      spi = &spi2;
    } else {
      return;
    }
    spi->begin(SPI_4_5MHZ, MSBFIRST, SPI_MODE_0);
    
    writeBuf[0] = 0x20;
    writeBuf[1] = 0x00;
    writeBuf[2] = 0x00;
    writeBuf[3] = 0x00;
    
    val = 0;
  }
  
  void sample() {
    
    digitalWrite(ssPin, LOW);
    delay(delayTime);
    
    readBuf[0] = spi->transfer(writeBuf[0]);
    delay(delayTime);
    readBuf[1] = spi->transfer(writeBuf[1]);
    delay(delayTime);
    readBuf[2] = spi->transfer(writeBuf[2]);
    delay(delayTime);
    readBuf[3] = spi->transfer(writeBuf[3]);
    delay(delayTime);
    
    digitalWrite(ssPin, HIGH);
    
    uint8 test = ((readBuf[0] & 0b00001100) == 0b00000100);
    if (test) {
      uint16 temp0 = (uint16) readBuf[0];
      uint16 temp1 = (uint16) readBuf[1];
      val = (readBuf[2] >> 2);
      val += (temp1 << 6);
      val += (temp0 << 14);  
    } else {
      // not sensor data; could be a R/W error message
      val = 0x8000;
    }
  }
  
  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = val;
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};


//-----------------------------------
// Ultrasonic range finder
//-----------------------------------

void ultrasonicISR(uint8 index);

void ultrasonicISR0() { ultrasonicISR(0); }
void ultrasonicISR1() { ultrasonicISR(1); }
void ultrasonicISR2() { ultrasonicISR(2); }
void ultrasonicISR3() { ultrasonicISR(3); }
void ultrasonicISR4() { ultrasonicISR(4); }
void ultrasonicISR5() { ultrasonicISR(5); }
void ultrasonicISR6() { ultrasonicISR(6); }
void ultrasonicISR7() { ultrasonicISR(7); }

typedef void (*UltrasonicISRPtr)();
UltrasonicISRPtr ultrasonicISRList[8] = {&ultrasonicISR0,
                                         &ultrasonicISR1,
                                         &ultrasonicISR2,
                                         &ultrasonicISR3,
                                         &ultrasonicISR4,
                                         &ultrasonicISR5,
                                         &ultrasonicISR6,
                                         &ultrasonicISR7};
                                                
class Ultrasonic;

Ultrasonic *ultrasonics[8];

class Ultrasonic : public SampleableDevice {
private:
  uint8 triggerPin;
  uint8 echoPin;

  uint32 startTime;
  bool isEchoLow;
  bool receivedEcho;

  uint16 val;

public:
  Ultrasonic() {
    triggerPin = serialRead();
    echoPin = serialRead();
    
    pinMode(triggerPin, OUTPUT);
    pinMode(echoPin, INPUT);
    
    digitalWrite(triggerPin, LOW);

    ultrasonics[ultrasonicCount] = this;
    attachInterrupt(echoPin, *(ultrasonicISRList[ultrasonicCount]), CHANGE);
    ultrasonicCount++;
    
    val = 0;
  }

  void localISR() {
    if (isEchoLow) {
      startTime = micros();
      isEchoLow = false;
      receivedEcho = true;
    } else {
      val = micros() - startTime;
      isEchoLow = true;
    }
  }

  void sample() {
    if (!receivedEcho) {
      if (micros()-startTime < 60000) {
        return;
      }
    }

    if (!isEchoLow) {
      return;
    }

    digitalWrite(triggerPin,HIGH);
    delayMicroseconds(10);
    digitalWrite(triggerPin,LOW);
    startTime = micros();

    isEchoLow = true;
    receivedEcho = false;
  }

  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = (uint8) val;
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};

void ultrasonicISR(uint8 index) {
  ultrasonics[index]->localISR();
}


//-----------------------------------
// Encoder (Pololu 29:1 64CPR)
//-----------------------------------

void encoderISR(uint8 index);

void encoderISR0() { encoderISR(0); }
void encoderISR1() { encoderISR(1); }
void encoderISR2() { encoderISR(2); }
void encoderISR3() { encoderISR(3); }
void encoderISR4() { encoderISR(4); }
void encoderISR5() { encoderISR(5); }
void encoderISR6() { encoderISR(6); }
void encoderISR7() { encoderISR(7); }

typedef void (*EncoderISRPtr)();
EncoderISRPtr encoderISRList[8] = {&encoderISR0,
                                   &encoderISR1,
                                   &encoderISR2,
                                   &encoderISR3,
                                   &encoderISR4,
                                   &encoderISR5,
                                   &encoderISR6,
                                   &encoderISR7};
                                                
class Encoder;

Encoder *encoders[8];

class Encoder : public SampleableDevice {
private:
  uint8 pinA;
  uint8 pinB;

  uint16 ticks;

public:
  Encoder() {
    pinA = serialRead();
    pinB = serialRead();
    
    pinMode(pinA, INPUT);
    pinMode(pinB, INPUT);
    
    encoders[encoderCount] = this;
    attachInterrupt(pinA, *(encoderISRList[encoderCount]), RISING);
    encoderCount++;
    
    ticks = 0;
  }

  void localISR() {
  // TODO: atomicize
    if (digitalRead(pinB)) {
        ticks--;
    } else {
        ticks++;
    }
  }

  void sample() { }

  void get() {
    // TODO: atomicize
    //uint16 temp = __sync_fetch_and_nand(&ticks, 0); // doesn't work in arm-...-gcc?
    uint16 temp = ticks;
    ticks = 0;
    uint8 msb = temp >> 8;
    uint8 lsb = (uint8) temp;
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};

void encoderISR(uint8 index) {
  encoders[index]->localISR();
}


//===================================
// LOGIC
//===================================

void firmwareInit();
void get();
void set();

bool initStatus;
DeviceList deviceList;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  initStatus = false;
}

void loop() {
  //sample sensors and buffer
  char header = serialRead();
  switch (header) {
  case INIT:
    firmwareInit();
    break;
  case GET:
    get();
    break;
  case SET:
    set();
    break;
  case END:
    break;
  default:
    while (serialRead() != END);
    break;
  }
}


void firmwareInit() {
  initStatus = false;
  deviceList.init();

  uint8 deviceCode;
  while (true) {
    deviceCode = serialRead();
    switch (deviceCode) {
    case ANALOG_INPUT_CODE:
      deviceList.add(new AnalogInput());
      break;
    case PWM_OUTPUT_CODE:
      deviceList.add(new PwmOutput());
      break;
    case DIGITAL_INPUT_CODE:
      deviceList.add(new DigitalInput());
      break;
    case DIGITAL_OUTPUT_CODE:
      deviceList.add(new DigitalOutput());
      break;
    case CYTRON_CODE:
      deviceList.add(new Cytron());
      break;
    case ENCODER_CODE:
      deviceList.add(new Encoder());
      break;
    case GYROSCOPE_CODE:
      deviceList.add(new Gyroscope());
      break;
    case ULTRASONIC_CODE:
      deviceList.add(new Ultrasonic());
      break;
    case END:
      initStatus = true;
      return;
    default:
      while (serialRead() != END);
      return;
    }
  }
  
}

void get() {
  if (!initStatus) {
    // send WTF packet
    return;
  }

  deviceList.sample();
  
  SerialUSB.write(RESPONSE);
  deviceList.get();
  SerialUSB.write(END);
}

void set() {
  if (!initStatus) {
    // send WTF packet
    return;
  }

  deviceList.set();
}




// Force init to be called *first*, i.e. before static object allocation.
// Otherwise, statically allocated objects that need libmaple may fail.
__attribute__((constructor)) void premain() {
    init();
}

int main(void) {
    setup();

    while (true) {
        loop();
    }

    return 0;
}
