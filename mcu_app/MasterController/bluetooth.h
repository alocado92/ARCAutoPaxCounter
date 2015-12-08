/*
 * bluetooth.h
 *
 *  Created on: Oct 14, 2015
 *      Author: Rosedanny Ortiz
 */

#ifndef BLUETOOTH_H_
#define BLUETOOTH_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//	 	Global Defines
//********************************
#define TX				BIT3
#define RX				BIT4

//********************************
//		Global Variables
//********************************
extern int flagRX;
extern int flagRXDiag;
extern int flagRXStop;

//********************************
//		Defines Functions
//********************************
void bluetoothInit();
void sendMessage(uint8_t value);

#endif /* BLUETOOTH_H_ */
