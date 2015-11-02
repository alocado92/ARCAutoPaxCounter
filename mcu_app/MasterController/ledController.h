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

#define RED         0x01        //Red command
#define GREEN       0x02        //Green command
#define BLUE1       0x03        //Blue command

#define WELCOME     0x00        //Welcome command
#define RETURNTAG	0x01		//Return tag command
#define DIAGNOSTIC  0x02        //Diagnostic command
#define TAGENTER	0x03        //Tag enter command
#define TAGEXIT     0x04        //Tag exit command
#define JUSTWAIT	0x05        //Wait command
#define SYSTEMOK    0x06	    //System ok command

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
