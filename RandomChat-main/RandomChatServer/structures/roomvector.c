#include "roomvector.h"
#include <string.h>
#include "room.h"
#include <sys/stat.h>

void initVector(RoomVector* roomVector, unsigned int realSize){
    roomVector->size = 0;
    roomVector->realSize = realSize;

    roomVector->rooms = malloc(sizeof(Room*) * (roomVector->realSize));

}

RoomVector* newRoomVector(unsigned int size){
    RoomVector* roomVector = malloc(sizeof(RoomVector));
    initVector(roomVector, size);
    return roomVector;
}

void deleteVector(RoomVector* roomVector){
    deleteRooms(roomVector);
    free(roomVector);
}


void deleteRooms(RoomVector* roomVector){
    free(roomVector->rooms);
}

int addInto(RoomVector* roomVector, Room* room){ //ritorna l'id della stanza, altrimenti -1
    if(roomVector->size < roomVector->realSize){
        roomVector->rooms[roomVector->size] = room;
        roomVector->size++;
    }else{
        return -1; //errore
    }
    return room->id; 
}

Room* removeFrom(RoomVector* roomVector, int index){
    if(roomVector->size == 0 || index >= roomVector->size) return NULL;
    Room* ret = roomVector->rooms[index];
    
    while(index < roomVector->size -1){
        roomVector->rooms[index] = roomVector->rooms[index+1];
    }

    free(roomVector->rooms[index]);
    roomVector->size--;

    return ret;
}


int getSize(RoomVector* roomVector){
    return roomVector->size;
}

int loadRoomsFromFile(RoomVector* roomVector, char* filepath){
   
    FILE* file = fopen(filepath, "r");

    char ch;
    char roomName[LEN_ROOM];
    int buffCount = 0;
    
    while((ch = getc(file)) != EOF){
        int comp_value = 10; //10 è \n in ascii

        if(ch != comp_value){ 
            roomName[buffCount] = ch;
            buffCount++;
        }else{
            roomName[buffCount+1] = '\0';
            Room* room = initRoom(roomName);
            if(addInto(roomVector, room) < 0){
                return -1;
            };
            printf("Created Room: %s\n", roomName);
            bzero(&roomName, LEN_ROOM);
            buffCount = 0;
        }   
    }

    
    fclose(file);
}