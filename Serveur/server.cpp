#include<stdio.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<unistd.h>
 
//Server

int main(int argc , char *argv[]) {
    int mysocket, client_socket, size, read_size;
    struct sockaddr_in server, client;
    char msg[1];

    mysocket = socket(AF_INET , SOCK_STREAM , 0);
    if (mysocket == -1) {
        printf("Error : socket creation\n");
	return -1;
    }

    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(6666);
    if(bind(mysocket,(struct sockaddr *)&server , sizeof(server)) < 0) {
      printf("Error : bind failed\n");
      return -1;
    }

    listen(mysocket, 3);
    size = sizeof(struct sockaddr_in);
    client_socket = accept(mysocket, (struct sockaddr *)&client, (socklen_t*)&size);
    if (client_socket < 0) {
      printf("accept failed\n");
      return -1;
    }
     
    recv(client_socket, msg, 1, 0);
    printf("Serveur received %c\n",msg[0]);
    //Send back an answer
    msg[0]=msg[0]+1;
    write(client_socket, msg, 1);

    return 0;
}
