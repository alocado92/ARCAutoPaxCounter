/*
 * buzzer.c
 * Purpose: Control the buzzer, starts the sound and stop 1/4 second later.
 *  Created on: Oct 14, 2015
 *      Author: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "buzzer.h"

//********************************
//			Functions
//********************************
//Initiliaze all ports
void buzzerInit(){
	P2DIR |= BUZZER;						//Pin Initilaization P2.2
	P2OUT &= ~BUZZER;						//Turn off Buzzer
}

//Start Sound and Start Timer
void startSound(){
	P2OUT |= BUZZER;						//Start Sound
	startTimer();							//Start Timer TA0
}

//Start timer for 1/4 seconds
void startTimer(){
	TA2CCR0 = BUZZERTIME;					// 1/4 second
	TA2CTL = TASSEL_1 + MC_1 + TACLR;		//ACLK, Up Mode, TA2 Clear
	TA2CCTL0 |= CCIE;						//Interrupt Enable
}

//Stop the sound of buzzer
void stopSound(){
	P2OUT &= ~BUZZER;
	TA2CTL = TACLR + MC_0;
}

//********************************
//          Interrupt
//********************************
//Timer Service Routine arrive to 1/4 seconds
#pragma vector = TIMER2_A0_VECTOR
__interrupt void TIMER2_A0_ISR(){
	stopSound();
}
