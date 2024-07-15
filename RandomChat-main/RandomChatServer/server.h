#include "structures/user.h"
#include "structures/room.h"

#define PORT 8080
#define MSG_BUFF_LEN 1000
#define BUFF_LEN 400
#define MAX_ROOMS 10

void* clientManagerFun(void* param);
int main();