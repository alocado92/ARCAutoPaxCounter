/*
 * diagnosticButton.h
 *
 *  Created on: Oct 29, 2015
 *      Author: Rosedanny Ortiz
 */

#ifndef DIAGNOSTICBUTTON_H_
#define DIAGNOSTICBUTTON_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//	 	Global Defines
//********************************
#define BUTTON BIT0

//********************************
//	 	Global Varibles
//********************************
unsigned int flagDiag;
unsigned int flagDiag2;

//********************************
//	 		Fuctions
//********************************
void diagnosticButtonInit();


#endif /* DIAGNOSTICBUTTON_H_ */
