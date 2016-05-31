/*
* C++要與Hadoop範例
*/

#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include "Client.h"
using namespace std;
int main(int argc, char* argv[]) {

	int port = 2345;
	char ip[40];

	switch (argc) {
	case 1:
		strcpy(ip, "127.0.0.1");
		port = 2345;
		break;
	case 3:
		strcpy(ip, argv[1]);
		port = atoi(argv[2]);
		break;
	default:
		printf("syntax error: server [ip] [port]\n");
		break;
	}

	int ret;
	char buf[MAX_BUF];
	int times=0;
	int pingCount=0;
	Client client(ip, port);//建立連線

	//丟值給server

while(pingCount<1000000){
	pingCount++;
	printf("連接完成?");

	while(client.isValid()){//連接成功後的運算
		printf("連接完成");
		pingCount=0;
	try {
		//ret = client.send("Hello! I'm C++ Client.\n");
		ret = client << "1,2,3,4\n";
		printf("ret=%d\n",ret);
		if (ret <= 0)
			throw ret;

	} catch (int err) {

		if (err == 0)
			printf("Server is offline!\n");
		else if (err == -1)
			printf("Fail to send message to Server!\n");
		break;
	}
	//接值
	printf("接收值");
	try {
		//ret = client.recv(buf);

			ret = client >> buf;
			if (ret <= 0){
				throw ret;
			}
			printf("%s\n", buf);

	} catch (int err) {

		if (err == 0)
			printf("Server is offline!\n");
		else if (err == -1)
			printf("Fail to receive message from Server!\n");
		break;
	}
	printf("第%d次",times++);

	//client.connect(ip, port);
	}
	printf("連線失敗，進行重新連線\n");
	client.create();
	client.reconnect(ip, port);

	//Sleep(1000);
	//
}
	client.close();
	//system("PAUSE");
	return 0;
}
