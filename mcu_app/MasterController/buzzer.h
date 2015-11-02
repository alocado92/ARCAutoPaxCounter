/*
 * buzzer.h
 * Purpose: Sound buzzer for 1/4 second, and then stop it.
 *  Created on: Oct 14, 2015
 *      Author: Rosedanny Ortiz
 */

#ifndef BUZZER_H_
#define BUZZER_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//	 	Global Defines
//********************************
#define BUZZERTIME	8192		// 1/4 second
#define BUZZER		BIT2		//Pin

//********************************
//	 		Functions
//********************************
void buzzerInit();				//Buzzer Pin Initialization
void startSound();				//Start Sound

//********************************
//	 	Private Functions
//********************************
void startTimer();				//Start Timer
void stopSound();				//Stop Timer


#endif /* BUZZER_H_ */
