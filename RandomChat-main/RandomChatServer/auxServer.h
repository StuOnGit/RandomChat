#ifndef AUX_SERVER_H
#define AUX_SERVER_H
#include "structures/user.h"
#include "structures/roomvector.h"
#include "structures/link.h"

enum commands{
    ENTER_ROOM = 69, // 'E'
    NICKNAME = 78, // 'N'
    EXIT = 88, // 'X'
    MSG = 77, // 'M'
    CHANGE_CHATTER = 67, // 'C'
    PUSH_ROOM_LIST = 82, // 'R'
    STOP_SEARCH = 83 // 'S'
};
int startChatting(User* user1, User* user2,Room* room, char* buff, Link* link);
void setNickname(User* user, char* nickname);
int cmdCatch(RoomVector* roomVector, User* user, int cmd, char* msg);
int enterInRoom(User* user, RoomVector* roomVector, int roomIndex, char* buff);
int sendRoomList(User* user,RoomVector* roomVector,char* msg);
#endif // AUX_SERVER_H