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

//********************************
//		Global Variables
//********************************
uint8_t bluetoothMessage[MESSAGESIZE];

//********************************
//		Local Variables
//********************************
unsigned int counter2 =0;

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

	//LED init
	P1DIR |= LED1;
	P1OUT &= ~LED1;
}

void sendMessage(uint8_t value){
	char messageON[6] = "LED ON";
	char messageOFF[7] = "LED OFF";

	if(value == 0){
		int i;
		for(i=0; i<6; i++){
			while(!(UCA0IFG & UCTXIFG));
			UCA0TXBUF = messageON[i];
		}
	}
	else if (value == 1){
		int i;
		for(i=0; i<7; i++){
			while(!(UCA0IFG & UCTXIFG));
			UCA0TXBUF = messageOFF[i];
		}
	}

}

//********************************
//          Interrupt
//********************************
#pragma vector = USCI_A0_VECTOR
__interrupt void USCI_A0_ISR(void){
	switch(__even_in_range(UCA0IV, 4)){
		case 0: break;
		case 2:
			if (UCA0RXBUF == 't'){
				P1OUT |= LED1;
				sendMessage(0);

			}
			if (UCA0RXBUF == 'f'){
				P1OUT &= ~LED1;
				sendMessage(1);
			}
			break;
		case 4: break;
	}
}

