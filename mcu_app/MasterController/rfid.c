/*
 * rfid.c
 * Purpose: Capture tag ID from RFID Sensor through UART RX
 *  Created on: Oct 12, 2015
 *      Author: Rosedanny Ortiz
 */
//********************************
//			Includes
//********************************
#include "rfid.h"
#include "arcLogic.h"

//********************************
//      Global Variables
//********************************
uint8_t rfidCard[RFIDSIZE];

//********************************
//      Local Variables
//********************************
unsigned int counter = 0;

//********************************
//          Function
//********************************
//Initialize all ports
void rfidInit(){
    P4SEL = RFID;                           //P4.5 = PM_UCA1RXD/TXD   ----> P3.4
    UCA1CTL1 |= UCSWRST;                    //Put state machine in reset
    UCA1CTL1 |= UCSSEL_2;                   //SMCLK
    UCA1BR0 = 6;                            //1MHz 9600
    UCA1BR1 = 0;                            //1MHz 9600
    UCA1MCTL = UCBRS_0 + UCBRF_13 + UCOS16; //Modln UCBRSx=0, UCBRFx=13
    UCA1CTL1 &= ~UCSWRST;                   //Initilaize USCI state machine
    UCA1IE |= UCRXIE;                       //Enable USCI_A1 RX interrupt
}

//********************************
//          Interrupt
//********************************
//When rfid sensor receive a tag ID
#pragma vector = USCI_A1_VECTOR
__interrupt void USCI_A1_ISR(void){
    uint8_t cardTemp;
    switch(_even_in_range(UCA1IV, 4)){
    	case 0: break;
    	case 2:
    		//RX interrupt
    		cardTemp = UCA1RXBUF;        	//Get message form UART
    		if(counter >= FIRST && counter <= LAST){
    			rfidCard[counter-1] = cardTemp;
    		}
    		counter++;
    		if(counter >= MAXINTERRUPT){
    			counter = 0;
    			rfidOperation();
    		}
    		break;
    case 4:break;
    }

}
