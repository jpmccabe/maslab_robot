#include <Servo.h>

// TODO:
// [X] Serial Comm
// [ ] Dagu Motor Controller
// [X] Cytron Motor Controller
// [X] Encoders

// [X] Servo
// [X] HC-SR04 Sonic Range Finder
// [X] Analog read
// [X] Analog write
// [X] Digital read
// [X] Digital write

#define LED_PIN BOARD_LED_PIN

/*
// Serial test
void setup() {
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  delay(100);
  while (SerialUSB.available()) {
    char input = SerialUSB.read();
    SerialUSB.println(input);
    toggleLED();
  }
}
*/

/*
// Cytron Motor Controller
unsigned int pwm = 32000;
boolean dir = true;

void setup() {
  pinMode(3, PWM);
  pinMode(4, OUTPUT);
}

void loop() {
  digitalWrite(4, dir);
  pwmWrite(3, pwm);
  delay(100);
  SerialUSB.println('A');
}
*/

/*
volatile unsigned int count = 0;

void irh() {
  if ( digitalRead(28)==HIGH )
    count++;
  else
    count--;
}

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(26, INPUT);
  pinMode(28, INPUT);

  attachInterrupt(26,irh,RISING);
  interrupts();
}

void loop() {
  pwmWrite(24,count*10);
}
*/

/*
// Encoder rig
volatile unsigned int count = 0;

void irh() {
  if ( digitalRead(28)==HIGH )
    count++;
  else
    count--;
}

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(26, INPUT);
  pinMode(28, INPUT);

  attachInterrupt(26,irh,RISING);
  interrupts();
}

void loop() {
  pwmWrite(24,count*10);
}
*/


// Sonic Range Finder

class Ultra {
public:
  uint8 trig;
  uint8 echo;
  volatile unsigned int start;
  volatile unsigned int endx;

  Ultra(uint8 _trig, uint8 _echo) : trig(_trig), echo(_echo) {
    pinMode(trig, OUTPUT);
    pinMode(echo, INPUT);
    start = 0;
    endx = 0;
    attachInterrupt(echo, Ultra::irh, CHANGE);
  }
  
  void irh() {
    if ( digitalRead(echo)==HIGH ) {
      start = micros();
    } else {
      endx = micros();
    }
  }
  
  unsigned int diff() {
    return endx - start;
  }
}

Ultra ultra;

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  ultra = Ultra(30, 28);
  
  interrupts();
}

void loop() {
  digitalWrite(30,HIGH);
  delayMicroseconds(10);
  digitalWrite(30,LOW);
  delay(60);
  
  unsigned int diff = ultra.diff();
  pwmWrite(24, diff*3/2);
  SerialUSB.println(diff);
}


/*
// Servo
Servo servo;

void setup() {
  servo.attach(1);
  
  // Set up the built-in LED pin as an output:
  //pinMode(24, PWM);
}

int i = 0;
void loop() {
  //pwmWrite(24,i);
  servo.write(i);
  delay(1000);
  i += 5;
  if ( i > 180 ) i = 0;
}
*/

/*
// Analog Write
void setup() {
    // Set up the built-in LED pin as an output:
    pinMode(24, PWM);
}

int i = 0;
void loop() {
  pwmWrite(24,i*100);
  delay(10);
  i += 2;
}
*/

/*
// Analog Read
// Requires port labeled with AIN (from what I can tell)
void setup() {
  pinMode(2, INPUT_ANALOG);
}

int i = 0;
void loop() {
  uint16 val = analogRead(2);
  SerialUSB.print((char) (val >> 8));
  delay(100);
}
*/

/*
// Digital Write
void setup() {
    // Set up the built-in LED pin as an output:
    pinMode(24, OUTPUT);
}

int i = 0;
void loop() {
  digitalWrite(24,HIGH);
  delay(500);
  digitalWrite(24,LOW);
  delay(500);
}
*/

/*
// Digital Read
void setup() {
  // Set up the built-in LED pin as an output:
  pinMode(24, OUTPUT);
  pinMode(23, INPUT);
}

int i = 0;
void loop() {
  int val = digitalRead(23);
  digitalWrite(24,val);
  delay(50);
}
*/
