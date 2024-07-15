#ifndef ROOM_H
#define ROOM_H

#define LEN_ROOM 50
#include "queue.h"
#include <pthread.h>
#include "user.h"

typedef struct {
    char name[LEN_ROOM];
    int id;
    Queue waitingQueue;
    Queue users;
    Queue links;
    pthread_mutex_t mutex;

}Room;

Room* initRoom(char* name);

#endif