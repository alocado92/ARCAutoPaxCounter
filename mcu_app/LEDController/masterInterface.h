/*
 * masterInterface.h
 *
 *  Created on: Oct 25, 2015
 *      Author: 802085520
 */

#ifndef MASTERINTERFACE_H_
#define MASTERINTERFACE_H_

//********************************
//      	 Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//      	  Defines
//********************************
#define TRIGGER BIT0

//********************************
//      Public Function
//********************************
void masterInterfaceInit();

//********************************
//      Private Function
//********************************
void decodeCommand(uint8_t command1, uint8_t command2);

#endif /* MASTERINTERFACE_H_ */
