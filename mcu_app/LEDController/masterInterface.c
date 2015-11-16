/*
 * masterInterface.c
 *
 *  Created on: Oct 25, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */
//********************************
//          Includes
//********************************
#include "masterInterface.h"
#include "ledController.h"

//********************************
//          Local Variables
//********************************
uint8_t command1;
uint8_t command2;

//********************************
//          Functions
//********************************
void masterInterfaceInit(){
	P1DIR &= ~(BIT2 + BIT3 + BIT4); 	//For message
	P4DIR &= ~(BIT0 + BIT1 + BIT2);		//For color

	P2DIR &= ~TRIGGER;					//Trigger
	P2IES &= ~TRIGGER;
	P2IE |= TRIGGER;
	P2OUT &= ~TRIGGER;
	P2REN |= TRIGGER;
	P2IFG &= ~TRIGGER;

}

/*
 * Decode command structure:
 * [B4][B3][B2]P1 ---> Message
 * [B2][B1][B0]P4 ---> Color
 *
 * Message Decoding:
 * 0 ---> WELCOME
 * 1 ---> RETURN TAG
 * 2 ---> DIAGNOSTIC
 * 3 ---> TAG ENTER
 * 4 ---> TAG EXIT
 * 5 ---> WAIT
 * 6 ---> SYSTEM OK
 *
 * Color Decoding:
 * 0 ---> RED
 * 1 ---> GREEN
 * 2 ---> BLUE
 * 3 ---> MAGNETA (Avoid)
 * 4 ---> YELLOW (Avoid)
 * 5 ---> CYAN (Avoid)
 * 6 ---> WHITE (Not implemented)
 */

void decodeCommand(uint8_t command1, uint8_t command2){
	fillMessage((command1 & 0x1C) >> 2);
	uint8_t color = (command2 & 0x07);

	switch(color){
		case 0: fillBuffer(RED);
				break;
		case 1: fillBuffer(GREEN);
		        break;
		case 2: fillBuffer(BLUE);
		        break;
//      case 3: fillBuffer(MAGNETA);
//              break;
//      case 4: fillBuffer(YELLOW);
//              break;
//      case 5: fillBuffer(CYAN);
//              break;
//      case 6: break;                  //WHITE CASE --- AVOID
	    default: break;
	}
}

//********************************
//          Interrupt
//********************************
#pragma vector = PORT2_VECTOR
__interrupt void PORT2_ISR(void){
    switch(__even_in_range(P2IV,16)){
        case 0: break;
        case 2:
            command1 = P1IN;					//Message
            command2 = P4IN;					//Color
            decodeCommand(command1, command2);
            P2IFG &= ~TRIGGER;
            break;
        case 4: break;
        case 6: break;
        case 8: break;
        case 10: break;
        case 12: break;
        case 14: break;
        case 16: break;
        default: break;
    }
}
