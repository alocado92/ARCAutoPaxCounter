/*
 * arcLogic.h
 *
 *  Created on: Nov 5, 2015
 *      Author: Rosedanny Ortiz
 */

#ifndef ARCLOGIC_H_
#define ARCLOGIC_H_
//********************************
//			Includes
//********************************
#include <msp430f5529.h>
#include <stdint.h>

//********************************
//      Global Defines
//********************************
#define NOEXIST		-1
#define EXIST		 1

#define DEAD		0x00
#define LIVE		0x01
#define ERROR		0x02

#define PAXNUM		 5
#define IDLEN        12
#define BLUELEN	     13

//********************************
//      Global Variables
//********************************
extern uint8_t idStatus[PAXNUM];
extern uint8_t id[PAXNUM][IDLEN];

//********************************
//      Defines Functions
//********************************
void arcLogicInit();
void rfidOperation();
int getIndex(uint8_t idPax[IDLEN]);
uint8_t getStatus(int index);
int addID(int index, uint8_t idPax[IDLEN]);
void diagnosticProtocol();


#endif /* ARCLOGIC_H_ */
