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
#define RX				BIT3
#define TX				BIT4
#define LED1 			BIT0

//********************************
//		Global Variables
//********************************
extern uint8_t bluetoothMessage[MESSAGESIZE];

//********************************
//		Defines Functions
//********************************
void bluetoothInit();

#endif /* BLUETOOTH_H_ */
