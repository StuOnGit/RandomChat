#ifndef SERVER_USER_H
#define SERVER_USER_H

#include <pthread.h>

#define LEN_LAST_USERS_BUFF 2
#define LEN_NICK 20
#define LEN_IP 16

typedef struct Utente{
    char nickname[LEN_NICK];
    int socketfd;
    int id;
    int lastUserId;
    pthread_t tid;
    
}User; 


#endif // SERVER_USER_H
