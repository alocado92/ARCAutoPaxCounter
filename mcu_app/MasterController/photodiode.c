/*
 * photodiode.c
 * Purpose: Detect when an object obstruct beam, start buzzer sound.
 *  Created on: Oct 14, 2015
 *      Author: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "photodiode.h"
#include "buzzer.h"

//********************************
//			Function
//********************************
void photodiodeInit(){
    P1DIR &= ~CONTAINER;                // P1.4 Photodiode
    P1IE |= CONTAINER;                  // P1.4 interrupt enabled
    P1IES |= CONTAINER;                 // P1.4 Hi/Lo edge
    P1IFG &= ~CONTAINER;                // P1.4 IFG cleared
}

//********************************
//          Interrupt
//********************************
#pragma vector=PORT1_VECTOR
__interrupt void Port_1(void){
	startSound();
    P1IFG &= ~CONTAINER;                // P7.4 IFG cleared
}
