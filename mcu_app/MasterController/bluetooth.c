/*
 * bluetooth.c
 * Purpose: Send and receive message via bluetooth UART protocol
 *  Created on: Oct 14, 2015
 *      Author: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "bluetooth.h"
#include "trueOrFalse.h"
#include "arcLogic.h"

//********************************
//		Global Variables
//********************************
uint8_t bluetoothMessage[MESSAGESIZE];
int flagRX = FALSE;
int flagRXDiag;
int flagRXStop = FALSE;

unsigned int counterBlue = 0;

//********************************
//			Function
//********************************
void bluetoothInit(){
	P3SEL = (RX + TX);						//P3.3,4 = PM_UCA0RXD/TXD
	UCA0CTL1 |= UCSWRST;					//Put state machine in reset
	UCA0CTL1 |= UCSSEL_2;					//SMCLK
	UCA0BR0 = 6;							//1MHz 9600
	UCA0BR1 = 0;							//1MHz 9600
	UCA0MCTL = UCBRS_0 + UCBRF_13 + UCOS16;	//Modln UCBRSx=0, UCBRFx=13
	UCA0CTL1 &= ~UCSWRST;					//Initilaize USCI state machine
	UCA0IE |= UCRXIE;						//Enable USCI_A0 RX interrupt

}

//********************************
//          Interrupt
//********************************
#pragma vector = USCI_A0_VECTOR
__interrupt void USCI_A0_ISR(void){
	switch(__even_in_range(UCA0IV, 4)){
		case 0: break;
		case 2:
			if (UCA0RXBUF == 'A'){
				flagRX = TRUE;
				counterBlue++;
			}
			else if (UCA0RXBUF == 'D'){
				flagRXDiag = TRUE;
				diagnosticProtocol();
			}
			else if (UCA0RXBUF == 'S'){
				flagRXStop = TRUE;
				arcLogicInit();
			}
			break;
		case 4: break;
	}
}

