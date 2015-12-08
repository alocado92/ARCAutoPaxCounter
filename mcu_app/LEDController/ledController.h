/*
 * ledController.h
 *
 *  Created on: Oct 20, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */

#ifndef LEDCONTROLLER_H_
#define LEDCONTROLLER_H_

//********************************
//      	 Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>
#include "alphabet.h"
#include "numbers.h"
//********************************
//          Definitions
//********************************
//Ports
#define R1				BIT0
#define G1				BIT1
#define B1				BIT2
#define R2				BIT3
#define G2				BIT4
#define B2				BIT5

#define aAddr			BIT2
#define bAddr			BIT3
#define cAddr			BIT4

#define CLK				BIT2
#define OE				BIT5
#define LAT				BIT6

//Panel Description
#define MAXLET	 		10
#define MAXCOLUMN 		64
#define MAXROW			16

//Letter Description
#define LETWIDTH		5

//Boolean definition
typedef uint8_t bool;
#define FALSE			0x00
#define TRUE			0xFF

//Colors
#define	RED				0x01
#define GREEN			0x02
#define BLUE			0x04
#define MAGENTA			RED | BLUE
#define YELLOW          RED | GREEN
#define CYAN			BLUE | GREEN
#define WHITE			BLUE | GREEN | RED

//********************************
//         Global Variables
//********************************
extern uint8_t dMessage[7];
extern uint8_t ledBuff[16][64];
extern bool clkFlag;

//********************************
//        Public Functions
//********************************
void ledCntrlInit();
void fillMessage(int messageIndex);
void fillBuffer(uint8_t color);
//********************************
//        Private Functions
//********************************
void clkTimerInit();
void updatePort();


#endif /* LEDCONTROLLER_H_ */
