

#define trigPin 3
#define echoPin 2
#define trigPin2 5
#define echoPin2 4
#define led 12
#define led2 11
static int stable = 0;
static int stable2 = 0;
void setup() {
  Serial.begin (9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(led, OUTPUT);
  pinMode(led2, OUTPUT);
  
}

void loop() {
  long duration, distance;
  long duration2,distance2;
  digitalWrite(trigPin, LOW);  // Added this line
  digitalWrite(trigPin2, LOW);  // Added this line
  delayMicroseconds(2); // Added this line
  digitalWrite(trigPin, HIGH);
  digitalWrite(trigPin2, HIGH);
//  delayMicroseconds(1000); - Removed this line
  delayMicroseconds(10); // Added this line
  digitalWrite(trigPin, LOW);
  digitalWrite(trigPin2, LOW);
  duration = pulseIn(echoPin, HIGH);
  duration2 = pulseIn(echoPin2, HIGH);
  distance = (duration/2) / 29.1;
  distance2 = (duration2/2) / 29.1;
  if(35 < distance && distance < 210){
      make_stable(1);
  }
  else{
      make_stable(0);
   }
  if(35 < distance2 && distance2 < 210){
      make_stable2(1);
  }
  else{
      make_stable2(0);
   }
  if (stable == 4) {  // This is where the LED On/Off happens
    digitalWrite(led,HIGH); // When the Red condition is met, the Green LED should turn off
    Serial.println(" first lot: car is here!");
  digitalWrite(led2,LOW);
}
   if (stable2 == 4) {  // This is where the LED On/Off happens
    digitalWrite(led,HIGH); // When the Red condition is met, the Green LED should turn off
    Serial.println("second lot: car is here!");
  digitalWrite(led2,LOW);
}
  else {
    digitalWrite(led,LOW);
    digitalWrite(led2,HIGH);
  }
    Serial.print("first: ");
    Serial.print(distance);
    Serial.println(" cm");
    Serial.println(stable);
    delayMicroseconds(10);
    Serial.print("second: ");
    Serial.print(distance2);
    Serial.println(" cm");
    Serial.println(stable2);
  delay(500);
  
}
void make_stable(int current){
  Serial.println(current);
  if(stable < 4 && current == 1)
      stable = stable + 1;
  else if(stable < 4 && current == 0 )
      stable = 0;
  else if(stable == 4 && current == 1)
       stable = 4;
  else
       stable = 0; 
}
void make_stable2(int current){
  Serial.println(current);
  if(stable2 < 4 && current == 1)
      stable2 = stable2 + 1;
  else if(stable2 < 4 && current == 0 )
      stable2 = 0;
  else if(stable2 == 4 && current == 1)
       stable2 = 4;
  else
       stable2 = 0; 
}
