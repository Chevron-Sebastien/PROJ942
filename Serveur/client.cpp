#include<stdio.h>
#include<sys/socket.h>
#include<arpa/inet.h>
#include<unistd.h>
 
int main(int argc , char *argv[]) {
    int mysocket;
    struct sockaddr_in server;
    char msg[1], answer[1];

    if(argc!=2) {
      printf("Usage : %s ip_du_serveur\n",argv[0]);
      return -1;
    }
    
    mysocket = socket(AF_INET , SOCK_STREAM , 0);
    if (mysocket == -1) {
        printf("Error : socket creation\n");
	return -1;
    }
     
    server.sin_addr.s_addr = inet_addr(argv[1]);
    server.sin_family = AF_INET;
    server.sin_port = htons(6666);
 
    if (connect(mysocket, (struct sockaddr *)&server, sizeof(server)) < 0) {
      printf("Error : connection failed\n");
        return -1;
    }

    msg[0]='a';
    if( send(mysocket, msg, 1, 0) < 0) {
      printf("Error : send failed\n");
      return -1;
    }
    
    if( recv(mysocket, answer, 1, 0) < 0) {
      printf("Error : receive failed\n");
      return -1;
    }
    
    printf("Server answers : %c\n", answer[0]);
    close(mysocket);
    return 0;
}
