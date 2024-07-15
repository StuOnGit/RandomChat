#ifndef SEARCHER_H
#define SEARCHER_H

#include "user.h"
#include "room.h"
#include <string.h>
#include "link.h"

#define STOP_WAIT  "StopWait"

Link* searchAvailableUser(User* user, Room* room, char* buff);
int waitToUnlock(User* user, char* buff, int max_len_buff, Room* room);
#endif