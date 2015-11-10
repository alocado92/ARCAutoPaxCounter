/*
 * messageToogle.h
 *
 *  Created on: Oct 19, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */

#ifndef MESSAGETOOGLE_H_
#define MESSAGETOOGLE_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//	 	Global Defines
//********************************
#define messageToggleDelay 5    //Switch Delay *WELCOME <--> RETURN TAG*

//********************************
//	 	 Global Varibale
//********************************
extern int seconds;             //Seconds counter

//********************************
//	 	     Function
//********************************
void messageSwitchTimer();      //Timer Initialization
void messageSwitchStopTimer();  //Timer Stop



#endif /* MESSAGETOOGLE_H_ */
