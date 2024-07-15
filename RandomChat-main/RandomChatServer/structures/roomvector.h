#ifndef ROOMVECTOR_H
#define ROOMVECTOR_H

#include <stdlib.h>
#include "room.h"
#include "user.h"
#include <stdio.h>

typedef struct {
    unsigned int size;
    unsigned int realSize;
    Room** rooms;
} RoomVector;

void initVector(RoomVector*, unsigned int realSize);
void deleteRooms(RoomVector*);

RoomVector* newRoomVector(unsigned int size);
void deleteVector();

int addInto(RoomVector*, Room*);
Room* removeFrom(RoomVector*, int);
int getSize(RoomVector*);
int loadRoomsFromFile(RoomVector* roomVector, char* fp);
//Room* getRoomById(RoomVector*, int);
//Room* removeById(RoomVector*, int);

//RoomVector* seachByName(RoomVector*, char*);

#endif
