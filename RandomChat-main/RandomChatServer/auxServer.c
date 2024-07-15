#include "auxServer.h"
#include "server.h"
#include "structures/roomvector.h"
#include "structures/searcher.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <sys/socket.h>
#include <unistd.h>
#include "structures/link.h"
#include <sys/select.h>



//per settare il nickname, a causa di un errore irrisolvibile di telnet, si scriverà all'interno delle parentesi angolari (<NAME>)
void setNickname(User* user, char* nickname){
    //TODO: da finire e definire casi di errore
    char buff[1] = {0};
    strcpy(user->nickname, nickname);
    if(*(user->nickname+strlen(user->nickname)-2) == 13)
        memcpy(user->nickname+strlen(user->nickname)-2,buff, 1);
  
    printf("[Client: %d] Nickname is set to [%s].\n", user->id, user->nickname);
}
    


int sendRoomList(User* user, RoomVector* roomVector, char* buff){
    printf("[Client: %d] Request room list..\n", user->id);
    bzero(buff, BUFF_LEN);
    sprintf(buff, "%d\n", roomVector->size);
    if(send(user->socketfd, buff, strlen(buff), MSG_NOSIGNAL) < 0){
        perror("[!] Error Request room list");
        return 1;
    };

    
    for(int i = 0; i < roomVector->size; ++i){
        bzero(buff, BUFF_LEN);
        Room* room = roomVector->rooms[i];
        sprintf(buff, "%s %d\n", room->name, room->users.size);
        send(user->socketfd, buff, strlen(buff), MSG_NOSIGNAL);
    }
}


int enterInRoom(User* user, RoomVector* roomVector, int index, char* buff){
    
    printf("[Client: %d] Trying to enter in room with index %d.\n", user->id, index);

    Room* room = roomVector->rooms[index];
    if(room == NULL){
        printf("[Client: %d] [!] Error, index not valid.\n", user->id);
        return 1;
    }

    pthread_mutex_lock(&room->mutex);
    enqueue(&room->users, user);
    pthread_mutex_unlock(&room->mutex);

    printf("[Room: %s] users: %d\n", room->name, getQueueSize(&room->users));
    int searching = 1;
    while(searching){
        printf("[Client: %d] Searching for user to chat.\n", user->id);

        Link* link = searchAvailableUser(user, room, buff);
        if(link == NULL){
            break;
        }
        printf("User1 : %s\nUser2: %s\n", link->user1->nickname, link->user2->nickname);
        User* user2 = (link->user1->id == user->id)? link->user2 : link->user1;
        
        printf("[Client: %d] User with nickname [%s] found! Now they can chat.\n", user->id, link->user2->nickname);
        searching = startChatting(user, user2, room, buff, link);
    }

    printf("[Client: %d] Exit from room: %s\n",user->id, room->name);

    pthread_mutex_lock(&room->mutex);
    extract(&room->users, user); //per toglierlo..
    pthread_mutex_unlock(&room->mutex);
}



int startChatting(User* user1, User* user2,Room* room, char* buff, Link* link){
    printf("[Client: %d] Siamo arrivati qua|\n\t Start chatting\n", user1->id);
    
    user1->lastUserId = user2->id;
    user2->lastUserId = user1->id;
    
    memset(buff,'\0',BUFF_LEN);
    sprintf(buff, "[%s]\n",user1->nickname);
    if(send(user2->socketfd, buff, strlen(buff), MSG_NOSIGNAL) < 0){
        perror("[!] Error starting the chat..\n");
    };
    while(1){
        
        memset(buff,'\0',BUFF_LEN);
        ssize_t msglen = read(user1->socketfd, buff, BUFF_LEN);
        if(msglen <= 0){ //error or Exit
            buff[0] = EXIT;
            buff[1] = '\n';
        }

            switch(buff[0]){
                case EXIT:
                    if(getStatusOfLink(link) == OPEN){
                        pthread_mutex_lock(&room->mutex);
                        destroyLink(link, &room->links);
                        pthread_mutex_unlock(&room->mutex);
                        send(user2->socketfd, buff, strlen(buff), MSG_NOSIGNAL);
			send(user1->socketfd, buff, strlen(buff), MSG_NOSIGNAL);
                    }else{
                        pthread_mutex_lock(&room->mutex);
                        destroyLink(link, &room->links);
                        pthread_mutex_unlock(&room->mutex);
                    }
                    return 0;
                case CHANGE_CHATTER:
                    if(getStatusOfLink(link) == OPEN){
                        send(user2->socketfd, buff, strlen(buff), MSG_NOSIGNAL); //da vedere ancora..
                    }else{
                        pthread_mutex_lock(&room->mutex);
                        destroyLink(link, &room->links);
                        pthread_mutex_unlock(&room->mutex);
                    }
                    return 1;
                case MSG:
                    if(getStatusOfLink(link) == OPEN){
                        send(user2->socketfd, buff, strlen(buff), MSG_NOSIGNAL);
                    }else{
                          pthread_mutex_lock(&room->mutex);
                        destroyLink(link, &room->links);
                        pthread_mutex_unlock(&room->mutex);
                    }
                    break;
            }
    }
}

// return 0 stoppa e return 1 fa continuare

int cmdCatch(RoomVector* roomVector, User* user,int cmd,  char* msg){
    switch(cmd){
        case ENTER_ROOM:
            enterInRoom(user, roomVector, atoi(msg), msg);
            break;
        case NICKNAME:
            setNickname(user, msg);
            break;
        case EXIT:
            return 0;
        case PUSH_ROOM_LIST:
            sendRoomList(user, roomVector, msg);
            break;
        default:
            printf("[!] Command not found: %d\n",cmd);
            return 0;
    }
    return 1;
}
    
