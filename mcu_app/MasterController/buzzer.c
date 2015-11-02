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
void buzzerInit(){
	P2DIR |= BUZZER;						//Pin Initilaization P2.2
	P2OUT &= ~BUZZER;						//Turn off Buzzer
}

void startSound(){
	P2OUT |= BUZZER;						//Start Sound
	startTimer();							//Start Timer TA0
}

void startTimer(){
	TA2CCR0 = BUZZERTIME;					// 1/4 second
	TA2CTL = TASSEL_1 + MC_1 + TACLR;		//ACLK, Up Mode, TA2 Clear
	TA2CCTL0 |= CCIE;						//Interrupt Enable
}

void stopSound(){
	P2OUT &= ~BUZZER;
	TA2CTL = TACLR + MC_0;
}

//********************************
//          Interrupt
//********************************
//Timer 0 Service Routine
#pragma vector = TIMER2_A0_VECTOR
__interrupt void TIMER2_A0_ISR(){
	stopSound();
}
