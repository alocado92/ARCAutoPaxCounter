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
#define	MESSAGESIZE		20
#define TX				BIT3
#define RX				BIT4
#define LED1 			BIT0

//********************************
//		Global Variables
//********************************
extern uint8_t bluetoothMessage[MESSAGESIZE];
extern int flagRX;
extern int flagRXDiag;
extern int flagRXStop;

//********************************
//		Defines Functions
//********************************
void bluetoothInit();
void sendMessage(uint8_t value);

#endif /* BLUETOOTH_H_ */
