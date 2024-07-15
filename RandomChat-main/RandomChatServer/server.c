#include <stdio.h>
#include <sys/socket.h>
#include <unistd.h>
#include <netinet/in.h>
#include <pthread.h>
#include <string.h>
#include <sys/stat.h>
#include <errno.h>
#include <stdlib.h>
#include "server.h"
#include "auxServer.h" 
#include "structures/roomvector.h"

RoomVector* roomVector;

int main(){
  
    //setup del server 
    
    roomVector = newRoomVector(MAX_ROOMS);
    if(loadRoomsFromFile(roomVector, "rooms.txt") < 0){
        perror("[!] Error in loading the Rooms.\n");
        exit(EXIT_FAILURE);
    };

    //inizializzando il server
    int server, client;

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_port = htons(PORT);
    address.sin_addr.s_addr = htonl(INADDR_ANY);


    printf("[Server] Creating the socket for the server..\n");
    server = socket(PF_INET, SOCK_STREAM, 0);
    if(server < 0){
        perror("!Error in server socket:");
        exit(EXIT_FAILURE);
    }
    printf("[Server] Done!\n");

    printf("[Server] Binding the socket..\n");
    if(bind(server, (struct sockaddr*) &address, sizeof(address))< 0){
        perror("[!] Error while binding the socket:");
        exit(EXIT_FAILURE);
    }
    printf("[Server] Done!\n");

    printf("[Server] Setting server into listening mode..\n");
    if(listen(server, 10) < 0){
        perror("[!] Error Listen.\n"); 
        exit(EXIT_FAILURE);
    }
    printf("[Server] Done!\n");

    unsigned int idClient = 0; // è una variabile che può essere utile anche per contare quanti client totali connessi..
    while(1){
        printf("[Server] -Waiting for the client...\n");
        client = accept(server, NULL, NULL);
        if(client < 0){
            perror("[!] Error when server tried to accept the client:");
            exit(EXIT_FAILURE);
        }
        printf("[Server] New Client accepted\n");

        printf("[Server] Creating the thread for the new client..\n");
        User* user = malloc(sizeof(User));
        user->id = ++idClient;
        user->socketfd = client;
        if(pthread_create(&user->tid, NULL, clientManagerFun, user) == -1){
            perror("[!] Error creating the thread:");
            exit(EXIT_FAILURE);
        }; //al posto di client possiamo dargli una struttura
        if(pthread_detach(user->tid) == -1){
            perror("[!] Error detaching the thread:");
            exit(EXIT_FAILURE);
        };
        printf("[Server] Done!\n[Server] Starting the thread..");

    }

    close(server);
    return 0;
}


void* clientManagerFun(void* param){
    char buff[BUFF_LEN];
    ssize_t msglen;

    User* user = (User*) param;
    msglen = recv(user->socketfd, buff, BUFF_LEN, MSG_NOSIGNAL);
    printf("[Client: %d] Received: %s\n", user->id, buff);
    if(msglen <= 0 || buff[0] != 'N'){
        printf("[Client: %d] Connection closed by client (nickname)\n", user->id);
        printf("[Client: %d] Closing connection\n", user->id);
        if(close(user->socketfd) < 0){
            perror("[!] Error in Closing connection.\n");
        }
        free(user);
        pthread_exit(NULL);
    }
    //se va allora avrà inviato il nickname, quindi una stringa..
    buff[msglen] = '\0';

    //TODO: gestire il nickname..
    setNickname(user, buff+1);
    do{
        memset(buff,'\0',BUFF_LEN);
        msglen = read(user->socketfd, buff, BUFF_LEN);
        if(msglen <= 0){
            printf("[Client: %d] Connection closed by client\n", user->id);
            break;
        }
        buff[msglen] = '\0';
        printf("[Client: %d] Received: %s\n", user->id, buff);

    }while(cmdCatch(roomVector, user, buff[0], buff+1));

    //dopo che la condizione è falsa allora chiudere connessioni
    printf("[Client: %d] Closing connection\n", user->id);
    if(close(user->socketfd) < 0){
        printf("[Client: %d] [!] Error: connection closing\n", user->id);
    }
    free(user);
    pthread_exit(NULL);

}

/**
 * Casi possibili:
 * 1) Entra nell'app inserendo il nickname, in quel caso dobbiamo salvarlo!
 * 2) Esce dall'app bruscamente o normalmente
 */




