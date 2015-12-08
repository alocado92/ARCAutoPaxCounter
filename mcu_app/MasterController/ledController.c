/*
 * ledController.c
 * Purpose: Send commands to the LED Controller of what message should display
 *  Created on: Oct 18, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "ledController.h"

//********************************
//          Variables
//********************************
//Color scheme for each message
//Message: Welcome/color: blue; Message: Return tag/color: red; etc.
const uint8_t colorM[7] = {BLUE1, RED, GREEN, GREEN, RED, GREEN, BLUE1};
int assembleFlag = 0;
int sendLedFlag = 0;

uint8_t command1;
uint8_t command2;

//********************************
//          Functions
//********************************
//Initialize all ports
void ledCtrlInit(){
	//For message
    P3DIR |= BIT0 + BIT1 + BIT2;
    P4DIR |= BIT0 + BIT1 + BIT2;
    //For interrupt
    P2OUT &= TRIGGER;
    P2DIR |= TRIGGER;
}

/*
 * Assemble command depending on message
 * Message Decoding:
 * 0 ---> WELCOME
 * 1 ---> RETURN TAG
 * 2 ---> TAG ENTER
 * 3 ---> TAG EXIT
 * 4 ---> SYSTEM OK
 * 5 ---> SYS FAIL
 * 6 ---> OPEN
 * 7 ---> CAPSTONE
 */
//Create the messange to be display
void assembleCommand(uint8_t message){
	assembleFlag = 1;
	sendLedFlag = 0;
	command1 = message;
	command2 = colorM[(unsigned int) message];
	sendledCommand(command1, command2);
}

//Send message by interrupt to LED controller microprocessor
void sendledCommand(uint8_t command1, uint8_t command2){
	assembleFlag = 0;
	sendLedFlag = 1;
	P3OUT = command1;
    P4OUT = command2;
    P2OUT |= TRIGGER;
    __delay_cycles(100);
    P2OUT &= ~TRIGGER;
}
