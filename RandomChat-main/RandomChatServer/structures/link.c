#include "link.h"
#include <stdlib.h>
#include <stdio.h>


Link* newLink(){
    
    Link* link = malloc(sizeof(Link));
    link->mutex = (pthread_mutex_t) PTHREAD_MUTEX_INITIALIZER;
    link->status = OPEN;
    return link;
}

Link* getLinkOf(User* user,Queue* links){
    
    if(links->size != 0){
        Link* linktmp =  (Link*)links->front->data;
        if(user->id == linktmp->user2->id){
            return linktmp;
        }else{
            struct Node* tmp = links->front;
            while(tmp->next != NULL){
                Link* linktmp = (Link*) tmp->next->data;
                
                if(linktmp->user2->id == user->id){
                    return linktmp;
                }else{
                    tmp = tmp->next;
		}
            }
        }
    }
    return NULL;
}




void destroyLink(Link* link, Queue* links){
    if(link != NULL){
        pthread_mutex_lock(&link->mutex);
        switch (link->status)
        {
        case OPEN:
            link->status = HALF_CLOSED;
            break;
        case HALF_CLOSED:
            link->status = CLOSED;
            free(extract(links, link));
            break;
        case CLOSED:
            printf("[!] Error Link already closed!\n");
        default:
            break;
        }
        pthread_mutex_unlock(&link->mutex);
    }
}

enum STATUS getStatusOfLink(Link* link){
    return link->status;
}
