/*
 * arcLogic.c
 * Purpose:
 *  Created on: Nov 5, 2015
 *      Author: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "arcLogic.h"
#include "rfid.h"
#include "bluetooth.h"
#include "diagnosticButton.h"
#include "trueOrFalse.h"
#include "ledController.h"

//********************************
//      Global Variables
//********************************
uint8_t idStatus[PAXNUM];
uint8_t id[PAXNUM][IDLEN];

//********************************
//      Local Variables
//********************************
unsigned int counterDiagFalse = 0;
unsigned int counterDiagTrue = 0;
unsigned int counterRXTrue = 0;
unsigned int counterRXFalse = 0;
unsigned int flagTagEnterDiag = FALSE;
unsigned int flagTagExitDiag = FALSE;
//********************************
//          Functions
//********************************
//Initialize idStatus abd id buffer
void arcLogicInit(){
	int i;
	int j = 0;
	for(i = 0; i < PAXNUM; i++){
		idStatus[i] = DEAD;
		while(j < IDLEN){
			id[i][j] = 0x00;
			j++;
		}
	}
}

//Add tag ID to buffer and send it through bluetooth to mobile app
void rfidOperation(){
	int index = getIndex(rfidCard);
	int newIndex;
	if(index != NOEXIST && getStatus(index) == LIVE){
		idStatus[index] = DEAD;
		if(flagDiag == FALSE){
			counterDiagFalse++;
			//Send through bluetooth
			UCA0TXBUF = '0';
			int i;
			for(i = 0; i < IDLEN; i++){
				while(!(UCA0IFG & UCTXIFG));
			    UCA0TXBUF = id[index][i];
			}
		}
		else if(flagDiag == TRUE){
			counterDiagTrue++;
			flagTagExitDiag = TRUE;
			//Send through bluetooth
			flagDiag = FALSE;
			UCA0TXBUF = '1';
			int i;
			for(i = 0; i < IDLEN; i++){
				while(!(UCA0IFG & UCTXIFG));
			    UCA0TXBUF = id[index][i];
			}
			diagnosticProtocol();
		}
	}
	else{
		newIndex = addID(index, rfidCard);
		if(flagDiag == FALSE){
			counterDiagFalse++;
			UCA0TXBUF = '0';
			int i;
			//Send through bluetooth
			for(i = 0; i < IDLEN; i++){
				while(!(UCA0IFG & UCTXIFG));
			    UCA0TXBUF = id[newIndex][i];
			}
		}
		else if(flagDiag == TRUE){
			counterDiagTrue++;
			flagTagEnterDiag = TRUE;
			UCA0TXBUF = '1';
			int i;
			//Send through bluetooth
			for(i = 0; i < IDLEN; i++){
				while(!(UCA0IFG & UCTXIFG));
			    UCA0TXBUF = id[newIndex][i];
			}
			diagnosticProtocol();
		}
	}
}

//PAXNUM = 5 for now
int getIndex(uint8_t idPax[IDLEN]){
	int i;
	for(i=0; i < PAXNUM; i++){
		int j;
		int match = 0;
		for(j = 0; j < IDLEN; j++){
			if(idPax[j] == id[i][j]){
				match++;
			}
		}
		if(match == 12){
			return i;
		}
	}
	return NOEXIST;
}

uint8_t getStatus(int index){
	if(index == NOEXIST || index >= PAXNUM){
		return ERROR;
	}
	else{
		return idStatus[index];
	}
}

int addID(int index, uint8_t idPax[IDLEN]){
	int storeIndex;
	if(index == NOEXIST){
		int i;
		for(i=0; i < PAXNUM; i++){
			if(idStatus[i] == DEAD){
				storeIndex = i;
				break;
			}
		}
	}
	else{
		storeIndex = index;
	}
	idStatus[storeIndex] = LIVE;
	int i;
	for(i=0; i < IDLEN; i++){
		id[storeIndex][i] = idPax[i];
	}
	return storeIndex;
}

void diagnosticProtocol(){
	if(flagDiag2 == TRUE){
		flagDiag2 == FALSE;
		messageSwitchStopTimer();
		assembleCommand(TAGENTER);
	}
	else if(flagTagEnterDiag == TRUE){
		flagTagEnterDiag == FALSE;
		assembleCommand(TAGEXIT);
	}
	else if(flagTagExitDiag == TRUE){
		flagTagExitDiag == FALSE;
		if(flagRXDiag == TRUE){
			flagRXDiag = FALSE;
			assembleCommand(SYSTEMOK);
			messageSwitchTimer();
		}
		else if (flagRXDiag == FALSE){
			//assembleCommand(SYSTEMFAIL);
			messageSwitchTimer();
		}
	}
}
