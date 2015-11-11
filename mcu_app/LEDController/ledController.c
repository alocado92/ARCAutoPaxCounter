/*
 * ledController.c
 * Purpose:
 *  Created on: Oct 20, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */
//********************************
//          Includes
//********************************
#include "ledController.h"

//********************************
//          Variables
//********************************
//Variable Declaration
uint8_t dMessage[7];
uint8_t ledBuff[16][64];

//Clock interrupt flag
bool clkFlag;

//Buffer Index
int ledBuffrow = 7;
int ledBuffcolumn = 63;

//********************************
//          Functions
//********************************
void ledCntrlInit(){
    P6DIR |= (R1 + G1 + B1 + R2 + G2 + B2);
    P3DIR |= (aAddr + bAddr + cAddr + OE + LAT);
    P1DIR |= CLK;
    P6OUT &= ~(R1 + G1 + B1);
    P3OUT &= ~(aAddr + bAddr + cAddr + OE + LAT);
    P1OUT |= CLK;

    fillMessage(0);
    fillBuffer(BLUE);
    clkTimerInit();
}

/*
 * Clock Timer Initialization
 */
void clkTimerInit(){
    TA0CCTL0 = CCIE;
    TA0CCR0 = 50;
    TA0CTL = TASSEL_2 + MC_1 + TACLR;
}

/*
 * Copy the message from the library
 * to ram
 */
void fillMessage(int messageIndex){
    int i;
    for(i = 0; i < MAXLET; i++){
        dMessage[i] = messages[messageIndex][i];
    }
}

/*
 * Fill the LED Buffer
 * Decode the message to the actual LED Display
 */
void fillBuffer(uint8_t color){
    int mIndex = 0;

    while(mIndex < MAXLET){
        uint8_t let = dMessage[mIndex];
        bool spaceFlag = FALSE;
        bool numberFlag = FALSE;
        unsigned int row;
        unsigned int column;
        unsigned int index;
        unsigned int letLUTindex = 0;

        if(let >= 0x41){
            //Letter is a Character
            index = (unsigned int) let - 0x41;

        }else if(let >= 0x30){
            //Letter is a number
            numberFlag = TRUE;
            index = (unsigned int) let - 0x30;

        }else if(let == 0x00){
            //Space - LED off in block
            spaceFlag = TRUE;

        }else{
            //Error case - Debug (Set break-point)
            __no_operation();
        }

        __no_operation();

        for(row = 0; row < MAXROW; row++){
            for(column = mIndex*(LETWIDTH+1); column < mIndex*(LETWIDTH+1) + LETWIDTH && column < MAXCOLUMN; column++, letLUTindex++){
                if(spaceFlag == FALSE && numberFlag == FALSE){
                    ledBuff[row][column] = alphabet[index][letLUTindex] & color;
                }else if(spaceFlag == FALSE && numberFlag == TRUE){
                    ledBuff[row][column] = numbers[index][letLUTindex] & color;
                }else if(spaceFlag == TRUE){
                    ledBuff[row][column] = 0x00;
                }
            }
        }

        mIndex++;
    }

    //Reset index -- in case message changed while to LED
    ledBuffrow = 7;
    ledBuffcolumn = 63;
}

/**
 * Update Port
 * Takes the next set of led commands and
 * push it through the port on time for the
 * clock signal.
 *
 * This algorithm push the 128 led data before turning
 * on the LED to avoid ghost letters
 */
void updatePort(){

    if(ledBuffcolumn > -1){
        P6OUT = (ledBuff[ledBuffrow + 8][ledBuffcolumn]) | ledBuff[ledBuffrow][ledBuffcolumn] << 3;
        ledBuffcolumn--;
    }else{
        switch(ledBuffrow){
                case 0: P3OUT |= aAddr + bAddr + cAddr;
                        break;
                case 1: P3OUT |= cAddr + bAddr;
                		P3OUT &= ~aAddr;
                        break;
                case 2: P3OUT |= cAddr + aAddr;
                		P3OUT &= ~bAddr;
                        break;
                case 3: P3OUT |= cAddr;
                		P3OUT &= ~(aAddr + bAddr);
                        break;
                case 4: P3OUT |= aAddr + bAddr;
                        P3OUT &= ~cAddr;
                        break;
                case 5: P3OUT |= bAddr;
                        P3OUT &= ~(aAddr + cAddr);
                        break;
                case 6: P3OUT |= aAddr;
                        P3OUT &= ~(bAddr + cAddr);
                        break;
                case 7: P3OUT &= ~(aAddr + bAddr + cAddr);
                        break;
                default:break;
        }

        P3OUT |= OE;
        P3OUT |= LAT;
        P3OUT &= ~LAT;
        P3OUT &= ~OE;

        ledBuffrow--;
        ledBuffcolumn = 63;
        if(ledBuffrow == -1){
            ledBuffrow = 7;
        }
    }
}

//********************************
//          Interrupt
//********************************
#pragma vector = TIMER0_A0_VECTOR
__interrupt void TIMER0_A0_ISR(){
    clkFlag = TRUE;
    __bic_SR_register_on_exit(LPM0_bits);
}
