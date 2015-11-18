/*
 * alphabet.c
 * Purpose: This module contains all the alphabet, one letter
 * 			contains 16 rows and 5 columns.
 *  Created on: Oct 20, 2015
 *      Author: Jaen K. De Leon
 *      Edited: Rosedanny Ortiz
 */
//********************************
//          Includes
//********************************
#include "alphabet.h"

//********************************
//          Variables
//********************************
//Built in message
const uint8_t messages[8][10]= {
                    {   'W',    'E',    'L',    'C',    'O',    'M',    'E',   0x00,   0x00,    0x00},
                    {   'R',    'E',    'T',    'U',    'R',    'N',   0x00,    'T',    'A',    'G' },
                    {   'T',    'A',    'G',   0x00,    'E',    'N',    'T',    'E',    'R',    0x00},
                    {   'T',    'A',    'G',   0x00,    'E',    'X',    'I',    'T',   0X00,    0x00},
                    {   'S',    'Y',    'S',    'T',    'E',    'M',    0x00,   'O',    'K',    0x00},
                    {   'S',    'Y',    'S',    'T',   0x00,    'F',    'A',    'I',    'L',    0x00},
                    {  0x00,   0x00,   0x00,    'O',    'P',    'E',    'N',   0x00,   0x00,    0x00},
                    {  0x00,    'C',    'A',    'P',    'S',    'T',    'O',    'N',    'E',    0x00},
    };

const uint8_t alphabet[26][80] = {
    //  <---------ROW0 ---------->  <---------ROW1 --------->   <---------ROW2 --------->   <---------ROW3 --------->   <---------ROW4 --------->   <---------ROW5 --------->   <---------ROW6 --------->   <---------ROW7 --------->   <---------ROW8 --------->   <---------ROW9 --------->   <---------ROW10--------->   <---------ROW11--------->   <---------ROW12--------->   <---------ROW13--------->   <---------ROW14--------->   <---------ROW015-------->
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //A
        {0xFF,0x00,0x00,0x00,0x00,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //B
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF}, //C
        {0xFF,0xFF,0xFF,0xFF,0x00,  0xFF,0x00,0x00,0xFF,0x00,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0xFF,0x00,   0xFF,0xFF,0xFF,0x00,0x00}, //D
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF}, //E
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00}, //F
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //G
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //H
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF}, //I
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0x00,0xFF,0x00,   0xFF,0xFF,0xFF,0xFF,0x00}, //J
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0xFF,0x00,   0xFF,0x00,0xFF,0x00,0x00,   0xFF,0xFF,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0x00,0x00,0x00,   0xFF,0x00,0xFF,0x00,0x00,   0xFF,0x00,0x00,0xFF,0x00,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //K
        {0xFF,0x00,0x00,0x00,0x00,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF}, //L
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0xFF,0x00,0xFF,0xFF,   0xFF,0xFF,0x00,0xFF,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //M
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0xFF,0x00,0x00,0xFF,   0xFF,0xFF,0x00,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0x00,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //N
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //O
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00}, //P
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0x00,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0x00,0xFF,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //Q
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF,   0xFF,0x00,0x00,0xFF,0x00,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //R
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //S
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0x00,0xFF,0x00,0x00}, //T
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //U
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0x00,0xFF,0x00,0xFF,0x00,   0x00,0x00,0xFF,0x00,0x00}, //V
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0x00,0xFF,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //W
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0x00,0xFF,0xFF,0xFF,0x00,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF}, //X
        {0xFF,0x00,0x00,0x00,0xFF,  0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0xFF,0xFF,0xFF,0xFF,0xFF}, //Y
        {0xFF,0xFF,0xFF,0xFF,0xFF,  0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0x00,0xFF,   0x00,0x00,0x00,0xFF,0x00,   0x00,0x00,0xFF,0x00,0x00,   0x00,0xFF,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0x00,0x00,0x00,0x00,   0xFF,0xFF,0xFF,0xFF,0xFF}  //Z
    };


