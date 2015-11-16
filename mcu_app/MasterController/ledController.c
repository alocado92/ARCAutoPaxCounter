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
const uint8_t colorM[7] = {BLUE1, RED, GREEN, GREEN, RED, GREEN, BLUE1};
int assembleFlag = 0;
int sendLedFlag = 0;

uint8_t command1;
uint8_t command2;
//********************************
//          Functions
//********************************
//Initialize Ports
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
 * 0 ---> WELCOME
 * 1 ---> RETURN TAG
 * 2 ---> DIAGNOSTIC
 * 3 ---> TAG ENTER
 * 4 ---> TAG EXIT
 * 5 ---> WAIT
 * 6 ---> SYSTEM OK
 */
void assembleCommand(uint8_t message){
	assembleFlag = 1;
	sendLedFlag = 0;
	command1 = message;
	command2 = colorM[(unsigned int) message];
	sendledCommand(command1, command2);
}

//Send Command
void sendledCommand(uint8_t command1, uint8_t command2){
	assembleFlag = 0;
	sendLedFlag = 1;
	P3OUT = command1;
    P4OUT = command2;
    P2OUT |= TRIGGER;
    __delay_cycles(100);
    P2OUT &= ~TRIGGER;
}
