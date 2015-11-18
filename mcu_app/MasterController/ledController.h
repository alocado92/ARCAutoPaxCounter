/*
 * ledController.h
 *
 *  Created on: Oct 18, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */

#ifndef LEDCONTROLLER_H_
#define LEDCONTROLLER_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//          Defines
//********************************
#define TRIGGER		BIT3

#define RED         0        //Red command
#define GREEN       1        //Green command
#define BLUE1       2        //Blue command

#define WELCOME     0        //Welcome command
#define RETURNTAG	1		 //Return tag command
#define TAGENTER	2        //Tag enter command
#define TAGEXIT     3        //Tag exit command
#define SYSTEMOK    4	     //System ok command
#define SYSFAIL		5		 //System Fail command
#define OPEN		6		 //Open command
#define CAPSTONE	7		 //Capstone command

//********************************
//        Public Functions
//********************************
void ledCtrlInit();                     //Led Control Initialization
void assembleCommand(uint8_t message);  //Assemble command

//********************************
//        Private Functions
//********************************
void sendledCommand(uint8_t command1, uint8_t command2);   //Send Command



#endif /* LEDCONTROLLER_H_ */
