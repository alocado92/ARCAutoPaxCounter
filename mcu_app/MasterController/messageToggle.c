/*
 * messageToggle.c
 * Purpose: Toogle in the LED display "WELCOME" and "RETURN TAG"
 *  Created on: Oct 19, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "ledController.h"
#include "messageToggle.h"

//********************************
//		Local Variables
//********************************
int seconds = 0;                            //Seconds counter
int returnFlag = 0;
int welcomeFlag = 0;

//********************************
//		Global Variables
//********************************
uint8_t shownMessage = WELCOME;             //Message currently shown

//********************************
//			Function
//********************************
//Start timer, set for 1 second
void messageSwitchTimer(){
    TA1CCR0 = 32768;                        // 1 second
    TA1CTL = TASSEL_1 + MC_1 + TACLR;       // ACLK, Up Mode, TA0 Clear
    TA1CCTL0 |= CCIE;                       // Interrupt Enable
}

//Stop timer
void messageSwitchStopTimer(){
	TA1CTL = TACLR + MC_0;
	seconds = 0;
}

//********************************
//          Interrupt
//********************************
// Timer 1 interrupt: Welcome and Return tag
#pragma vector = TIMER1_A0_VECTOR
__interrupt void TIMER1_A0_ISR(){
    seconds++;
    if(seconds == messageToggleDelay && shownMessage == WELCOME){
        returnFlag = 1;
        welcomeFlag = 0;
    	shownMessage = RETURNTAG;
        assembleCommand(shownMessage);
        seconds = 0;
    }else if(seconds == messageToggleDelay && shownMessage == RETURNTAG){
    	welcomeFlag = 1;
    	returnFlag = 0;
    	shownMessage = WELCOME;
        assembleCommand(shownMessage);
        seconds = 0;
    }else if(seconds > messageToggleDelay){
    	seconds = 0;
    }
}


