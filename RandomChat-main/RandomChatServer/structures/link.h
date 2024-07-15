#ifndef SERVER_LINK_H
#define SERVER_LINK_H

#include "user.h"
#include "queue.h"

enum STATUS{
    OPEN,
    HALF_CLOSED,
    CLOSED
};

typedef struct {
    User* user1;
    User* user2;
    pthread_mutex_t mutex;
    enum STATUS status;
}Link;

Link* newLink();
Link* getLinkOf(User* user,Queue* links);
void destroyLink(Link* link, Queue* links);
enum STATUS getStatusOfLink(Link* link);

#endif //SERVER_LINK_H