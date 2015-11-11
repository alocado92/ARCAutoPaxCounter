#include "msp430f5529.h"
/* Purpose: Initialize all modules
 * main.c
 */
//********************************
//          Includes
//********************************
#include "bluetooth.h"
#include "buzzer.h"
#include "diagnosticButton.h"
#include "ledController.h"
#include "messageToggle.h"
#include "photodiode.h"
#include "rfid.h"
#include "arcLogic.h"


//********************************
//          Function
//********************************
void ucsInit();


int main(void) {
    WDTCTL = WDTPW | WDTHOLD;	// Stop watchdog timer
	
    //Modules Initialization
    bluetoothInit();
    buzzerInit();
    diagnosticButtonInit();
    //ledCtrlInit();
    photodiodeInit();
    rfidInit();
    arcLogicInit();

    //Start Message to Toggle
    //messageSwitchTimer();


    __bis_SR_register(LPM0_bits + GIE);
    __no_operation();
}

void ucsInit(){
	//Set DDCO FLL Reference = REFO (32.768kHz)
	UCSCTL3 |= SELREF_2;
}
