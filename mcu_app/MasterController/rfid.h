/*
 * rfid.h
 *
 *  Created on: Oct 12, 2015
 *      Author: Rosedanny Ortiz
 */

#ifndef RFID_H_
#define RFID_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//      Global Defines
//********************************
#define RFID            BIT5
#define RFIDSIZE        12
#define MAXINTERRUPT  	16
#define FIRST           1
#define LAST            12

//********************************
//      Global Variables
//********************************
extern uint8_t rfidCard[RFIDSIZE];

//********************************
//      Defines Functions
//********************************
void rfidInit();

#endif /* RFID_H_ */
