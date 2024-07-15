#include "room.h"
#include <string.h>
#include <stdlib.h>


Room* initRoom(char* name){
    Room* room = malloc(sizeof(Room));
    strcpy(room->name, name);
    newQueue(&room->waitingQueue);
    newQueue(&room->links);
    newQueue(&room->users);
    
    room->mutex = (pthread_mutex_t) PTHREAD_MUTEX_INITIALIZER;
    return room;
}

