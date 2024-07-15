#include "searcher.h"
#include <unistd.h>
#include <stdio.h>
#include <strings.h>
#include <sys/socket.h>
#include "../server.h"
#include "../auxServer.h"
#include <signal.h>
#include <pthread.h>
#include "link.h"
#include <errno.h>
#include <sys/select.h>


void signal_handler(int signal){
    printf("[Signal] Signal handled ");
    return;
}

Link* searchAvailableUser(User* user, Room* room, char* buff){

    pthread_mutex_lock(&room->mutex);
    struct Node* tmp = room->waitingQueue.front;
    User* validUser = NULL;
    //dovrebbe trovare un utente se non si sono già incontrati
    while(tmp != NULL && validUser == NULL){
        User* currUser = ((User*) tmp->data);
        int okUserFlag = 0; //false
        if(user->lastUserId != currUser->id){
            okUserFlag = 1;
        }
        if(okUserFlag){
            validUser = currUser;
        }
        tmp = tmp->next;
    }

    //se la queue è vuota oppure non ho trovato un utente disponibile allora...
    if(room->waitingQueue.size == 0 || validUser == NULL){
        printf("[Client: %d] Enqueue of the user.\n", user->id);
        enqueue(&room->waitingQueue, user);
        pthread_mutex_unlock(&room->mutex);

        
        switch(waitToUnlock(user,buff, BUFF_LEN, room)){
            case 0: //someone wants to chat
                return getLinkOf(user, &room->links);
            case 1:
                return NULL;
            default:
               printf("[!] Command not found: %s\n",buff);
               return NULL;
        }
    }else{
        //bisogna toglierlo dalla coda e restituirlo.
        printf("[Client: %d] Taking the element from the waiting Queue.\n", user->id);
        User* user2 = (User*) extract(&room->waitingQueue, validUser);
        
        Link* link = newLink();

        pthread_mutex_lock(&link->mutex);
        link->user1 = user;
        link->user2 = user2;
        pthread_mutex_unlock(&link->mutex);

        enqueue(&room->links, link);

        pthread_mutex_unlock(&room->mutex);
        int ret = pthread_kill(user2->tid, SIGUSR1); 
        if(ret != 0){
            perror("[!] Error in pthread_kill\n");
            return NULL;
        };
        return link;
    }

}


int waitToUnlock(User* user, char* buff, int max_len_buff, Room* room){
    printf("[Client: %d] Waiting to be unlocked...\n", user->id);
    ssize_t fdcount;
    
    fd_set rd;
    FD_ZERO(&rd);
    FD_SET(user->socketfd, &rd);

    __sighandler_t  old = signal(SIGUSR1, signal_handler);
    fdcount = select(user->socketfd + 1, &rd, NULL, NULL, NULL); 
    //signal(SIGUSR1, old); //resettiamo il vecchio handler

    if(fdcount <= 0){
        if(errno == EINTR){
            printf("[Client: %d] Someone wants to chat\n", user->id);
            //Allora qualcuno vuole chattare
            return 0;
        }else{
            printf("[Client: %d] Connection closed by client\n", user->id);
            return -1;
        }
    }else{
        int msglen;
        
        if((msglen = read(user->socketfd, buff, BUFF_LEN)) != -1){
            buff[msglen] = '\0';
            printf("[Client: %d] Received: %s.\n",user->id, buff);
            if(strncmp(buff, STOP_WAIT, strlen(STOP_WAIT)) == 0){
                printf("[Client: %d] Stop Waiting!\n", user->id);
                char* stopSearch = "S\n";
                send(user->socketfd, stopSearch, 2, MSG_NOSIGNAL);
                pthread_mutex_lock(&room->mutex);
                extract(&room->waitingQueue, user);
                pthread_mutex_unlock(&room->mutex);
                return 1;//stop waiting
            }else{
                printf("[!] Error arrived in waitToUnlock\n");
            }
        }else{
            perror("[!] Error while reading!\n");
        };
    };

    pthread_mutex_lock(&room->mutex);
    extract(&room->waitingQueue, user);
    pthread_mutex_unlock(&room->mutex);

    return -1;
}


