/*
 * diagnosticButton.c
 * Purpose: When the button is pressed, the diagnostic protocol begin
 *  Created on: Oct 29, 2015
 *      Author: Rosedanny Ortiz
 */

//********************************
//			Includes
//********************************
#include "diagnosticButton.h"
#include "buzzer.h"
#include "arcLogic.h"
#include "trueOrFalse.h"

//********************************
//			Function
//********************************
//Initialize all ports
void diagnosticButtonInit(){
    P2DIR &= ~BUTTON;                // P2.0 Photodiode
    P2IE |= BUTTON;                  // P2.0 interrupt enabled
    P2IES |= BUTTON;                 // P2.0 Hi/Lo edge
    P2IFG &= ~BUTTON;                // P2.0 IFG cleared
}

//********************************
//          Interrupt
//********************************
//When button is pressed
#pragma vector=PORT2_VECTOR
__interrupt void Port_2(void){
	flagDiag = TRUE;
	flagDiag2 = TRUE;
	startSound();
	diagnosticProtocol();
    P2IFG &= ~BUTTON;                // P2.0 IFG cleared
}

